package org.springframework.samples.petclinic.customer;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.samples.petclinic.customer.application.OwnerService;
import org.springframework.samples.petclinic.customer.application.mapper.OwnerMapper;
import org.springframework.samples.petclinic.customer.domain.Owner;
import org.springframework.samples.petclinic.customer.domain.OwnerRepository;
import org.springframework.samples.petclinic.customer.domain.Pet;
import org.springframework.samples.petclinic.customer.domain.PetType;
import org.springframework.samples.petclinic.customer.presentation.OwnerResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(RestDocumentationExtension.class)
class OwnerResourceTest {

  @Mock private OwnerRepository ownerRepository;
  @Spy private OwnerMapper ownerMapper = Mappers.getMapper(OwnerMapper.class);
  @Mock private MeterRegistry meterRegistry;
  MockMvc mockMvc;
  private AutoCloseable openedMocks;

  @BeforeEach
  void init(RestDocumentationContextProvider restDocumentation) {
    openedMocks = MockitoAnnotations.openMocks(this);
    when(meterRegistry.gauge(anyString(), any(), any(Number.class)))
        .thenReturn(new AtomicInteger());
    OwnerService ownerService =
        Mockito.spy(new OwnerService(ownerRepository, ownerMapper, meterRegistry));
    OwnerResource ownerResource = new OwnerResource(ownerService);

    // For use without full Spring context supporting
    // executing controllers via MockMvc
    // Spring Rest Docs for API documentation
    mockMvc =
        MockMvcBuilders.standaloneSetup(ownerResource)
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @AfterEach
  void end() throws Exception {
    openedMocks.close();
  }

  @Test
  void shouldReturnSuccessResponse() throws Exception {
    // Given
    var pets =
        Set.of(
            Pet.builder()
                .id(1L)
                .name("petName")
                .birthDate(LocalDate.now())
                .type(PetType.builder().id(1L).name("Animal").build())
                .build(),
            Pet.builder()
                .id(2L)
                .name("petName")
                .birthDate(LocalDate.now())
                .type(PetType.builder().id(2L).name("Bird").build())
                .build());
    var owner =
        Owner.builder()
            .id(1L)
            .firstName("firstName")
            .lastName("lastName")
            .address("Whatever address")
            .city("Cairo")
            .telephone("01019199241")
            .pets(pets)
            .build();
    pets.forEach(pet -> pet.setOwner(owner));

    var pets2 =
        Set.of(
            Pet.builder()
                .id(3L)
                .name("petName")
                .birthDate(LocalDate.now())
                .type(PetType.builder().id(1L).name("Animal").build())
                .build(),
            Pet.builder()
                .id(4L)
                .name("petName")
                .birthDate(LocalDate.now())
                .type(PetType.builder().id(2L).name("Bird").build())
                .build());

    var owner2 =
        Owner.builder()
            .id(2L)
            .firstName("firstName")
            .lastName("lastName")
            .address("Whatever address")
            .city("Cairo")
            .telephone("01019199241")
            .pets(pets2)
            .build();
    pets2.forEach(pet -> pet.setOwner(owner2));
    when(ownerRepository.findAll()).thenReturn(List.of(owner, owner2));

    // When
    mockMvc
        .perform(get("/owners"))
        .andDo(print())
        .andDo(document("getOwners"))

        // Then
        .andExpect(openApi().isValid("api.yaml"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void domainShouldNotAccessOtherLayers() {

    // Given
    JavaClasses importedClasses =
        new ClassFileImporter().importPackagesOf(CustomersServiceApplication.class);

    ArchRule rule =
        noClasses()
            .that()
            .resideInAnyPackage("..application", "..infrastructure", "..domain", "..presentation")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..presentation");
    // When
    // Then
    rule.check(importedClasses);
  }
}
