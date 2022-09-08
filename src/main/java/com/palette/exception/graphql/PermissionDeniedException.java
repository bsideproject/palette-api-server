package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends GraphqlException {
    public PermissionDeniedException() {
        super(HttpStatus.UNAUTHORIZED, GlobalErrorType.A004);
    }
}
