package org.springframework.samples.petclinic.customer;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.samples.petclinic.customer.application.data.OwnerData;
import org.springframework.samples.petclinic.customer.application.data.PetData;
import org.springframework.samples.petclinic.customer.application.data.PetTypeData;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "h2"})
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OwnerResourceITest {

  @LocalServerPort private Integer port;

  private AutoCloseable openedMocks;
  private RequestSpecification spec; // Spring Rest Docs for API Documentation

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void init(RestDocumentationContextProvider restDocumentation) {
    openedMocks = MockitoAnnotations.openMocks(this);

    spec =
        new RequestSpecBuilder()
            .addFilter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @AfterEach
  void end() throws Exception {
    openedMocks.close();
  }

  @Test
  void shouldReturnSuccessResponse() throws Exception {

    // Given
    var pet =
        new PetData(
            1, "petName", "ownerName", LocalDate.now().minusDays(5), new PetTypeData(1, "Animal"));
    var owner =
        new OwnerData(
            1,
            "firstNameExample",
            "lastNameExample",
            "Address Example",
            "CityExample",
            "01019198771",
            List.of(pet));

    // When
    RestAssured.given(this.spec)
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .filter(RestAssuredRestDocumentation.document("createOwner"))
        .body(objectMapper.writeValueAsString(owner))
        .when()
        .post("http://localhost:" + port + "/owners")

        // Then
        .then()
        .assertThat()
        .statusCode(Is.is(201));
  }
}
