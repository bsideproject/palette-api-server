package com.palette.exception.graphql;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import org.springframework.http.HttpStatus;

public class AlarmHistoryNotFoundException extends GraphqlException {

    public AlarmHistoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, GlobalErrorType.R001);
    }

}
