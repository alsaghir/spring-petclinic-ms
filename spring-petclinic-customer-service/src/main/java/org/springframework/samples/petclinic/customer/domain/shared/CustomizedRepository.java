package org.springframework.samples.petclinic.customer.domain.shared;

import org.jooq.QualifiedRecord;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Optional;

public interface CustomizedRepository<T extends Table<? extends Record>, E, ID> {

    Optional<E> findByNativeId(ID IdValue,
                               Class<E> entityClass,
                               T table,
                               TableField<? extends QualifiedRecord<? extends Record>, ID> idField);

}
