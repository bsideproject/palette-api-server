package com.palette.infra.jwtTokenProvider;

import com.palette.exception.rest.TokenExpirationException;
import com.palette.exception.rest.TokenNotValidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final JwtAccessTokenInfo jwtAccessTokenInfo;
    private final JwtRefreshTokenInfo jwtRefreshTokenInfo;

    public JwtTokenProvider(JwtAccessTokenInfo jwtAccessTokenInfo,
        JwtRefreshTokenInfo jwtRefreshTokenInfo) {
        this.jwtAccessTokenInfo = jwtAccessTokenInfo;
        this.jwtRefreshTokenInfo = jwtRefreshTokenInfo;
    }

    public String createAccessToken(Long userId, String email) {
        return createToken(userId, email, jwtAccessTokenInfo.getValidityInMilliseconds(),
            jwtAccessTokenInfo.getSecretKey());
    }

    public String createRefreshToken(Long userId, String email) {
        return createToken(userId, email, jwtRefreshTokenInfo.getValidityInMilliseconds(),
            jwtRefreshTokenInfo.getSecretKey());
    }

    public String getEmailFromPayLoad(String token, JwtTokenType jwtTokenType) {
        Claims claims = getClaims(token, jwtTokenType);
        return claims.get("email", String.class);
    }

    public Long getUserIdFromPayLoad(String token, JwtTokenType jwtTokenType) {
        Claims claims = getClaims(token, jwtTokenType);
        return claims.get("id", Long.class);
    }

    public Long getTimeToLiveInMilliseconds(JwtTokenType jwtTokenType) {
        if (jwtTokenType.equals(JwtTokenType.ACCESS_TOKEN)) {
            return jwtAccessTokenInfo.getValidityInMilliseconds();
        }
        return jwtRefreshTokenInfo.getValidityInMilliseconds();
    }

    public String getSecretKey(JwtTokenType jwtTokenType) {
        if (jwtAccessTokenInfo.supports(jwtTokenType)) {
            return jwtAccessTokenInfo.getSecretKey();
        }
        return jwtRefreshTokenInfo.getSecretKey();
    }

    public void validateToken(String token, JwtTokenType jwtTokenType) {
        try {
            Objects.requireNonNull(token);
            Jwts.parser()
                .setSigningKey(getSecretKey(jwtTokenType))
                .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpirationException();
        } catch (NullPointerException | JwtException | IllegalArgumentException e) {
            throw new TokenNotValidException();
        }
    }

    private String createToken(Long userId, String email, Long validityTime, String secretKey) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityTime);

        return Jwts.builder()
            .claim("id", userId)
            .claim("email", email)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    private Claims getClaims(String token, JwtTokenType jwtTokenType) {
        return Jwts
            .parser()
            .setSigningKey(getSecretKey(jwtTokenType))
            .parseClaimsJws(token)
            .getBody();
    }
}
