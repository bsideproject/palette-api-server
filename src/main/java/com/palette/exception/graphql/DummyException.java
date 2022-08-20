package com.palette.exception.graphql;

import com.palette.exception.common.GraphqlErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class DummyException extends GraphqlException {

    public DummyException(HttpStatus httpStatus,
        GraphqlErrorType graphqlErrorType) {
        super(httpStatus, graphqlErrorType);
    }

}
