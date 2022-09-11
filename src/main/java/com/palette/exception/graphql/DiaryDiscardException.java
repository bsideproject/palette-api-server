package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class DiaryDiscardException extends GraphqlException {

    public DiaryDiscardException() {
        super(HttpStatus.BAD_REQUEST, GlobalErrorType.C001);
    }
}

