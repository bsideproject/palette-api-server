package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final ErrorType errorType;

    public BaseException(HttpStatus httpStatus, ErrorType errorType) {
        this.httpStatus = httpStatus;
        this.errorType = errorType;
    }
}
