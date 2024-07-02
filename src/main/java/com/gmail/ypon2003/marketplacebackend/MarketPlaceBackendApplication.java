package com.gmail.ypon2003.marketplacebackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketPlaceBackendApplication {

    public static void main(String[] args) {

        //Завантаження змінних з .env файлу
        Dotenv dotenv = Dotenv.load();
        //Встановлення змінних
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(MarketPlaceBackendApplication.class, args);
    }

}
