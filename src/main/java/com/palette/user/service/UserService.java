package com.palette.user.service;

import com.palette.exception.TokenNotValidException;
import com.palette.infra.jwtTokenProvider.JwtTokenProvider;
import com.palette.infra.jwtTokenProvider.JwtTokenType;
import com.palette.token.domain.RefreshToken;
import com.palette.token.repository.RefreshTokenRepository;
import com.palette.user.domain.SocialType;
import com.palette.user.domain.User;
import com.palette.user.dto.LoginRequest;
import com.palette.user.dto.TokenResponse;
import com.palette.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String getEmailFromToken(String accessToken, JwtTokenType tokenType) {
        return jwtTokenProvider.getEmailFromPayLoad(accessToken, tokenType);
    }

    public TokenResponse createAccessToken(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        SocialType socialLoginType = SocialType.of(loginRequest.getSocialType());
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isPresent()) {
            if (user.get().addSocialType(socialLoginType)) {
                userRepository.save(user.get());
            }
            return TokenResponse.of(jwtTokenProvider.createAccessToken(user.get().getEmail()));
        }

        User userInfo = User.builder().email(email).socialTypes(List.of(socialLoginType)).build();
        User savedUser = userRepository.save(userInfo);
        return TokenResponse.of(jwtTokenProvider.createAccessToken(savedUser.getEmail()));
    }

    public void validateAccessToken(String accessToken) {
        jwtTokenProvider.validateToken(accessToken, JwtTokenType.ACCESS_TOKEN);
    }

    public void validateRefreshToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
        validateStoredRefreshToken(refreshToken);
    }

    public TokenResponse renewAccessToken(String email, String refreshToken) {
        validateAccessToken(refreshToken);
        return TokenResponse.of(jwtTokenProvider.createAccessToken(email));
    }

    public String createRefreshToken(String email) {
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(email);
        Long timeToLive = jwtTokenProvider.getTimeToLiveInMilliseconds(JwtTokenType.REFRESH_TOKEN);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(new RefreshToken(email, refreshTokenValue, new Date(new Date().getTime() + timeToLive)));
        return savedRefreshToken.getTokenValue();
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        validateRefreshToken(refreshToken);
        String email = getEmailFromToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
        refreshTokenRepository.deleteByEmailAndTokenValue(email, refreshToken);
    }

    private void validateStoredRefreshToken(String refreshToken) {
        RefreshToken storedRefreshToken = refreshTokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(TokenNotValidException::new);
        String email = jwtTokenProvider.getEmailFromPayLoad(refreshToken, JwtTokenType.REFRESH_TOKEN);
        if (!storedRefreshToken.getEmail().equals(email)) {
            throw new TokenNotValidException();
        }
    }
}
