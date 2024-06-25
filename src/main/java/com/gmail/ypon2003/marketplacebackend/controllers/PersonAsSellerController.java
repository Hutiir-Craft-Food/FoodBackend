package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dto.PersonAsSellerDTO;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.services.PersonAsSellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
@Tag(name = "Seller management", description = "Operations related to managin seller")
@RequiredArgsConstructor
@Slf4j
public class PersonAsSellerController {

    private final PersonAsSellerService personAsSellerService;

    @PostMapping("/register-seller")
    @Operation(summary = "Register a new personAsSeller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seller registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Person> registerSeller(@RequestBody PersonAsSellerDTO personAsSellerDTO) {
        Person person = personAsSellerService.saveSeller(personAsSellerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(person);
    }
}
