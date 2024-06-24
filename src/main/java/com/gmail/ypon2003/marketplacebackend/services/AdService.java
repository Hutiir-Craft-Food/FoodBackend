package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.repositories.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdService {

    private final AdRepository adRepository;

    @Transactional
    public Ad save(Ad ad) {
        try {
            if(ad.getCreateAt() == null) {
                ad.setCreateAt(new Date());
            }
            ad.setName(ad.getName());
            ad.setPrice(ad.getPrice());
            ad.setInfoSeller(ad.getInfoSeller());
            ad.setDescription(ad.getDescription());
            ad.setMeasurement(ad.getMeasurement());
            adRepository.save(ad);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save ad", e);
        }
        return ad;
    }

    public Optional<Ad> showAd(long id) {
        return adRepository.findById(id);
    }

    @Transactional
    public void updateAd(long id, Ad adUpdate) {
        Optional<Ad> updateToBeAd = showAd(id);
        if(updateToBeAd.isPresent()) {
            Ad ad = updateToBeAd.get();
            ad.setName(adUpdate.getName());
            ad.setCreateAt(adUpdate.getCreateAt());
            ad.setMeasurement(adUpdate.getMeasurement());
            ad.setDescription(adUpdate.getDescription());
            ad.setPrice(adUpdate.getPrice());
            ad.setInfoSeller(ad.getInfoSeller());
        }
    }

    @Transactional
    public void deleteAd(long id) {
        adRepository.deleteById(id);
    }
    public List<Ad> searchAdsByName(String name) {
        return adRepository.findByNameContaining(name);
    }

    public List<Ad> getAdsSortedByPrice() {
        return adRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    public List<Ad> getAdsSortedByDate() {
        return adRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Page<Ad> getAdsPage(Pageable pageable) {
        return adRepository.findAll(pageable);
    }

    public List<Ad> findAll() {
        return adRepository.findAll();
    }
}
