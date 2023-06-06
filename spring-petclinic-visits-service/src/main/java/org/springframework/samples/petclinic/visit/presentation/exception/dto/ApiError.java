package org.springframework.samples.petclinic.visit.presentation.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;

import java.net.URI;
import java.time.Instant;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError extends AbstractThrowableProblem {

    @NonNull
    URI type;
    @NonNull
    String title;
    @NonNull
    StatusType status;
    @NonNull
    String detail;
    @NonNull
    URI instance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Instant timestamp = Instant.now();

    List<ApiSubError> subErrors;

    @NonNull
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status.getStatusCode());
    }

    public ApiError(@NonNull URI type, @NonNull String title, @NonNull StatusType status, @NonNull String detail,
                    @NonNull URI instance, List<ApiSubError> subErrors) {
        super(type, title, status, detail, instance);
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.subErrors = subErrors;
    }
}
