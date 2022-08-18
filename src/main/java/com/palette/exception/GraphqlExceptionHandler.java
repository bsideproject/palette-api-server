package com.palette.exception;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import com.palette.exception.common.ErrorType;
import com.palette.exception.common.GraphqlException;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GraphqlExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
        DataFetcherExceptionHandlerParameters handlerParameters) {
        if (handlerParameters.getException() instanceof GraphqlException) {
            GraphqlException exception = (GraphqlException) handlerParameters.getException();
            ErrorType errorType = exception.getErrorType();
            HttpStatus httpStatus = exception.getHttpStatus();
            log.info("errorType: {}, message: {}, httpStatus {}", errorType, errorType.getMessage(),
                httpStatus);

            // 필요시 에러 추적을 위한 인자값 추가
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("somefield", "somevalue");

            GraphQLError graphqlError = TypedGraphQLError.newInternalErrorBuilder()
                .message(errorType.getMessage())
                .debugInfo(debugInfo)
                .path(handlerParameters.getPath()).build();

            DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build();

            return CompletableFuture.completedFuture(result);
        } else {
            return DataFetcherExceptionHandler.super.handleException(handlerParameters);
        }
    }

}
