package com.palette.exception.rest;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class DeletedUserException extends RestException {
    public DeletedUserException() {
        super(HttpStatus.UNAUTHORIZED, GlobalErrorType.A005);
    }
}
