package org.springframework.samples.petclinic.customer.application.data;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public record PetData(Integer id,
                      String name,
                      String ownerName,
                      @DateTimeFormat(pattern = "yyyy-MM-dd")
                      LocalDate birthDate,
                      PetTypeData type) {
}
