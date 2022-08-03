package com.palette.exception;

import com.palette.exception.common.BaseException;
import com.palette.exception.common.ErrorType;
import org.springframework.http.HttpStatus;

public class TokenNotValidException extends BaseException {
    public TokenNotValidException() {
        super(HttpStatus.UNAUTHORIZED, ErrorType.A001);
    }
}
