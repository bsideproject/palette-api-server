package com.palette.exception.common;

import com.netflix.graphql.types.errors.ErrorType;
import com.palette.exception.graphql.DummyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GraphqlErrorType {
    A001("test", "test", ErrorType.BAD_REQUEST, DummyException.class);

    private final String code;
    private final String message;
    private final ErrorType errorType;
    private final Class<? extends GraphqlException> classType;
}
