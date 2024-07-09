package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dtos.BuyerDTO;
import com.gmail.ypon2003.marketplacebackend.models.BuyerEntity;
import com.gmail.ypon2003.marketplacebackend.repositories.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buyers")
public class BuyerController {

    @Autowired
    private BuyerRepository buyerRepository;

    @GetMapping
    public List<BuyerDTO> getAllBuyers() {
        return buyerRepository.findAll().stream()
                .map(buyer -> new BuyerDTO(buyer.getId(), buyer.getLastname(), buyer.getFirstname(), buyer.getCreateDate(), buyer.getPhone(), buyer.getPassword(), buyer.getProducts(), buyer.getEmail()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public BuyerDTO createBuyer(@RequestBody BuyerDTO buyerDto) {
        BuyerEntity buyer = new BuyerEntity();
        buyer.setLastname(buyerDto.lastname());
        buyer.setFirstname(buyerDto.firstname());
        buyer.setCreateDate(buyerDto.createDate());
        buyer.setPhone(buyerDto.phone());
        buyer.setPassword(buyerDto.password());
        buyer.setProducts(buyerDto.products());
        buyer.setEmail(buyerDto.email());
        BuyerEntity savedBuyer = buyerRepository.save(buyer);
        return new BuyerDTO(savedBuyer.getId(), savedBuyer.getLastname(), savedBuyer.getFirstname(), savedBuyer.getCreateDate(), savedBuyer.getPhone(), savedBuyer.getPassword(), savedBuyer.getProducts(), savedBuyer.getEmail());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyerDTO> getBuyerById(@PathVariable Long id) {
        Optional<BuyerEntity> buyer = buyerRepository.findById(id);
        if (buyer.isPresent()) {
            BuyerEntity b = buyer.get();
            BuyerDTO buyerDto = new BuyerDTO(b.getId(), b.getLastname(), b.getFirstname(), b.getCreateDate(), b.getPhone(), b.getPassword(), b.getProducts(), b.getEmail());
            return ResponseEntity.ok(buyerDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyerDTO> updateBuyer(@PathVariable Long id, @RequestBody BuyerDTO buyerDto) {
        Optional<BuyerEntity> buyerOptional = buyerRepository.findById(id);
        if (buyerOptional.isPresent()) {
            BuyerEntity buyer = buyerOptional.get();
            buyer.setLastname(buyerDto.lastname());
            buyer.setFirstname(buyerDto.firstname());
            buyer.setCreateDate(buyerDto.createDate());
            buyer.setPhone(buyerDto.phone());
            buyer.setPassword(buyerDto.password());
            buyer.setProducts(buyerDto.products());
            buyer.setEmail(buyerDto.email());
            BuyerEntity updatedBuyer = buyerRepository.save(buyer);
            BuyerDTO updatedBuyerDto = new BuyerDTO(updatedBuyer.getId(), updatedBuyer.getLastname(), updatedBuyer.getFirstname(), updatedBuyer.getCreateDate(), updatedBuyer.getPhone(), updatedBuyer.getPassword(), updatedBuyer.getProducts(), updatedBuyer.getEmail());
            return ResponseEntity.ok(updatedBuyerDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Long id) {
        if (buyerRepository.existsById(id)) {
            buyerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}