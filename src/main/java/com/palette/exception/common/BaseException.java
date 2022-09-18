package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

    protected final HttpStatus httpStatus;
    protected final GlobalErrorType globalErrorType;

    public BaseException(HttpStatus httpStatus, GlobalErrorType globalErrorType) {
        this.httpStatus = httpStatus;
        this.globalErrorType = globalErrorType;
    }

}
