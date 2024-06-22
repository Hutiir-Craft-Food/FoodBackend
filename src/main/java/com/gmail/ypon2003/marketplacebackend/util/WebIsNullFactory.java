package com.gmail.ypon2003.marketplacebackend.util;

import com.gmail.ypon2003.marketplacebackend.dto.ProductDTO;
import com.gmail.ypon2003.marketplacebackend.factory.ProductFactory;
import com.gmail.ypon2003.marketplacebackend.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
Цей клас служить для перетворення списку продуктів користувача
 з моделі Product в ProductDTO. Якщо користувач не має продуктів,
  повертається порожній список.
 */
@Component
@AllArgsConstructor
public class WebIsNullFactory {

    private final ProductFactory productFactory;

    public List<ProductDTO> isNullProductUser(User user) {
        return Optional.ofNullable(user.getProducts())
                .map(list -> list.stream()
                        .map(productFactory)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
