package org.springframework.samples.petclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@ConfigurationPropertiesScan
public class VetsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetsServiceApplication.class, args);
    }
}
