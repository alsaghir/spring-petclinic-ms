package org.springframework.samples.petclinic.customer.infrastructure.db;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.customer.infrastructure.db.h2.V1__Base_tables;

@Profile({"default", "h2", "h2-jooq-generator"})
@Configuration
@ComponentScan(basePackageClasses = V1__Base_tables.class)
public class H2 {
}
