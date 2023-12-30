package org.springframework.samples.petclinic.customer.infrastructure.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.customer.application.mapper.OwnerMapper;
import org.springframework.samples.petclinic.customer.application.mapper.PetMapper;
import org.springframework.samples.petclinic.customer.application.mapper.PetTypeMapper;

@Configuration
public class MappersConfig {

  @Bean
  public OwnerMapper getOwnerMapper() {
    return Mappers.getMapper(OwnerMapper.class);
  }

  @Bean
  public PetTypeMapper getPetTypeMapper() {
    return Mappers.getMapper(PetTypeMapper.class);
  }

  @Bean
  public PetMapper getPetMapper() {
    return Mappers.getMapper(PetMapper.class);
  }
}
