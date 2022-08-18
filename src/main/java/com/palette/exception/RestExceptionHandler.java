package com.palette.exception;

import com.palette.exception.common.ExceptionResponse;
import com.palette.exception.common.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    /**
     * 비즈니스 에러 발생시
     */
    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<ExceptionResponse> handleRestException(RestException e) {
        log.error("{} \n", e.getMessage(), e);
        return this.sendException(HttpStatus.OK.value(), e.getErrorType().getCode(),
            e.getMessage());
    }

    /**
     * 알수없는 오류 발생시
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("{} \n", e.getMessage(), e);
        return this.sendException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "9999",
            HttpStatus.INTERNAL_SERVER_ERROR.name());
    }

    private ResponseEntity<ExceptionResponse> sendException(int statusCode, String code,
        String message) {
        return ResponseEntity
            .status(statusCode)
            .body(
                ExceptionResponse.builder()
                    .code(code)
                    .message(message)
                    .build()
            );
    }

}
