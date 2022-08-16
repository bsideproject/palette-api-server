package com.palette.user.fetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends TokenResponse {
    private Boolean isRegistered;

    public LoginResponse (String accessToken, Boolean isRegistered) {
        super(accessToken);
        this.isRegistered = isRegistered;
    }
}
