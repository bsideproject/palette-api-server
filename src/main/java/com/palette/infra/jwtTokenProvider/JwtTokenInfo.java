package com.palette.infra.jwtTokenProvider;

public interface JwtTokenInfo {
    String getSecretKey();
    Long getValidityInMilliseconds();
    Long getValidityInSeconds();
    boolean supports(JwtTokenType jwtTokenType);
}
