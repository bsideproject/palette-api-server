package com.palette.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class GraphqlException extends BaseException {

    protected final GraphqlErrorType graphqlErrorType;

    public GraphqlException(HttpStatus httpStatus, GraphqlErrorType graphqlErrorType) {
        super(httpStatus);
        this.graphqlErrorType = graphqlErrorType;
    }

}
