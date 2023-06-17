package org.springframework.samples.petclinic.customer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.style.ToStringCreator;

import java.time.LocalDate;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PET")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PET_SEQ_GENERATOR")
    @TableGenerator(
            name = "PET_SEQ_GENERATOR",
            table = "APP_SEQ_GENERATOR",
            pkColumnName = "SEQ_NAME",
            pkColumnValue = "PET_SEQ_PK",
            valueColumnName = "SEQ_VALUE",
            initialValue = 1,
            allocationSize = 1)
    private Long id;

    private String name;
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "TYPE_ID")
    private PetType type;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    @JsonIgnore
    private Owner owner;

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId())
                .append("name", this.getName())
                .append("birthDate", this.getBirthDate())
                .append("type", this.getType() == null ? null : this.getType().getName())
                .append("ownerFirstname", this.getOwner() == null ? null : this.getOwner().getFirstName())
                .append("ownerLastname", this.getOwner() == null ? null : this.getOwner().getLastName())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pet current = (Pet) o;

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
