package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends GraphqlException {

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, GlobalErrorType.U001);
    }
}
