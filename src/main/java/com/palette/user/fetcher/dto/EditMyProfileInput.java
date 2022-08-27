package com.palette.user.fetcher.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class EditMyProfileInput {
    private String nickname;
    private Boolean agreeWithTerms;
    private String profileImg;
    private Set<String> socialTypes;
    private Boolean pushEnabled;
}
