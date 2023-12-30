package org.springframework.samples.petclinic.customer.application.mapper;

import org.jooq.Result;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.samples.petclinic.customer.application.data.PetTypeData;
import org.springframework.samples.petclinic.customer.domain.tables.records.PetTypeRecord;

import java.util.List;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PetTypeMapper {
    PetTypeData recordToData(PetTypeRecord petTypeRecord);

    List<PetTypeData> recordsToData(Result<PetTypeRecord> petTypeRecords);

}
