package com.palette.exception.common;

import com.palette.exception.SocialTypeNotFoundException;
import com.palette.exception.TokenExpirationException;
import com.palette.exception.TokenNotValidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    A001("A001", "토큰이 유효하지 않습니다.", TokenNotValidException.class),
    A002("A002", "만료된 토큰입니다.", TokenExpirationException.class),
    A003("A003", "존재하지 않는 소셜 로그인 방식입니다.", SocialTypeNotFoundException.class);


    private final String code;
    private final String message;
    private final Class<? extends BaseException> classType;
}
