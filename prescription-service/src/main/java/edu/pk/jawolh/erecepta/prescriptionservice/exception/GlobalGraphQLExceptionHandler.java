package edu.pk.jawolh.erecepta.prescriptionservice.exception;

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
public class GlobalGraphQLExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handle(UnauthorizedAccessException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.FORBIDDEN);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(InvalidVisitStateException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(AllergyConflictException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("conflictingIngredients", ex.getConflictingIngredients());
        return buildError(ex, env, ErrorType.BAD_REQUEST, extensions);
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(PrescriptionNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Exception: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
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
