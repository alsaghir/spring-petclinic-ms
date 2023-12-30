package org.springframework.samples.petclinic.customer.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.style.ToStringCreator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OWNER")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "OWNER_SEQ_GENERATOR")
    @TableGenerator(
            name = "OWNER_SEQ_GENERATOR",
            table = "APP_SEQ_GENERATOR",
            pkColumnName = "SEQ_NAME",
            pkColumnValue = "OWNER_SEQ_PK",
            valueColumnName = "SEQ_VALUE",
            initialValue = 1,
            allocationSize = 1)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String address;

    private String city;

    @NotBlank
    @Digits(fraction = 0, integer = 12)
    private String telephone;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "owner")
    private Set<Pet> pets = new HashSet<>();


    public void add(Pet pet) {
        this.pets.add(pet);
        pet.setOwner(this);
    }

    public Set<Pet> getPets() {
        return Collections.unmodifiableSet(pets);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)

                .append("id", this.getId())
                .append("lastName", this.getLastName())
                .append("firstName", this.getFirstName())
                .append("address", this.address)
                .append("city", this.city)
                .append("telephone", this.telephone)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Owner current = (Owner) o;

        return getId() != null && getId().equals(current.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null)
            return getClass().hashCode();
        else
            return Objects.hash(this.getId());
    }
}
