package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.services.PersonService;
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
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/{personId}/favorites/{adId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable("personId") Long personId,
                                               @PathVariable("adId") Long adId) throws ChangeSetPersister.NotFoundException {
        personService.addToFavorites(personId, adId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{personId}/favorites")
    public ResponseEntity<List<Ad>> getFavorites(@PathVariable("personId") Long personId) throws ChangeSetPersister.NotFoundException {
        List<Ad> favorites = personService.getFavorites(personId);
        return ResponseEntity.ok(favorites);
    }
}
