package org.springframework.samples.petclinic.customer.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customer.application.OwnerService;
import org.springframework.samples.petclinic.customer.application.command.OwnerCommand;
import org.springframework.samples.petclinic.customer.application.data.OwnerData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/owners")
@RestController
@Tag(name = "owner", description = "Owner Resource")
public class OwnerResource {

  private final OwnerService ownerService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OwnerData create(@Valid @RequestBody OwnerCommand ownerCommand) {
    return ownerService.create(ownerCommand);
  }

  @GetMapping(value = "/{ownerId}")
  public OwnerData findOwner(@PathVariable("ownerId") @Min(1) int ownerId) {
    return ownerService.findById(ownerId);
  }

  @Operation(
      summary = "Get all owners",
      description = "Get all owners in one shot, no pagination support",
      tags = {"store"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(array = @ArraySchema(schema = @Schema(implementation = OwnerData.class)))),
        @ApiResponse(responseCode = "400", description = "Bad Request")
      })
  @GetMapping
  public List<OwnerData> findAll() {
    return ownerService.findAll();
  }

  @PutMapping(value = "/{ownerId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(
      @PathVariable("ownerId") @Min(1) int ownerId, @Valid @RequestBody OwnerCommand ownerCommand) {
    ownerService.updateOwner(ownerId, ownerCommand);
  }
}
