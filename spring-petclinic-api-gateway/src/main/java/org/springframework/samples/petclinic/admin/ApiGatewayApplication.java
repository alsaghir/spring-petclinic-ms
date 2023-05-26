package org.springframework.samples.petclinic.admin;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.LogstashEncoder;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        log.info("Started and logstash is loaded and its name is " + LogstashEncoder.class.getSimpleName());
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public ApplicationRunner afterAppStarted() {
        return args -> {
            // Read Yaml and get ready to resolve values
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            File file = resolver.getResource("classpath:static/assets/assets/config.yaml").getFile();
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(new PathResource(file.toPath()));
            factory.afterPropertiesSet();

            Properties applicationProperties = factory.getObject();

            PropertiesPropertySource ps = new PropertiesPropertySource("configProperties", Objects.requireNonNull(applicationProperties));
            StandardEnvironment env = new StandardEnvironment();
            env.getPropertySources().addFirst(ps);
            env.getPropertySources().addFirst(new SimpleCommandLinePropertySource(args.getSourceArgs()));
            String[] properties = ps.getPropertyNames();

            // Resolve properties values
            Map<String, Object> resolvedProperties = new LinkedHashMap<>();
            for (String property : properties) {
                resolvedProperties.put(property, env.getProperty(property));
            }

            // Write properties back with resolved values
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            FileWriter writer = new FileWriter(file);
            Yaml yaml = new Yaml(options);
            yaml.dump(resolvedProperties, writer);
            writer.close();
        };

    }
}