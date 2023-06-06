package org.springframework.samples.petclinic.customer.application.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

@Valid
public record OwnerCommand(
        @NotBlank
        JsonNullable<String> firstName,
        JsonNullable<String> lastName,
        JsonNullable<String> address,

        @NotBlank
        JsonNullable<String> city,
        JsonNullable<String> telephone) {
}
