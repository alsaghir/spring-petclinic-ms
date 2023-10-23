package org.springframework.samples.petclinic.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

  // Playwright objects
  static Playwright playwright;
  static Browser browser;

  // Playwright New instance for each test method
  BrowserContext context;
  Page page;

  @BeforeAll
  void init() throws IOException, InterruptedException {
    // Config loading
    StandardEnvironment env = getStandardEnvironment();
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

    configureJsonAssertions();

    verifyApiGatewayIsUp();

    assert remoteBrowserHost != null;

    playwright = Playwright.create();
     browser = playwright.firefox().launch();

   
  }

  /**
   * Use JacksonJsonProvider for json resolving and assertions
   */
  private static void configureJsonAssertions() {

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


  @AfterAll
  void last() {
    //webDriver.quit();
    playwright.close();
  }

  @BeforeEach
  void createContextAndPage() {
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  void closeContext() {
    context.close();
  }

  @Test
  void whenBrowserRequestOwners_ThenElementsRenderedSuccessfully() throws InterruptedException {
    // Given
    page.navigate("http://" + gatewayHost + ":" + gatewayPort + "/#/owners");

    // When
    var rootElement = page.locator("flt-glass-pane");
    rootElement.waitFor(new WaitForOptions().setState(WaitForSelectorState.ATTACHED));
    PlaywrightAssertions.assertThat(rootElement).isEnabled();
    /* In case html web renderer used
    var cityText = rootElement.locator("flt-span:has-text('City')").first();
    cityText.waitFor(new WaitForOptions().setTimeout(Duration.ofSeconds(5).toMillis()));
    cityText.waitFor(new WaitForOptions().setState(WaitForSelectorState.ATTACHED));
    cityText.waitFor(new WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    // Then
    PlaywrightAssertions.assertThat(cityText).isAttached();
    PlaywrightAssertions.assertThat(cityText).isEnabled();
    PlaywrightAssertions.assertThat(cityText).isVisible();*/
    assertEquals("Pet Clinic App", page.title());
  }

  @Test
  void whenCallConfigServerDefaultProfileApi_ThenSuccessResponse() {
    // Given
    WebTestClient webTestClient = WebTestClient.bindToServer()
      .baseUrl("http://" + configServerHost + ":" + configServerPort).build();

    // When
    String result = webTestClient.get().uri("/config-server/default")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class)
      .getResponseBody().blockFirst();

    // Then
    String appName = JsonPath.parse(result).read("$.name", String.class);
    assertNotNull(appName);
    assertEquals("config-server", appName);
  }

  @Test
  @Disabled
  void whenCallDiscoverServerAppsApi_ThenSuccessResponse() {
    // Given
    WebTestClient webTestClient = WebTestClient.bindToServer()
      .baseUrl("http://" + discoveryServerHost + ":" + discoveryServerPort).build();

    // When
    String result = webTestClient.get().uri("/eureka/v2/apps")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class)
      .getResponseBody().blockFirst();

    // Then
    List<String> appNames = JsonPath.parse(result)
      .read("$.applications.application[*].name", new TypeRef<>() {
      });
    assertNotNull(appNames);
    Assertions.assertFalse(CollectionUtils.isEmpty(appNames));
    appNames.forEach(s -> Assertions.assertTrue(StringUtils.hasText(s)));
    Assertions.assertTrue(appNames.contains("API-GATEWAY"));
    Assertions.assertTrue(appNames.contains("ADMIN-SERVER"));
    Assertions.assertTrue(appNames.contains("CUSTOMERS-SERVICE"));
  }

  @Test
  @Disabled
  void whenCallAdminServerInstancesApi_ThenSuccessResponse() {
    // Given
    WebTestClient webTestClient = WebTestClient.bindToServer()
      .baseUrl("http://" + adminServerHost + ":" + adminServerPort).build();

    // When
    String result = webTestClient.get().uri("/instances")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class)
      .getResponseBody().blockFirst();

    // Then
    List<String> appNames = JsonPath.parse(result).read("$[*].registration.name", new TypeRef<>() {
    });
    assertNotNull(appNames);
    Assertions.assertFalse(CollectionUtils.isEmpty(appNames));
    appNames.forEach(s -> Assertions.assertTrue(StringUtils.hasText(s)));
    Assertions.assertTrue(appNames.contains("API-GATEWAY"));
    Assertions.assertTrue(appNames.contains("ADMIN-SERVER"));
    Assertions.assertTrue(appNames.contains("CUSTOMERS-SERVICE"));
  }

  @Test
  void whenCallGatewayActuatorApi_ThenSuccessResponse() {
    // Given
    WebTestClient webTestClient = WebTestClient.bindToServer()
      .baseUrl("http://" + gatewayHost + ":" + gatewayPort).build();

    // When
    String result = webTestClient.get().uri("/actuator/gateway/routes")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class)
      .getResponseBody().blockFirst();

    // Then
    String uri = JsonPath.parse(result).read("$[0].uri", new TypeRef<>() {
    });
    assertNotNull(uri);
    Assertions.assertFalse(uri.isBlank());
  }


  @Test
  void whenCallCustomerServiceAllOwnersApi_ThenSuccessResponse() {
    // Given
    WebTestClient webTestClientDirect = WebTestClient.bindToServer()
      .baseUrl("http://" + customerServiceHost + ":" + customerServicePort).build();
    WebTestClient webTestClientApiGateway = WebTestClient.bindToServer()
      .baseUrl("http://" + gatewayHost + ":" + gatewayPort).build();

    // When
    String resultDirect = webTestClientDirect.get().uri("/owners")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class)
      .getResponseBody().blockFirst();

    String resultApiGateway = webTestClientApiGateway.get().uri("/api/customer/owners")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON).returnResult(String.class)
      .getResponseBody().blockFirst();

    // Then
    Integer id = JsonPath.parse(resultDirect).read("$[0].id", Integer.class);
    assertNotNull(id);

    id = JsonPath.parse(resultApiGateway).read("$[0].id", Integer.class);
    assertNotNull(id);
  }


  /**
   * @return {@link StandardEnvironment} represents the configured environment
   * @throws IOException in case of error reading the config file
   */
  private static StandardEnvironment getStandardEnvironment() throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    File file = resolver.getResource("classpath:config.yml").getFile();
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(new PathResource(file.toPath()));
    factory.afterPropertiesSet();

    Properties applicationProperties = factory.getObject();
    assert applicationProperties != null;
    PropertiesPropertySource ps = new PropertiesPropertySource("configProperties",
      applicationProperties);
    StandardEnvironment env = new StandardEnvironment();
    env.getPropertySources().addFirst(ps);
      if (System.getProperty("host") != null) {
          env.getPropertySources()
            .addFirst(new SimpleCommandLinePropertySource("--host=" + System.getProperty("host")));
      }
    env.setIgnoreUnresolvableNestedPlaceholders(true);
    return env;
  }

  /**
   * retry hitting the api gateway owners end point as a proof of being ready for completing all
   * other tests
   *
   * @throws InterruptedException if process interrupted
   */
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
        if (code.equals(HttpStatus.OK) || counter == 10) {
            apiGateWayIsUp = true;
        }


    }
  }
}
