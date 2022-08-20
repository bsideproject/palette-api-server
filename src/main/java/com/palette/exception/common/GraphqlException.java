package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class GraphqlException extends BaseException {

    public GraphqlException(HttpStatus httpStatus, GlobalErrorType graphqlErrorType) {
        super(httpStatus, graphqlErrorType);
    }

}
