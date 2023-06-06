package org.springframework.samples.petclinic.visit.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    List<Visit> findByPetId(int petId);

    List<Visit> findByPetIdIn(Collection<Integer> petIds);

}
