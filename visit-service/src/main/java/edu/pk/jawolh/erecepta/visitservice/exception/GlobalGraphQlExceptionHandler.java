package edu.pk.jawolh.erecepta.visitservice.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalGraphQlExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handle(DoctorNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(VisitNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(WeeklyAvailabilityNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(AvailabilityExceptionNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(DoctorSpecializationNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(DoctorSpecializationAlreadyExistsException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(InvalidTimeConstraintException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(InvalidSpecializationException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(IllegalArgumentException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    private GraphQLError buildError(Throwable ex, DataFetchingEnvironment env, ErrorType errorType) {
        return buildError(ex, env, errorType, Collections.emptyMap());
    }

    private GraphQLError buildError(Throwable ex, DataFetchingEnvironment env, ErrorType errorType, Map<String, Object> additionalExtensions) {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("errorCode", ex.getClass().getSimpleName());
        extensions.putAll(additionalExtensions);

        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .errorType(errorType)
                .extensions(extensions)
                .build();
    }
}
