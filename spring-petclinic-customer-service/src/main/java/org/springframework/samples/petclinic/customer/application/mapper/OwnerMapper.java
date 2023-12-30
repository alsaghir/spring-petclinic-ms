package org.springframework.samples.petclinic.customer.application.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.samples.petclinic.customer.application.command.OwnerCommand;
import org.springframework.samples.petclinic.customer.application.data.OwnerData;
import org.springframework.samples.petclinic.customer.domain.Owner;

import java.util.List;

@Mapper(uses = {JsonNullableMapper.class, PetMapper.class},
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OwnerMapper {

    Owner toEntity(OwnerCommand ownerCommand);

    OwnerData toDto(Owner owner);

    List<OwnerData> toDto(List<Owner> owners);

    @InheritConfiguration
    void update(OwnerCommand update, @MappingTarget Owner destination);
}
