package org.springframework.samples.petclinic.admin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.LogstashEncoder;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

  public static void main(String[] args) {
    log.info(
        "Started and logstash is loaded and its name is " + LogstashEncoder.class.getSimpleName());
    SpringApplication.run(ApiGatewayApplication.class, args);
  }

  /**
   * After initialization functions done here
   *
   * @return {@link ApplicationRunner} to run after everything starts
   */
  @Bean
  public ApplicationRunner afterAppStarted() {
    return args -> configureFlutterFrontEnd(args.getSourceArgs());
  }

  /**
   * Resolves properties from command line to be written to config.yaml for the front end app
   * (flutter UI in the src/dart folder)
   *
   * @param args are properties sources from command line
   * @throws IOException dealing with read/write config file of the UI
   */
  private static void configureFlutterFrontEnd(String[] args) throws IOException {
    // Read Yaml and get ready to resolve values
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    File file = resolver.getResource("classpath:static/assets/assets/config.yaml").getFile();
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(new PathResource(file.toPath()));
    factory.afterPropertiesSet();

    Properties applicationProperties = factory.getObject();

    PropertiesPropertySource ps =
        new PropertiesPropertySource(
            "configProperties", Objects.requireNonNull(applicationProperties));
    StandardEnvironment env = new StandardEnvironment();
    env.getPropertySources().addFirst(ps);
    env.getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));
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
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Qualifier("corsConfigurer")
  public WebFluxConfigurer corsConfigurer(
      WebProperties webProperties,
      WebFluxProperties webFluxProperties,
      ListableBeanFactory beanFactory,
      ObjectProvider<HandlerMethodArgumentResolver> resolvers,
      ObjectProvider<CodecCustomizer> codecCustomizers,
      ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizer,
      ObjectProvider<ViewResolver> viewResolvers) {
    return new WebFluxAutoConfiguration.WebFluxConfig(
        webProperties,
        webFluxProperties,
        beanFactory,
        resolvers,
        codecCustomizers,
        resourceHandlerRegistrationCustomizer,
        viewResolvers) {

      @Override
      public void addCorsMappings(CorsRegistry registry) {

        registry
            .addMapping("/**")
            .allowedOriginPatterns(Constant.ALLOWED_ORIGIN_PATTERNS.get())
            .allowedMethods(HttpMethod.GET.name(), HttpMethod.OPTIONS.name());
      }
    };
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOriginPatterns(List.of(Constant.ALLOWED_ORIGIN_PATTERNS.get()));
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.setAllowedMethods(
        List.of(
            HttpMethod.GET.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name()));
    UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsWebFilter(corsConfigurationSource);
  }
}
