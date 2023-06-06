package org.springframework.samples.petclinic.admin;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.LogstashEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        log.info("Started and logstash is loaded and its name is " + LogstashEncoder.class.getSimpleName());
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
