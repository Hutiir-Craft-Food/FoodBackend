package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.services.AdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Goods management", description = "Operations related to goods management")
public class AdController {

    private final AdService adService;

    @Operation(summary = "Get of list all goods")
    @GetMapping("/ad")
    public List<Ad> getAllAds() {
        return adService.findAll();
    }

    @PostMapping("/ad")
    @Operation(summary = "Adding new goods")
    public Ad addAd(@RequestBody Ad ad) {
        return adService.save(ad);
    }

    @PutMapping("ad/{id}")
    @Operation(summary = "Updating goods in BD")
    public void updateAd(@PathVariable("id") long id, @RequestBody Ad ad) {
        adService.updateAd(id, ad);
    }

    @DeleteMapping("ad/{id}")
    @Operation(summary = "Deleting goods from BD")
    public void deleteAd (@PathVariable("id") long id) {
        adService.deleteAd(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search of goods from name", description = "Return the status")
    public ResponseEntity<List<Ad>> searchAdsByName(@RequestParam("name") String name) {
        List<Ad> ads = adService.searchAdsByName(name);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/sorted")
    @Operation(summary = "Sorted of goods from price or date created", description = "Creates a list of sorted goods and returns the status")
    public ResponseEntity<List<Ad>> getAdsSorted(@RequestParam("sortBy") String sortBy) {
        List<Ad> ads;
        if(sortBy.equals("price")) {
            ads = adService.getAdsSortedByPrice();
        } else {
            ads = adService.getAdsSortedByDate();
        }
        return ResponseEntity.ok(ads);
    }

    @GetMapping
    @Operation(summary = "Pageable of pages", description = "Creates pageable of pages, starting with 0 pages and 10 goods per page, return status")
    public ResponseEntity<Page<Ad>> getAds(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ad> adsPage = adService.getAdsPage(pageable);
        return ResponseEntity.ok(adsPage);
    }
}
