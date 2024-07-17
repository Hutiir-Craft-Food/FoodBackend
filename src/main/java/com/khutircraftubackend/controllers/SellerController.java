package com.khutircraftubackend.controllers;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.services.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@Valid @RequestBody SellerDTO sellerDTO) {
        if(!sellerDTO.isPasswordMatching()) {
            return new ResponseEntity<>("Пароль та підтвердження паролю не співпадають", HttpStatus.BAD_REQUEST);
        }

        sellerService.registerSeller(sellerDTO);
        return new ResponseEntity<>("Продавець зареєстрований успішно", HttpStatus.CREATED);
    }

    public ResponseEntity<String> confirmSeller(@RequestParam("code")
                                                String confirmationCode) {
        if(sellerService.confirmSeller(confirmationCode)) {
            return ResponseEntity.ok("Обліковий запис підтверджено.");
        }
        return ResponseEntity.badRequest().body("Невірний код підтвердження або обліковий запис уже підтверджено.");
    }
}
