package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uriiponomarenko 31.05.2024
 */
@RestController
@RequestMapping("/api/person")
@Tag(name = "Person management", description = "Operations related to managing person")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    @GetMapping("/persons")
    @Operation(summary = "Get of list all persons")
    public List<Person> getAllPersons() {
        return personService.findAllPerson();
    }

    @PostMapping("/person")
    @Operation(summary = "Add a new person")
    public Person addPerson(@RequestBody Person person) {
        return personService.save(person);
    }

    @PutMapping("/person/{id}")
    @Operation(summary = "Update an existing person")
    public void updatePerson(@PathVariable("id") long id, @RequestBody Person person) {
        personService.updatePerson(id, person);
    }

    @DeleteMapping("/person/{id}")
    @Operation(summary = "Delete a person")
    public void deletePerson(long id) {
        personService.deletePerson(id);
    }
    @PostMapping("/{personId}/favorites/{adId}")
    @Operation(summary = "Adding products to favorites for person")
    public void addToFavorites(@PathVariable("personId") Long personId,
                                               @PathVariable("adId") Long adId) throws ChangeSetPersister.NotFoundException {
        personService.addToFavorites(personId, adId);
    }

    @GetMapping("/{personId}/favorites")
    @Operation(summary = "Get list of products that liked person")
    public ResponseEntity<List<Ad>> getFavorites(@PathVariable("personId") Long personId) throws ChangeSetPersister.NotFoundException {
        List<Ad> favorites = personService.getFavorites(personId);
        return ResponseEntity.ok(favorites);
    }
}
