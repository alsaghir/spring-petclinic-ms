package org.springframework.samples.petclinic.customer.application.mapper;

import org.jooq.Result;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.samples.petclinic.customer.application.data.PetTypeData;
import org.springframework.samples.petclinic.customer.domain.Pet;
import org.springframework.samples.petclinic.customer.domain.tables.records.PetTypeRecord;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PetTypeMapper {
    PetTypeData toData(PetTypeRecord petTypeRecord);

    List<PetTypeData> toData(Result<PetTypeRecord> petTypeRecords);

}
