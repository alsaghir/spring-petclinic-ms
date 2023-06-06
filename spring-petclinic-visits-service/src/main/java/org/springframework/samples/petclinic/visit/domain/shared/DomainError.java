package org.springframework.samples.petclinic.visit.domain.shared;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum DomainError {
    UNEXPECTED(1, "Unexpected error. Please contact administrator."),
    RESOURCE_NOT_FOUND(2, "Resource not found"),

    VALIDATION(3, "Consuming validation error"),
    ;

    private static final Map<DomainError, URI> errorTypes;


    static {
        Set<Integer> checkedErrorCode = new HashSet<>();
        errorTypes = new HashMap<>();
        for (DomainError error : DomainError.values()) {
            // Validate uniqueness of error numbers
            if (checkedErrorCode.contains(error.errorCode))
                throw new IllegalStateException(
                        "Duplicated Error Code Id" + error,
                        new DomainException("error code should not be repeated", DomainError.UNEXPECTED));
            checkedErrorCode.add(error.errorCode);

            // Keep formatted error types
            errorTypes.put(error, URI.create(String.format("/probs/e%05d", error.errorCode)));
        }
    }

    private final int errorCode;
    private final String errorMessage;

    DomainError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public URI getCode() {
        return errorTypes.get(this);
    }

    public String getMessage(String... args) {
        return String.format(errorMessage, (Object[]) args);
    }

    @Override
    public String toString() {
        return "errorCode="
                + errorCode
                + ", errorMessage='"
                + getMessage()
                + '\'';
    }
}
