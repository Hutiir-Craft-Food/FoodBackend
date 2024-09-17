package com.khutircraftubackend.config;

import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class AuthConfig {

    public Random random(){
        return new Random();
    }
}
