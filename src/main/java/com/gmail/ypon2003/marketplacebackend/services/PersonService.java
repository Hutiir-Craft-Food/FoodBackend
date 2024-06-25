package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.dto.PersonDTO;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.repositories.PersonRepository;
import com.gmail.ypon2003.marketplacebackend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
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
    private final ProductRepository productRepository;

    public List<Person> findAllPerson() {
        return personRepository.findAll();
    }

    public Optional<Person> findPersonById(Long id) {
        return personRepository.findById(id);
    }

    @Transactional
    public Person save(PersonDTO personDTO) {
        if(personRepository.existsByEmail(personDTO.getEmail())) {
            throw new RuntimeException("Email вже існує");
        }
        Person person = Person.builder()
                .email(personDTO.getEmail())
                .password(personDTO.getPassword())
                .role("USER_ROLE")
                .build();

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

    public void addToFavorites(Long personId, Long productId) throws ChangeSetPersister.NotFoundException {
        Person person = personRepository.findById(personId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Product product = productRepository.findById(productId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        person.addToFavorites(product);
        personRepository.save(person);
    }

    public List<Product> getFavorites(Long personId) throws ChangeSetPersister.NotFoundException {
        Person person = personRepository.findById(personId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return person.getFavorites();
    }
}
