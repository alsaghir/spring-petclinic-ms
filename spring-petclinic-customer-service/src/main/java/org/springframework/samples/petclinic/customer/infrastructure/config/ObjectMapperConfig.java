package org.springframework.samples.petclinic.customer.infrastructure.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.jackson.ProblemModule;

@Configuration
public class ObjectMapperConfig {


    @Bean
    public Jackson2ObjectMapperBuilderCustomizer configureJacksonObjectMapper() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modulesToInstall(new JsonNullableModule(),
                        new ProblemModule().withStackTraces(false),
                        new JavaTimeModule());
    }
}
