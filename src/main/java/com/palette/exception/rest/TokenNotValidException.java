package com.palette.exception.rest;

import com.palette.exception.common.ErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class TokenNotValidException extends RestException {

    public TokenNotValidException() {
        super(HttpStatus.UNAUTHORIZED, ErrorType.A001);
    }
}
