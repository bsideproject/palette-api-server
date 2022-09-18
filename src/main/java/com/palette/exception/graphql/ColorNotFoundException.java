package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class ColorNotFoundException extends GraphqlException {

    public ColorNotFoundException() {
        super(HttpStatus.BAD_REQUEST, GlobalErrorType.C001);
    }
}




