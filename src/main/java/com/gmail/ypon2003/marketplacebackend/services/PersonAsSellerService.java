package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.dto.PersonAsSellerDTO;
import com.gmail.ypon2003.marketplacebackend.models.Person;
import com.gmail.ypon2003.marketplacebackend.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonAsSellerService {

    private final PersonRepository personRepository;

    @Transactional
    public Person saveSeller(PersonAsSellerDTO personAsSellerDTO) {
        if(personRepository.existsByEmail(personAsSellerDTO.email())) {
            throw new RuntimeException("Email вже існує");
        }
        Person person = Person.builder()
                .name(personAsSellerDTO.name())
                .lastName(personAsSellerDTO.lastName())
                .email(personAsSellerDTO.email())
                .password(personAsSellerDTO.password())
                .phoneNumber(personAsSellerDTO.phoneNumber())
                .role("USER_SELLER")
                .build();

        return personRepository.save(person);
    }


}
