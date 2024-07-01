package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dto.PersonDTO;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author uriiponomarenko 31.05.2024
 */
@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person management", description = "Operations related to managing person")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @Operation(summary = "Get of list all persons")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of persons")
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> personList = personService.findAllPerson();
        return ResponseEntity.ok(personList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get person by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Person> getPersonById(
            @Parameter(description = "ID of the person to be retrieved", required = true)
            @PathVariable Long id) {
        Optional<Person> person = personService.findPersonById(id);
        return person.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PostMapping("/create-person")
    @Operation(summary = "Creating new person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Person> createPerson(@RequestBody PersonDTO personDTO) {

        Person person = personService.save(personDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(person);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update person by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Void> updatePerson(
            @Parameter(description = "ID of the person to be updated", required = true)
            @PathVariable Long id, @RequestBody PersonDTO personDTO) {
        personService.updatePerson(id, personDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete person by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Void> deletePerson(
            @Parameter(description = "ID of the person to be deleted", required = true)
            @PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{personId}/favorites/{productId}")
    @Operation(summary = "Adding products to favorites for person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to favorites successfully"),
            @ApiResponse(responseCode = "404", description = "Person or Product not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Void> addToFavorites(
            @Parameter(description = "ID of the person", required = true)
            @PathVariable("personId") Long personId,
            @Parameter(description = "ID of the product", required = true)
            @PathVariable("productId") Long productId) throws ChangeSetPersister.NotFoundException {
        personService.addToFavorites(personId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{personId}/favorites")
    @Operation(summary = "Get list of products that person liked")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of favorite products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<List<Product>> getFavorites(
            @Parameter(description = "ID of the person", required = true)
            @PathVariable("personId") Long personId) throws ChangeSetPersister.NotFoundException {
        List<Product> favorites = personService.getFavorites(personId);
        return ResponseEntity.ok(favorites);
    }
}
