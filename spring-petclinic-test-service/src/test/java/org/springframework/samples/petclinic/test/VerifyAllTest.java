package org.springframework.samples.petclinic.test;


import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VerifyAllTest {

    private String host;
    private String configServerPort;
    private String discoveryServerPort;
    private String adminServerPort;
    private String gatewayPort;
    private String customerServicePort;

    @BeforeAll
    void init() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        File file = resolver.getResource("classpath:config.yml").getFile();
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new PathResource(file.toPath()));
        factory.afterPropertiesSet();

        Properties applicationProperties = factory.getObject();
        assert applicationProperties != null;
        PropertiesPropertySource ps = new PropertiesPropertySource("configProperties", applicationProperties);
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(ps);
        host = env.getProperty("host");
        configServerPort = env.getProperty("config.server.port");
        discoveryServerPort = env.getProperty("discovery.server.port");
        adminServerPort = env.getProperty("admin.server.port");
        gatewayPort = env.getProperty("gateway.port");
        customerServicePort = env.getProperty("customer.service.port");


        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }


    @Test
    void whenBrowserRequestOwners_ThenElementsRenderedSuccessfully() {

        /*
        ChromeOptions options = new ChromeOptions();
        WebDriver webDriver = new RemoteWebDriver(new URL("http://"+browserHost+":4444/wd/hub"),options);*/

        // Given
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://localhost:7778/#/owners");

        // When
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        webDriverWait.pollingEvery(Duration.ofSeconds(5)).
                until(ExpectedConditions.presenceOfElementLocated(By.tagName("flt-glass-pane"))
                        .andThen(WebElement::getShadowRoot).andThen(sc -> sc.findElements(By.cssSelector("flt-span"))));
        Optional<WebElement> atleastOneOwner = webDriver.findElement(By.tagName("flt-glass-pane")).getShadowRoot()
                .findElements(By.cssSelector("flt-span")).stream().filter(we -> we.getText().equals("City")).findAny();

        // Then
        Assertions.assertNotNull(webDriver.getTitle());
        Assertions.assertTrue(atleastOneOwner.isPresent());

        webDriver.quit();
    }

    @Test
    void whenCallConfigServerDefaultProfileApi_ThenSuccessResponse() {
        // Given
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + host + ":" + configServerPort).build();

        // When
        String result = webTestClient.get().uri("/config-server/default")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();


        // Then
        String appName = JsonPath.parse(result).read("$.name", String.class);
        Assertions.assertNotNull(appName);
        Assertions.assertEquals("config-server", appName);
    }

    @Test
    void whenCallDiscoverServerAppsApi_ThenSuccessResponse() {
        // Given
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + host + ":" + discoveryServerPort).build();

        // When
        String result = webTestClient.get().uri("/eureka/v2/apps")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();


        // Then
        List<String> appNames = JsonPath.parse(result).read("$.applications.application[*].name", new TypeRef<>() {
        });
        Assertions.assertNotNull(appNames);
        Assertions.assertFalse(CollectionUtils.isEmpty(appNames));
        appNames.forEach(s -> Assertions.assertTrue(StringUtils.hasText(s)));
        Assertions.assertTrue(appNames.contains("API-GATEWAY"));
        Assertions.assertTrue(appNames.contains("ADMIN-SERVER"));
        Assertions.assertTrue(appNames.contains("CUSTOMERS-SERVICE"));
    }

    @Test
    void whenCallAdminServerInstancesApi_ThenSuccessResponse() {
        // Given
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + host + ":" + adminServerPort).build();

        // When
        String result = webTestClient.get().uri("/instances")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();


        // Then
        List<String> appNames = JsonPath.parse(result).read("$[*].registration.name", new TypeRef<>() {
        });
        Assertions.assertNotNull(appNames);
        Assertions.assertFalse(CollectionUtils.isEmpty(appNames));
        appNames.forEach(s -> Assertions.assertTrue(StringUtils.hasText(s)));
        Assertions.assertTrue(appNames.contains("API-GATEWAY"));
        Assertions.assertTrue(appNames.contains("ADMIN-SERVER"));
        Assertions.assertTrue(appNames.contains("CUSTOMERS-SERVICE"));
    }

    @Test
    void whenCallGatewayActuatorApi_ThenSuccessResponse() {
        // Given
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + host + ":" + gatewayPort).build();

        // When
        String result = webTestClient.get().uri("/actuator/gateway/routes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();


        // Then
        String uri = JsonPath.parse(result).read("$[0].uri", new TypeRef<>() {
        });
        Assertions.assertNotNull(uri);
        Assertions.assertFalse(uri.isBlank());
    }


    @Test
    void whenCallCustomerServiceAllOwnersApi_ThenSuccessResponse() {
        // Given
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + host + ":" + customerServicePort).build();

        // When
        String result = webTestClient.get().uri("/api/customer/owners")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();


        // Then
        Integer id = JsonPath.parse(result).read("$[0].id", Integer.class);
        Assertions.assertNotNull(id);
    }
}
