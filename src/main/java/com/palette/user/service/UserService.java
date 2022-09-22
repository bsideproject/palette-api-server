package com.palette.user.service;

import com.palette.exception.rest.TokenNotValidException;
import com.palette.infra.jwtTokenProvider.JwtTokenProvider;
import com.palette.infra.jwtTokenProvider.JwtTokenType;
import com.palette.token.domain.RefreshToken;
import com.palette.token.repository.RefreshTokenRepository;
import com.palette.user.fetcher.dto.TokenResponse;
import com.palette.user.repository.UserRepository;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String getEmailFromToken(String accessToken, JwtTokenType tokenType) {
        return jwtTokenProvider.getEmailFromPayLoad(accessToken, tokenType);
    }

    public void validateAccessToken(String accessToken) {
        jwtTokenProvider.validateToken(accessToken, JwtTokenType.ACCESS_TOKEN);
    }

    public void validateRefreshToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
        validateStoredRefreshToken(refreshToken);
    }

    public TokenResponse renewAccessToken(Long userId, String email, String refreshToken) {
        return TokenResponse.of(jwtTokenProvider.createAccessToken(userId, email));
    }

    public String createRefreshToken(Long userId, String email) {
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(userId, email);
        Long timeToLive = jwtTokenProvider.getTimeToLiveInMilliseconds(JwtTokenType.REFRESH_TOKEN);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(
            new RefreshToken(email, refreshTokenValue,
                new Date(new Date().getTime() + timeToLive)));
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
        String email = jwtTokenProvider.getEmailFromPayLoad(refreshToken,
            JwtTokenType.REFRESH_TOKEN);
        if (!storedRefreshToken.getEmail().equals(email)) {
            throw new TokenNotValidException();
        }
    }
}
