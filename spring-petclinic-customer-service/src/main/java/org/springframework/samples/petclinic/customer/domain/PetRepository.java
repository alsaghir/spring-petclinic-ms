package org.springframework.samples.petclinic.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.customer.domain.shared.CustomizedRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer>,
        CustomizedRepository<org.springframework.samples.petclinic.customer.domain.tables.PetType, PetType, Long> {

}

