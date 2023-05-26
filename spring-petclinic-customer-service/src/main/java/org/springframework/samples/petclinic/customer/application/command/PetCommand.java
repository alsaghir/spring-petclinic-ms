package org.springframework.samples.petclinic.customer.application.command;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PetCommand(Integer id,
                         @JsonFormat(pattern = "yyyy-MM-dd")
                         @NotNull LocalDate birthDate,
                         @NotNull String name,
                         @NotNull Integer typeId) {
}
