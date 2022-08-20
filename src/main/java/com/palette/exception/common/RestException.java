package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class RestException extends BaseException {

    protected final RestErrorType restErrorType;

    public RestException(HttpStatus httpStatus, RestErrorType restErrorType) {
        super(httpStatus);
        this.restErrorType = restErrorType;
    }

}
