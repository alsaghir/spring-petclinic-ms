package org.springframework.samples.petclinic.customer.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.samples.petclinic.customer.application.data.PetData;
import org.springframework.samples.petclinic.customer.application.data.PetTypeData;
import org.springframework.samples.petclinic.customer.domain.Owner;
import org.springframework.samples.petclinic.customer.domain.Pet;
import org.springframework.samples.petclinic.customer.domain.PetType;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PetMapper {

    @Mapping(source = "owner", target = "ownerName", qualifiedByName = "assembleOwnerName")
    PetData toData(Pet pet);

    PetTypeData toData(PetType petType);

    @Named("assembleOwnerName")
    default String assembleOwnerName(Owner owner) {
        return owner.getFirstName() + " " + owner.getLastName();
    }

}
