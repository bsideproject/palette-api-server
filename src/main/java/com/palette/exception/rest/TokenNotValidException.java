package com.palette.exception.rest;

import com.palette.exception.common.RestErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class TokenNotValidException extends RestException {

    public TokenNotValidException() {
        super(HttpStatus.UNAUTHORIZED, RestErrorType.A001);
    }
}
