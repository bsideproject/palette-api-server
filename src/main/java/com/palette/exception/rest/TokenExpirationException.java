package com.palette.exception.rest;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class TokenExpirationException extends RestException {

    public TokenExpirationException() {
        super(HttpStatus.UNAUTHORIZED, GlobalErrorType.A002);
    }
}
