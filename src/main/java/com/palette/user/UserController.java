package com.palette.user;

import com.palette.infra.jwtTokenProvider.JwtRefreshTokenInfo;
import com.palette.infra.jwtTokenProvider.JwtTokenType;
import com.palette.user.domain.User;
import com.palette.user.fetcher.dto.LoginRequest;
import com.palette.user.fetcher.dto.LoginResponse;
import com.palette.user.fetcher.dto.TokenResponse;
import com.palette.user.repository.UserRepository;
import com.palette.user.service.UserService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "PTOKEN_REFRESH";
    private final UserService userService;

    private final UserRepository userRepository;
    private final JwtRefreshTokenInfo jwtRefreshTokenInfo;


    public UserController(UserService userService,UserRepository userRepository, JwtRefreshTokenInfo jwtRefreshTokenInfo) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtRefreshTokenInfo = jwtRefreshTokenInfo;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        String accessToken = userService.createAccessToken(loginRequest);
        String email = userService.getEmailFromToken(accessToken, JwtTokenType.ACCESS_TOKEN);
        ResponseCookie responseCookie = createRefreshTokenCookie(email);
        response.addHeader(SET_COOKIE, responseCookie.toString());
        Optional<User> user = userRepository.findByEmail(email);
        Boolean isRegistered = user.isPresent() ? user.get().getAgreeWithTerms() : false;
        return ResponseEntity.ok(new LoginResponse(accessToken, isRegistered, user.get().getSocialTypes()));
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
        TokenResponse tokenResponse = userService.renewAccessToken(email, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    private ResponseCookie createRefreshTokenCookie(String email) {
        String refreshToken = userService.createRefreshToken(email);
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
