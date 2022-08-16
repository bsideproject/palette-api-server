package com.palette.user.fetcher.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditMyProfileInput {
    private String nickname;
    private Boolean agreeWithTerms;
    private String profileImg;
}
