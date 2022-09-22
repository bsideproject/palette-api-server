package com.palette.user.fetcher.dto;

import com.palette.user.domain.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends TokenResponse {
    private Boolean isRegistered;
    private Set<SocialType> socialTypes;

    public LoginResponse (String accessToken, Boolean isRegistered, Set<SocialType> socialTypes) {
        super(accessToken);
        this.isRegistered = isRegistered;
        this.socialTypes = socialTypes;
    }
}
