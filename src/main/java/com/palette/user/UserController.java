package com.palette.user;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.event.Events;
import com.palette.event.EventsKind;
import com.palette.event.PushAlarmEvent;
import com.palette.event.PushAlarmEventDto;
import com.palette.exception.rest.DeletedUserException;
import com.palette.exception.rest.UserNotFoundExceptionForRest;
import com.palette.infra.jwtTokenProvider.JwtRefreshTokenInfo;
import com.palette.infra.jwtTokenProvider.JwtTokenProvider;
import com.palette.infra.jwtTokenProvider.JwtTokenType;
import com.palette.user.domain.SocialType;
import com.palette.user.domain.User;
import com.palette.user.fetcher.dto.LoginRequest;
import com.palette.user.fetcher.dto.LoginResponse;
import com.palette.user.fetcher.dto.TokenResponse;
import com.palette.user.repository.UserRepository;
import com.palette.user.service.UserService;

import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class UserController {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "PTOKEN_REFRESH";
    private final UserService userService;

    private static final String BEARER_TYPE = "Bearer";
    private final UserRepository userRepository;
    private final JwtRefreshTokenInfo jwtRefreshTokenInfo;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiaryGroupRepository diaryGroupRepository;

    public UserController(UserService userService, UserRepository userRepository,
        JwtRefreshTokenInfo jwtRefreshTokenInfo, JwtTokenProvider jwtTokenProvider,
        DiaryGroupRepository diaryGroupRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtRefreshTokenInfo = jwtRefreshTokenInfo;
        this.jwtTokenProvider = jwtTokenProvider;
        this.diaryGroupRepository = diaryGroupRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletResponse response) {
        String email = loginRequest.getEmail();
        SocialType loginSocialType = SocialType.of(loginRequest.getSocialType());
        Optional<User> optionalUser = userRepository.findUsersIncDeletedUserByEmail(
            loginRequest.getEmail());
        User user;
        if (optionalUser.isEmpty()) {
            User userInfo = User.builder().email(email)
                .socialTypes(new HashSet<>(Collections.singletonList(loginSocialType))).build();
            user = userRepository.save(userInfo);
        } else {
            user = optionalUser.get();
            if (user.getIsDeleted()) {
                throw new DeletedUserException();
            }
        }
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        ResponseCookie responseCookie = createRefreshTokenCookie(user.getId(), email);
        response.addHeader(SET_COOKIE, responseCookie.toString());
        Boolean isRegistered = user.getAgreeWithTerms();
        return ResponseEntity.ok(
            new LoginResponse(accessToken, isRegistered, user.getSocialTypes()));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
        @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
        HttpServletResponse response) {
        userService.removeRefreshToken(refreshToken);
        expireRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(
        @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = true) String refreshToken,
        HttpServletResponse response) {
        userService.validateRefreshToken(refreshToken);
        String email = userService.getEmailFromToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundExceptionForRest::new);
        TokenResponse tokenResponse = userService.renewAccessToken(user.getId(), email,
            refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @DeleteMapping("/user")
    @Transactional
    public ResponseEntity<Void> deleteUser(
        @RequestHeader(name = "Authorization") String bearerToken,
        @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
        HttpServletResponse response) {
        String token = bearerToken.substring(BEARER_TYPE.length() + 1);
        userService.validateAccessToken(token);
        String email = userService.getEmailFromToken(token, JwtTokenType.ACCESS_TOKEN);
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundExceptionForRest::new);
        userRepository.delete(user);

        //모든 일기 나감처리
        List<DiaryGroup> diaryGroups = diaryGroupRepository.findByUser(user);
        for (DiaryGroup diaryGroup : diaryGroups) {
            diaryGroup.userOut();
        }

        List<Diary> outDiaries = new ArrayList();
        for (DiaryGroup diaryGroup : diaryGroups) {
            outDiaries.add(diaryGroup.getDiary());
        }

        //나간일기에 대한 푸시 알림
        PushAlarmEventDto eventDto = PushAlarmEventDto.builder()
                .eventsKind(EventsKind.OUT_DIARY)
                .outDiaries(outDiaries)
                .user(user)
                .build();

        Events.raise(new PushAlarmEvent(eventDto));

        userService.removeRefreshToken(refreshToken);
        expireRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie createRefreshTokenCookie(Long userId, String email) {
        String refreshToken = userService.createRefreshToken(userId, email);
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .sameSite("Lax")
//                .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(jwtRefreshTokenInfo.getValidityInSeconds().intValue())
            .build();
    }

    private void expireRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
            .sameSite("Lax")
//                .secure(true)
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build();
        response.addHeader(SET_COOKIE, responseCookie.toString());
    }
}
