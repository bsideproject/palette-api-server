package com.palette.exception.common;

import org.springframework.http.HttpStatus;

public abstract class GraphqlException extends BaseException {

    public GraphqlException(HttpStatus httpStatus, ErrorType errorType) {
        super(httpStatus, errorType);
    }

}
