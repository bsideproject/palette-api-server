package com.palette.exception;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import com.palette.exception.common.GlobalErrorType;
import com.palette.exception.common.GraphqlException;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
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
            GlobalErrorType graphqlErrorType = exception.getGlobalErrorType();
            HttpStatus httpStatus = exception.getHttpStatus();
            log.info("errorType: {}, message: {}, httpStatus {}", graphqlErrorType,
                graphqlErrorType.getMessage(),
                httpStatus);

            // 필요시 에러 추적을 위한 인자값 추가
//            Map<String, Object> debugInfo = new HashMap<>();
//            debugInfo.put("somefield", "somevalue");

            GraphQLError graphqlError = TypedGraphQLError.newInternalErrorBuilder()
                .errorType(graphqlErrorType.getErrorType())
                .message(graphqlErrorType.getMessage())
                //.debugInfo(debugInfo)
                .path(handlerParameters.getPath()).build();

            DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build();

            Sentry.captureMessage(graphqlErrorType.getCode(), SentryLevel.INFO);
            Sentry.captureMessage(graphqlErrorType.getMessage(), SentryLevel.INFO);
            Sentry.captureException(exception);
            return CompletableFuture.completedFuture(result);
        } else {
            return DataFetcherExceptionHandler.super.handleException(handlerParameters);
        }
    }

}
