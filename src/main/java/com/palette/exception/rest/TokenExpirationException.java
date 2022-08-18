package com.palette.exception.rest;

import com.palette.exception.common.ErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class TokenExpirationException extends RestException {

    public TokenExpirationException() {
        super(HttpStatus.UNAUTHORIZED, ErrorType.A002);
    }
}
