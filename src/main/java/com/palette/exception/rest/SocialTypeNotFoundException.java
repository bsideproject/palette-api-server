package com.palette.exception.rest;

import com.palette.exception.common.ErrorType;
import com.palette.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class SocialTypeNotFoundException extends RestException {

    public SocialTypeNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorType.A003);
    }
}
