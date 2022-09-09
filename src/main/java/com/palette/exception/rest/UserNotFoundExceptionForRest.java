package com.palette.exception.rest;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class UserNotFoundExceptionForRest extends RestException {
    public UserNotFoundExceptionForRest() {
        super(HttpStatus.NOT_FOUND, GlobalErrorType.U001);
    }

}
