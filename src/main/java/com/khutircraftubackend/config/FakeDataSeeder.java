package com.khutircraftubackend.config;

import com.github.javafaker.Faker;
import com.khutircraftubackend.user.Role;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerRepository;
import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
@Component
@Profile({"local", "qa"}) // ✅ будет работать только в этих профилях
@RequiredArgsConstructor
public class FakeDataSeeder {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker fakerUa = new Faker(new Locale("uk"));
    private final Faker fakerEn = new Faker(Locale.ENGLISH);
    private final Random random = new Random();

    @PostConstruct
    public void seed() {
        if (userRepository.count() > 1) {
            log.info("⚠️ Seed data already exists — skipping fake data generation.");
            return;
        }

        log.info("🚀 Starting fake data generation...");

        // 👤 Создаём пользователей
        List<UserEntity> users = IntStream.range(0, 20)
                .mapToObj(i -> {
                    UserEntity u = new UserEntity();
                    u.setEmail(i + fakerEn.internet().emailAddress());
                    u.setPassword(passwordEncoder.encode("!Password1")); //пароль для юзера
                    u.setRole(i % 2 == 0 ? Role.SELLER : Role.BUYER);
                    u.setEnabled(true);
                    u.setConfirmed(true);
                    return u;
                }).toList();
        userRepository.saveAll(users);

        // 🏪 Продавцы
        List<SellerEntity> sellers = users.stream()
                .filter(u -> u.getRole().equals(Role.SELLER))
                .map(u -> {
                    SellerEntity s = new SellerEntity();
                    s.setSellerName(fakerUa.company().name());
                    s.setUser(u);
                    return s;
                }).toList();
        sellerRepository.saveAll(sellers);

        // 🏷 Категории
        List<CategoryEntity> categories = IntStream.range(0, 30)
                .mapToObj(i -> CategoryEntity.builder()
                        .name(fakerUa.commerce().department() + i)
                        .description(fakerUa.lorem().sentence())
                        .iconUrl(fakerEn.internet().avatar())
                        .parentCategory(i % 2 == 0 ? null : getRandomRootCategory())
                        .build())
                .toList();
        categoryRepository.saveAll(categories);

        // 🛒 Продукты
        List<ProductEntity> products = IntStream.range(0, 100)
                .mapToObj(i -> ProductEntity.builder()
                        .name(fakerUa.commerce().productName())
                        .available(true)
                        .description(fakerUa.lorem().paragraph())
                        .category(getRandomParentCategory())
                        .seller(getRandomSeller())
                        .build())
                .toList();
        productRepository.saveAll(products);



        log.info("✅ Fake data generated successfully.");
    }

    private CategoryEntity getRandomRootCategory() {
        List<CategoryEntity> rootCategories = categoryRepository.findAllByParentCategoryIsNull();
        if (rootCategories.isEmpty()) {
            return null;
        }

        return rootCategories.get(random.nextInt(rootCategories.size()));
    }

    private CategoryEntity getRandomParentCategory(){
        List<CategoryEntity> parentCategory = categoryRepository.findAllByParentCategoryIsNotNull();
        if (parentCategory.isEmpty()){
            return null;
        }

        return parentCategory.get(random.nextInt(parentCategory.size()));
    }

    private SellerEntity getRandomSeller(){
        List<SellerEntity> sellerEntityList = sellerRepository.findAllBy();
        if (sellerEntityList.isEmpty()){
            return null;
        }

        return sellerEntityList.get(random.nextInt(sellerEntityList.size()));
    }
}