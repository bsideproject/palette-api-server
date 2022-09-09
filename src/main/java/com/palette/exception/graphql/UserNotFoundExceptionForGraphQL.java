package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class UserNotFoundExceptionForGraphQL extends GraphqlException {

    public UserNotFoundExceptionForGraphQL() {
        super(HttpStatus.NOT_FOUND, GlobalErrorType.U001);
    }
}
