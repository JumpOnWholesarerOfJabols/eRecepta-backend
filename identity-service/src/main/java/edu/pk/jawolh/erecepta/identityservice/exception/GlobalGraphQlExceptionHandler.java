package edu.pk.jawolh.erecepta.identityservice.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalGraphQlExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handle(UserDoesNotExistException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(InvalidCredentialsException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.UNAUTHORIZED);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(UserAlreadyExistsException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(AccountVerificationException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(CodeDoesNotExistException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(CodeExpiredException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(ValidationException ex, DataFetchingEnvironment env) {
        log.warn("Generic validation failed: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }


    private GraphQLError buildError(Throwable ex, DataFetchingEnvironment env, ErrorType errorType) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .errorType(errorType)
                .extensions(Map.of("errorCode", ex.getClass().getSimpleName()))
                .build();
    }
}
