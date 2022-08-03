package com.palette.user;

import com.palette.infra.jwtTokenProvider.JwtRefreshTokenInfo;
import com.palette.infra.jwtTokenProvider.JwtTokenInfo;
import com.palette.infra.jwtTokenProvider.JwtTokenType;
import com.palette.user.dto.LoginRequest;
import com.palette.user.dto.TokenResponse;
import com.palette.user.service.UserService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class UserController {
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "PTOKEN_REFRESH";
    private final UserService userService;
    private final JwtRefreshTokenInfo jwtRefreshTokenInfo;


    public UserController(UserService userService, JwtRefreshTokenInfo jwtRefreshTokenInfo) {
        this.userService = userService;
        this.jwtRefreshTokenInfo = jwtRefreshTokenInfo;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        TokenResponse tokenResponse = userService.createAccessToken(loginRequest);
        String email = userService.getEmailFromToken(tokenResponse.getAccessToken(), JwtTokenType.ACCESS_TOKEN);
        ResponseCookie responseCookie = createRefreshTokenCookie(email);
        response.addHeader(SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok(tokenResponse);
    }


    private ResponseCookie createRefreshTokenCookie(String email) {
        String refreshToken = userService.createRefreshToken(email);
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .sameSite("Lax")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtRefreshTokenInfo.getValidityInSeconds().intValue())
                .build();
    }
}
