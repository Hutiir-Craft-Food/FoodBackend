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
@Transactional(readOnly = true)
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
        if (personRepository.existsByEmail(personDTO.email())) {
            throw new RuntimeException("Email вже існує");
        }
        Person person = Person.builder()
                .email(personDTO.email())
                .password(personDTO.password())
                .role("USER_ROLE")
                .build();

        return personRepository.save(person);
    }

    @Transactional
    public void updatePerson(long id, PersonDTO personDTO) {
        Optional<Person> updateToBePerson = findPersonById(id);
        if (updateToBePerson.isPresent()) {
            Person person = updateToBePerson.get();
            person.setName(personDTO.name());
            person.setEmail(personDTO.email());
            person.setLastName(personDTO.lastName());
            person.setPhoneNumber(personDTO.phoneNumber());
            person.setPassword(personDTO.password());
            person.setRole(personDTO.role());

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
