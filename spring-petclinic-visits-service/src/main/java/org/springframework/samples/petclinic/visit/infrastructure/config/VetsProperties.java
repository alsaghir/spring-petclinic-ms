package org.springframework.samples.petclinic.visit.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "vets")
public class VetsProperties {

    private Cache cache;

    @Data
    public static class Cache {

        private int ttl;

        private int heapSize;
    }
}
