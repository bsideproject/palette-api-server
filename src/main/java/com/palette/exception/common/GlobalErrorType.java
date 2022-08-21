package com.palette.exception.common;

import com.netflix.graphql.types.errors.ErrorType;
import com.palette.exception.graphql.DummyException;
import com.palette.exception.rest.SocialTypeNotFoundException;
import com.palette.exception.rest.TokenExpirationException;
import com.palette.exception.rest.TokenNotValidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorType {
    A001("A001", "토큰이 유효하지 않습니다.", null, TokenNotValidException.class),
    A002("A002", "만료된 토큰입니다.", null, TokenExpirationException.class),
    A003("A003", "존재하지 않는 소셜 로그인 방식입니다.", null, SocialTypeNotFoundException.class),
    A004("test", "test", ErrorType.BAD_REQUEST, DummyException.class);


    private final String code;
    private final String message;
    private final ErrorType errorType;
    private final Class<? extends BaseException> classType;
}
