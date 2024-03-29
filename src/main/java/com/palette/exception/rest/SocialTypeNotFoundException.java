package com.palette.exception.rest;

import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class SocialTypeNotFoundException extends RestException {

    public SocialTypeNotFoundException() {
        super(HttpStatus.NOT_FOUND, GlobalErrorType.A003);
    }
}
