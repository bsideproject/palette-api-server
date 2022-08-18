package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

    protected final HttpStatus httpStatus;
    protected final ErrorType errorType;

    public BaseException(HttpStatus httpStatus, ErrorType errorType) {
        this.httpStatus = httpStatus;
        this.errorType = errorType;
    }

}
