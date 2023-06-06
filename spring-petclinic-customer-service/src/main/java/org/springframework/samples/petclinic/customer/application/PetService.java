package org.springframework.samples.petclinic.customer.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.samples.petclinic.customer.application.command.PetCommand;
import org.springframework.samples.petclinic.customer.application.data.PetData;
import org.springframework.samples.petclinic.customer.application.data.PetTypeData;
import org.springframework.samples.petclinic.customer.application.mapper.PetMapper;
import org.springframework.samples.petclinic.customer.application.mapper.PetTypeMapper;
import org.springframework.samples.petclinic.customer.domain.Owner;
import org.springframework.samples.petclinic.customer.domain.OwnerRepository;
import org.springframework.samples.petclinic.customer.domain.Pet;
import org.springframework.samples.petclinic.customer.domain.PetRepository;
import org.springframework.samples.petclinic.customer.domain.shared.DomainError;
import org.springframework.samples.petclinic.customer.domain.shared.DomainException;
import org.springframework.samples.petclinic.customer.domain.tables.PetType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PetService {

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final DSLContext context;
    private final PetTypeMapper petTypeMapper;
    private final PetMapper petMapper;

    public List<PetTypeData> getPetTypes(int page, int size) {
        return petTypeMapper.toData(context
                .selectFrom(PetType.PET_TYPE)
                .offset(page)
                .limit(size)
                .fetch());
    }


    public List<PetTypeData> findPetTypes() {
        return petTypeMapper.toData(context
                .selectFrom(PetType.PET_TYPE)
                .fetch());
    }

    public PetData createPet(Integer ownerId, PetCommand petCommand) {
        final Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        Owner owner = optionalOwner.orElseThrow(() -> new DomainException("Owner with id [" + ownerId + "] not found", DomainError.RESOURCE_NOT_FOUND));

        final Pet pet = new Pet();
        owner.add(pet);
        pet.setName(petCommand.name());
        pet.setBirthDate(petCommand.birthDate());

        petRepository.findByNativeId(petCommand.typeId().longValue(),
                        org.springframework.samples.petclinic.customer.domain.PetType.class,
                        PetType.PET_TYPE,
                        PetType.PET_TYPE.ID)
                .ifPresent(pet::setType);

        log.info("Saving pet {}", pet);
        return petMapper.toData(petRepository.save(pet));
    }

    public PetData getPetById(int petId) {
        return petMapper.toData(petRepository
                .findById(petId)
                .orElseThrow(() -> new DomainException("Pet with id [" + petId + "] not found", DomainError.RESOURCE_NOT_FOUND)));
    }
}
