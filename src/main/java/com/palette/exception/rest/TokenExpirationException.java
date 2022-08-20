package com.palette.exception.rest;

import com.palette.exception.common.RestErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class TokenExpirationException extends RestException {

    public TokenExpirationException() {
        super(HttpStatus.UNAUTHORIZED, RestErrorType.A002);
    }
}
