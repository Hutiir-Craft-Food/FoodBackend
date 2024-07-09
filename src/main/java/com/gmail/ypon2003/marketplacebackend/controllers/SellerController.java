package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dtos.SellerDTO;
import com.gmail.ypon2003.marketplacebackend.models.SellerEntity;
import com.gmail.ypon2003.marketplacebackend.repositories.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    @Autowired
    private SellerRepository sellerRepository;

    @GetMapping
    public List<SellerDTO> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(seller -> new SellerDTO(seller.getId(), seller.getName(), seller.getEmail(), seller.getPhone(), seller.getCreateDate(), seller.getPassword(), seller.getProducts()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public SellerDTO createSeller(@RequestBody SellerDTO sellerDto) {
        SellerEntity seller = new SellerEntity();
        seller.setName(sellerDto.name());
        seller.setEmail(sellerDto.email());
        seller.setPhone(sellerDto.phone());
        seller.setCreateDate(sellerDto.createDate());
        seller.setPassword(sellerDto.password());
        seller.setProducts(sellerDto.products());
        SellerEntity savedSeller = sellerRepository.save(seller);
        return new SellerDTO(savedSeller.getId(), savedSeller.getName(), savedSeller.getEmail(), savedSeller.getPhone(), savedSeller.getCreateDate(), savedSeller.getPassword(), savedSeller.getProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSellerById(@PathVariable Long id) {
        Optional<SellerEntity> seller = sellerRepository.findById(id);
        if (seller.isPresent()) {
            SellerEntity s = seller.get();
            SellerDTO sellerDto = new SellerDTO(s.getId(), s.getName(), s.getEmail(), s.getPhone(), s.getCreateDate(), s.getPassword(), s.getProducts());
            return ResponseEntity.ok(sellerDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerDTO> updateSeller(@PathVariable Long id, @RequestBody SellerDto sellerDto) {
        Optional<SellerEntity> sellerOptional = sellerRepository.findById(id);
        if (sellerOptional.isPresent()) {
            SellerEntity seller = sellerOptional.get();
            seller.setName(sellerDto.name());
            seller.setEmail(sellerDto.email());
            seller.setPhone(sellerDto.phone());
            seller.setCreateDate(sellerDto.createDate());
            seller.setPassword(sellerDto.password());
            seller.setProducts(sellerDto.products());
            SellerEntity updatedSeller = sellerRepository.save(seller);
            SellerDTO updatedSellerDto = new SellerDTO(updatedSeller.getId(), updatedSeller.getName(), updatedSeller.getEmail(), updatedSeller.getPhone(), updatedSeller.getCreateDate(), updatedSeller.getPassword(), updatedSeller.getProducts());
            return ResponseEntity.ok(updatedSellerDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        if (sellerRepository.existsById(id)) {
            sellerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}