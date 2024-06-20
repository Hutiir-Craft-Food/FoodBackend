package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.repositories.AdRepository;
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
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;
    private static final Logger log = LoggerFactory.getLogger(AdService.class);

    @Autowired
    public AdService(AdRepository adRepository) {

        this.adRepository = adRepository;
    }

    @Transactional
    public Ad save(Ad ad) {
        try {
            if(ad.getCreateAt() == null) {
                ad.setCreateAt(new Date());
            }
            log.info("Saving ad: {}", ad);
            adRepository.save(ad);
        } catch (Exception e) {
            log.error("Error saving ad: {}", e.getMessage());
            throw new RuntimeException("Failed to save ad", e);
        }
        return ad;
    }

    @Transactional
    public void saveAd(List<Ad> adList) {
        adList.forEach(ad -> {
            if (ad.getCreateAt() == null) {
                ad.setCreateAt(new Date());
            }
        });
        adRepository.saveAll(adList);
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
            ad.setFlag(adUpdate.isFlag());
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
