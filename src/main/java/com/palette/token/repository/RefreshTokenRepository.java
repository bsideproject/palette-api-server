package com.palette.token.repository;

import com.palette.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);
    Optional<RefreshToken> findByTokenValue(String tokenValue);
    void deleteByEmailAndTokenValue(String email, String tokenValue);
}
