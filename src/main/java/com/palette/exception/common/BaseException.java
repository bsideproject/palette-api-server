package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

    protected final HttpStatus httpStatus;

    public BaseException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

}
