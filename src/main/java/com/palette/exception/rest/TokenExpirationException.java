package com.palette.exception.rest;

import com.palette.exception.common.BaseException;
import com.palette.exception.common.ErrorType;
import org.springframework.http.HttpStatus;

public class TokenExpirationException extends BaseException {

    public TokenExpirationException() {
        super(HttpStatus.UNAUTHORIZED, ErrorType.A002);
    }
}
