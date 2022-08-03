package com.palette.user.domain;

import com.palette.exception.SocialTypeNotFoundException;

import java.util.Arrays;

public enum SocialType {
    KAKAO, NAVER;

    public static SocialType of(String input) {
        return Arrays.stream(values())
                .filter(socialType -> socialType.name().equals(input.toUpperCase()))
                .findFirst()
                .orElseThrow(SocialTypeNotFoundException::new);
    }
}
