package org.springframework.samples.petclinic.vet.domain.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.QualifiedRecord;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.conf.ParamType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomizedRepositoryImpl<T extends Table<? extends Record>, E, ID> implements CustomizedRepository<T, E, ID> {

    @PersistenceContext
    private EntityManager entityManager;
    private final DSLContext dslContext;

    public CustomizedRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Optional<E> findByNativeId(ID IdValue,
                          Class<E> entityClass,
                          T table,
                          TableField<? extends QualifiedRecord<? extends Record>, ID> idField) {
        final SelectQuery<Record> sqlGenerator =
                this.dslContext.select()
                        .from(table)
                        .where(idField.eq(IdValue))
                        .getQuery();

        // Retrieve sql with named parameter
        final String sql = sqlGenerator.getSQL(ParamType.NAMED);
        // and create actual hibernate query
        final Query query = this.entityManager.createNativeQuery(sql, entityClass);
        // fill in parameter
        sqlGenerator.getParams().forEach((n, v) -> query.setParameter(n, v.getValue()));
        // execute query
        return Optional.of((E) query.getSingleResult());
    }
}
