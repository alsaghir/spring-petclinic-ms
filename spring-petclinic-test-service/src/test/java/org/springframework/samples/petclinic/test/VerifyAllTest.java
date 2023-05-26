package org.springframework.samples.petclinic.test;


import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VerifyAllTest {

    private String configServerPort;
    private String discoveryServerPort;
    private String adminServerPort;
    private String gatewayPort;
    private String customerServicePort;

    private String configServerHost;
    private String discoveryServerHost;
    private String adminServerHost;
    private String gatewayHost;
    private String customerServiceHost;

    WebDriver webDriver;

    @BeforeAll
    void init() throws IOException, InterruptedException {
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
        if (System.getProperty("host") != null)
            env.getPropertySources().addFirst(new SimpleCommandLinePropertySource("--host=" + System.getProperty("host")));
        env.setIgnoreUnresolvableNestedPlaceholders(true);
        String remoteBrowserHost = env.getProperty("remote.browser.host");
        String remoteBrowserPort = env.getProperty("remote.browser.port");
        configServerHost = env.getProperty("config-server.host");
        configServerPort = env.getProperty("config-server.port");
        discoveryServerHost = env.getProperty("discovery-server.host");
        discoveryServerPort = env.getProperty("discovery-server.port");
        adminServerHost = env.getProperty("admin-server.host");
        adminServerPort = env.getProperty("admin-server.port");
        gatewayHost = env.getProperty("gateway-server.host");
        gatewayPort = env.getProperty("gateway-server.port");
        customerServiceHost = env.getProperty("customer-service.host");
        customerServicePort = env.getProperty("customer-service.port");

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

        verifyApiGatewayIsUp();


        assert remoteBrowserHost != null;
        webDriver = remoteBrowserHost.equals("localhost") ?
                new FirefoxDriver(new FirefoxOptions()
                        .setImplicitWaitTimeout(Duration.ofSeconds(10))
                        .setScriptTimeout(Duration.ofSeconds(10))
                        .setPageLoadTimeout(Duration.ofSeconds(10))
                ) :
                new RemoteWebDriver(new URL("http://" + remoteBrowserHost + ":" + remoteBrowserPort + "/wd/hub"),
                        new FirefoxOptions()
                                .setImplicitWaitTimeout(Duration.ofSeconds(5))
                                .setScriptTimeout(Duration.ofSeconds(5))
                                .setPageLoadTimeout(Duration.ofSeconds(5))
                );

    }

    @SuppressWarnings("java:S2925")
    private void verifyApiGatewayIsUp() throws InterruptedException {
        boolean apiGateWayIsUp = false;
        int counter = 0;
        while (!apiGateWayIsUp) {
            counter++;
            WebClient webClient = WebClient.create("http://" + gatewayHost + ":" + gatewayPort);
            HttpStatusCode code;
            try {
                code = webClient.get().uri("/api/customer/owners")
                        .retrieve()
                        .toBodilessEntity()
                        .map(ResponseEntity::getStatusCode)
                        .block();

            } catch (Exception ex) {
                System.out.println("Connection Failure");
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                continue;
            }
            assert code != null;
            if (code.equals(HttpStatus.OK) || counter == 10)
                apiGateWayIsUp = true;


        }
    }

    @AfterAll
    void last() {
        webDriver.quit();
    }

    @Test
    void whenBrowserRequestOwners_ThenElementsRenderedSuccessfully() {

        int attempts = 0;
        while (attempts < 2) {
            try {
                // Given
                webDriver.get("http://" + gatewayHost + ":" + gatewayPort + "/#/owners");

                // When
                WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(20), Duration.ofSeconds(20));
                Optional<WebElement> atleastOneOwner = webDriverWait.pollingEvery(Duration.ofSeconds(5)).
                        until(presenceOfElementLocated(By.tagName("flt-glass-pane"))
                                .andThen(WebElement::getShadowRoot)
                                .andThen(sc -> sc.findElements(By.cssSelector("flt-span")).stream()
                                        .filter(we -> we.getText().equals("City")).findAny()));

                // Then
                Assertions.assertNotNull(webDriver.getTitle());
                Assertions.assertEquals("Pet Clinic App", webDriver.getTitle());
                Assertions.assertTrue(atleastOneOwner.isPresent());
                break;
            } catch (StaleElementReferenceException e) {
                e.printStackTrace();
            }
            attempts++;
        }

        // TODO Remove
       /* attempts = 0;
        while (attempts < 2) {
            try {
                // Given
                webDriver.get("http://" + gatewayHost + ":" + gatewayPort + "/#/owners");

                // When
                WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(20), Duration.ofSeconds(20));
                WebElement glassPane = webDriverWait.pollingEvery(Duration.ofSeconds(5)).
                        until(presenceOfElementLocated(By.tagName("flt-glass-pane")));

                SearchContext shadowRoot = (SearchContext) ((JavascriptExecutor) webDriver).executeScript("return arguments[0].shadowRoot", glassPane);
                Optional<WebElement> cityElement = webDriverWait.until(ExpectedConditions.visibilityOf(shadowRoot.findElement(By.cssSelector("flt-span"))))
                        .findElements(By.tagName("flt-span")).stream()
                        .filter(we -> we.getText().equals("City"))
                        .findFirst();

                // Then
                Assertions.assertNotNull(webDriver.getTitle());
                Assertions.assertEquals("Pet Clinic App", webDriver.getTitle());
                Assertions.assertTrue(cityElement.isPresent());
                break;
            } catch (StaleElementReferenceException e) {
                e.printStackTrace();
            }
            attempts++;
        }*/
    }

    @Test
    void whenCallConfigServerDefaultProfileApi_ThenSuccessResponse() {
        // Given
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + configServerHost + ":" + configServerPort).build();

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
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + discoveryServerHost + ":" + discoveryServerPort).build();

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
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + adminServerHost + ":" + adminServerPort).build();

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
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://" + gatewayHost + ":" + gatewayPort).build();

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
        WebTestClient webTestClientDirect = WebTestClient.bindToServer().baseUrl("http://" + customerServiceHost + ":" + customerServicePort).build();
        WebTestClient webTestClientApiGateway = WebTestClient.bindToServer().baseUrl("http://" + gatewayHost + ":" + gatewayPort).build();

        // When
        String resultDirect = webTestClientDirect.get().uri("/owners")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();

        String resultApiGateway = webTestClientApiGateway.get().uri("/api/customer/owners")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class).getResponseBody().blockFirst();


        // Then
        Integer id = JsonPath.parse(resultDirect).read("$[0].id", Integer.class);
        Assertions.assertNotNull(id);

        id = JsonPath.parse(resultApiGateway).read("$[0].id", Integer.class);
        Assertions.assertNotNull(id);
    }
}
