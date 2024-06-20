package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.repositories.AdRepository;
import com.gmail.ypon2003.marketplacebackend.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final AdRepository adRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, AdRepository adRepository) {
        this.personRepository = personRepository;
        this.adRepository = adRepository;
    }

    public void addToFavorites(Long personId, Long adId) throws ChangeSetPersister.NotFoundException {
        Person person = personRepository.findById(personId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        person.addToFavorites(ad);
        personRepository.save(person);
    }

    public List<Ad> getFavorites(Long personId) throws ChangeSetPersister.NotFoundException {
        Person person = personRepository.findById(personId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return person.getFavorites();
    }
}
