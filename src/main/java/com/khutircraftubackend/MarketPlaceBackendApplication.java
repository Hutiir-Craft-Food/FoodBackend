package com.khutircraftubackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MarketPlaceBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(MarketPlaceBackendApplication.class, args);
    }

}
