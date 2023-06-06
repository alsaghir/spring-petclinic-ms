package org.springframework.samples.petclinic.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PET_TYPE")
public class PetType {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PET_TYPE_SEQ_GENERATOR")
    @TableGenerator(
            name = "PET_TYPE_SEQ_GENERATOR",
            table = "APP_SEQ_GENERATOR",
            pkColumnName = "SEQ_NAME",
            pkColumnValue = "PET_TYPE_SEQ_PK",
            valueColumnName = "SEQ_VALUE",
            initialValue = 1,
            allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;
}
