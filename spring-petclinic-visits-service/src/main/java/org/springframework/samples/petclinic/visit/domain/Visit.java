package org.springframework.samples.petclinic.visit.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "VISITS")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VISIT_SEQ_GENERATOR")
    @TableGenerator(
            name = "VISIT_SEQ_GENERATOR",
            table = "APP_SEQ_GENERATOR",
            pkColumnName = "SEQ_NAME",
            pkColumnValue = "VISIT_SEQ_PK",
            valueColumnName = "SEQ_VALUE",
            initialValue = 1,
            allocationSize = 1)
    private Long id;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate = LocalDate.now();

    @Size(max = 8192)
    private String description;

    private int petId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visit current = (Visit) o;

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
