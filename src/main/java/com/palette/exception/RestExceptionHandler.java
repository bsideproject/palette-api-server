package com.palette.exception;

import com.palette.exception.common.ExceptionResponse;
import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.RestException;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

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
        GlobalErrorType globalErrorType = e.getGlobalErrorType();
        Sentry.captureMessage(globalErrorType.getCode(), SentryLevel.INFO);
        Sentry.captureMessage(globalErrorType.getMessage(), SentryLevel.INFO);
        Sentry.captureException(e);
        return this.sendException(e.getHttpStatus().value(), e.getGlobalErrorType().getCode(),
            globalErrorType.getMessage());
    }

    /**
     * 알수없는 오류 발생시
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("{} \n", e.getMessage(), e);
        Sentry.captureMessage(e.getMessage(), SentryLevel.WARNING);
        Sentry.captureException(e);
        return this.sendException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "9999",
            HttpStatus.INTERNAL_SERVER_ERROR.name());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ExceptionResponse> handleMaxSizeException(
        MultipartException e) {
        log.error("{} \n", e.getMessage(), e);
        GlobalErrorType f001 = GlobalErrorType.F001;

        Sentry.captureMessage(e.getMessage(), SentryLevel.WARNING);
        Sentry.captureException(e);
        return this.sendException(HttpStatus.BAD_REQUEST.value(), f001.getCode(),
            f001.getMessage());
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
