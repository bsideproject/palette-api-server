package com.palette.exception.common;

import org.springframework.http.HttpStatus;

public abstract class RestException extends BaseException {

    public RestException(HttpStatus httpStatus, ErrorType errorType) {
        super(httpStatus, errorType);
    }

}
