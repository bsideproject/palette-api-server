package com.palette.exception.rest;

import com.palette.exception.common.BaseException;
import com.palette.exception.common.ErrorType;
import org.springframework.http.HttpStatus;

public class SocialTypeNotFoundException extends BaseException {

    public SocialTypeNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorType.A003);
    }
}
