package com.palette.user.fetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {

    protected String accessToken;

    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
