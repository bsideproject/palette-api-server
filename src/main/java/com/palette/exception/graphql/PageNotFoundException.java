package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class PageNotFoundException extends GraphqlException {
    public PageNotFoundException() { super(HttpStatus.NOT_FOUND, GlobalErrorType.D002);}
}
