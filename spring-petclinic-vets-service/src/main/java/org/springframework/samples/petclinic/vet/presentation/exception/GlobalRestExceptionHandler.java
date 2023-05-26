package org.springframework.samples.petclinic.vet.presentation.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.samples.petclinic.vet.domain.shared.DomainError;
import org.springframework.samples.petclinic.vet.domain.shared.DomainException;
import org.springframework.samples.petclinic.vet.presentation.exception.dto.ApiError;
import org.springframework.samples.petclinic.vet.presentation.exception.dto.ApiSubError;
import org.springframework.samples.petclinic.vet.presentation.exception.dto.ApiValidationError;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public final class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
    private final HttpHeaders headers;

    GlobalRestExceptionHandler() {
        super();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log(ex);
        String baseType = getBaseType((ServletWebRequest) request);
        ApiError body = getApiError(baseType,
                DomainError.UNEXPECTED.getCode(),
                ((HttpStatus) status).getReasonPhrase(),
                ex.getLocalizedMessage(),
                (HttpStatus) status);
        return handleExceptionInternal(ex, body, this.headers, status, request);
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(@NonNull HttpMessageNotWritableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {

        log(ex);
        String baseType = getBaseType((ServletWebRequest) request);
        ApiError body = getApiError(baseType,
                DomainError.UNEXPECTED.getCode(),
                ((HttpStatus) status).getReasonPhrase(),
                ex.getLocalizedMessage(),
                (HttpStatus) status);
        return handleExceptionInternal(ex, body, this.headers, status, request);

    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(@NonNull MissingServletRequestParameterException ex,
                                                                          @NonNull HttpHeaders headers,
                                                                          @NonNull HttpStatusCode status,
                                                                          @NonNull WebRequest request) {

        log(ex);
        String baseType = getBaseType((ServletWebRequest) request);
        ApiError body = getApiError(baseType,
                DomainError.VALIDATION.getCode(),
                ((HttpStatus) status).getReasonPhrase(),
                ex.getLocalizedMessage(),
                (HttpStatus) status);
        return handleExceptionInternal(ex, body, this.headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log(ex);
        String baseType = getBaseType((ServletWebRequest) request);
        ApiError body = getApiError(baseType, ex, DomainError.VALIDATION, toErrors(ex.getBindingResult().getFieldErrors()));
        return handleExceptionInternal(ex, body, this.headers, body.getHttpStatus(), request);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<Object> handle(
            ConstraintViolationException ex,
            HandlerMethod handlerMethod,
            WebRequest webRequest,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            HttpMethod httpMethod) {

        log(ex);
        String baseType = getBaseType((ServletWebRequest) webRequest);
        ApiError body = getApiError(baseType, ex, DomainError.VALIDATION, toErrors(ex.getConstraintViolations()));
        return handleExceptionInternal(ex, body, this.headers, body.getHttpStatus(), webRequest);
    }

    @ExceptionHandler(value = TransactionSystemException.class)
    ResponseEntity<Object> handle(
            TransactionSystemException ex,
            HandlerMethod handlerMethod,
            WebRequest webRequest,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            HttpMethod httpMethod) {

        log(ex);

        if (ex.getRootCause() != null)
            if (ex.getRootCause() instanceof ConstraintViolationException)
                return handle((ConstraintViolationException) ex.getRootCause(), handlerMethod, webRequest, servletRequest, servletResponse, httpMethod);

        return handleAnyException(ex, handlerMethod, webRequest, servletRequest, servletResponse, httpMethod);
    }

    @ExceptionHandler(value = DomainException.class)
    ResponseEntity<Object> handleApplicationException(
            DomainException domainException,
            HandlerMethod handlerMethod,
            WebRequest webRequest,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            HttpMethod httpMethod) {

        log(domainException);
        String baseType = getBaseType((ServletWebRequest) webRequest);
        ApiError apiError = getApiError(baseType, domainException, domainException.getDomainError());
        return handleExceptionInternal(
                domainException,
                apiError,
                this.headers,
                apiError.getHttpStatus(),
                webRequest);
    }

    @ExceptionHandler(value = Exception.class)
    private ResponseEntity<Object> handleAnyException(
            Exception ex,
            HandlerMethod handlerMethod,
            WebRequest webRequest,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            HttpMethod httpMethod) {

        log(ex);
        String baseType = getBaseType((ServletWebRequest) webRequest);
        ApiError body = getApiError(baseType, ex, DomainError.UNEXPECTED);
        return handleExceptionInternal(
                ex,
                body,
                this.headers,
                body.getHttpStatus(),
                webRequest);
    }

    private void log(Exception ex) {
        String className = ex.getClass().getSimpleName();
        if (log.isDebugEnabled()) {
            log.warn("{} occurs with message: {}", className, ex.getMessage(), ex);
        } else {
            log.warn("{} occurs with message: {}", className, ex.getMessage());
        }
    }

    private ApiError getApiError(String baseType, Exception ex, DomainError domainError) {
        return getApiError(baseType, ex, domainError, null);
    }

    private ApiError getApiError(String baseType, Exception ex,
                                 DomainError domainError,
                                 @Nullable List<ApiSubError> apiSubErrors) {
        ApiError.ApiErrorBuilder error = ApiError.builder()
                .type(URI.create(baseType + domainError.getCode()))
                .title(domainError.getMessage())
                .detail(ex.getLocalizedMessage())
                .instance(domainError.getCode())
                .subErrors(apiSubErrors);


        switch (domainError) {
            case RESOURCE_NOT_FOUND -> error.status(new HttpStatusAdapter(HttpStatus.NOT_FOUND));
            case VALIDATION -> error.status(new HttpStatusAdapter(HttpStatus.BAD_REQUEST));
            default -> error.status(new HttpStatusAdapter(HttpStatus.INTERNAL_SERVER_ERROR));
        }


        return error.build();
    }

    private ApiError getApiError(String baseType,
                                 URI code,
                                 String reason,
                                 String message,
                                 HttpStatus status) {
        return ApiError.builder()
                .type(URI.create(baseType + code))
                .title(reason)
                .detail(message)
                .instance(code)
                .status(new HttpStatusAdapter(status)).build();
    }

    private String getBaseType(ServletWebRequest request) {
        String fullUrl = request.getRequest().getRequestURL().toString();
        return fullUrl.substring(0, StringUtils.ordinalIndexOf(fullUrl, "/", 3));
    }

    private List<ApiSubError> toErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(this::toError).toList();
    }

    private List<ApiSubError> toErrors(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(this::toError).toList();
    }

    private ApiSubError toError(ConstraintViolation<?> cv) {
        return new ApiValidationError(
                cv.getRootBeanClass().getSimpleName(),
                ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
                cv.getInvalidValue(),
                cv.getMessage());
    }

    private ApiSubError toError(FieldError fieldError) {
        return new ApiValidationError(
                fieldError.getObjectName(),
                fieldError.getField(),
                Objects.toString(fieldError.getRejectedValue(), null),
                fieldError.getDefaultMessage());
    }

}
