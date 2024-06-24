package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.repositories.AdRepository;
import com.gmail.ypon2003.marketplacebackend.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final AdRepository adRepository;

    public List<Person> findAllPerson() {
        return personRepository.findAll();
    }

    public Optional<Person> findPersonById(Long id) {
        return personRepository.findById(id);
    }

    @Transactional
    public Person save(Person person) {
        person.setEmail(person.getEmail());
        person.setPassword(person.getPassword());
        person.setRole("USER_ROLE");
        return personRepository.save(person);
    }

    @Transactional
    public void updatePerson(long id, Person personUpdate) {
        Optional<Person> updateToBePerson = findPersonById(id);
        if(updateToBePerson.isPresent()) {
            Person person = updateToBePerson.get();
            person.setName(personUpdate.getName());
            person.setEmail(personUpdate.getEmail());
            person.setLastName(personUpdate.getLastName());
            person.setPhoneNumber(personUpdate.getPhoneNumber());
            person.setPassword(personUpdate.getPassword());
            person.setRole(personUpdate.getRole());
            personRepository.save(person);
        }
    }

    @Transactional
    public void deletePerson(long id) {

        personRepository.deleteById(id);
    }

    public void addToFavorites(Long personId, Long adId) throws ChangeSetPersister.NotFoundException {
        Person person = personRepository.findById(personId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Ad ad = adRepository.findById(adId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        person.addToFavorites(ad);
        personRepository.save(person);
    }

    public List<Ad> getFavorites(Long personId) throws ChangeSetPersister.NotFoundException {
        Person person = personRepository.findById(personId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return person.getFavorites();
    }
}
