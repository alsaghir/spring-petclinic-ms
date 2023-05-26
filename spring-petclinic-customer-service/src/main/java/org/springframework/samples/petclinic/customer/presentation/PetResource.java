package org.springframework.samples.petclinic.customer.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customer.application.PetService;
import org.springframework.samples.petclinic.customer.application.command.PetCommand;
import org.springframework.samples.petclinic.customer.application.data.PetData;
import org.springframework.samples.petclinic.customer.application.data.PetTypeData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
public class PetResource {

    private final PetService petService;

    @GetMapping("/petTypes")
    public List<PetTypeData> getPetTypes() {
        return petService.findPetTypes();
    }

    @PostMapping("/owners/{ownerId}/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public PetData processCreationForm(@PathVariable("ownerId") @Min(1) Integer ownerId,
            @Valid @RequestBody PetCommand petCommand) {

        return petService.createPet(ownerId, petCommand);
    }

    @GetMapping("owners/*/pets/{petId}")
    public PetData findPet(@PathVariable("petId") int petId) {
        return petService.getPetById(petId);
    }


}