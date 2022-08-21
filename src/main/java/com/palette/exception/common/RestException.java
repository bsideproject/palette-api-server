package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class RestException extends BaseException {

    public RestException(HttpStatus httpStatus, GlobalErrorType globalErrorType) {
        super(httpStatus, globalErrorType);
    }

}
