package com.khutircraftubackend.userprofile;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmailTokenInfo {
    private final String token;
    private final LocalDateTime timestamp;

    public EmailTokenInfo(String token) {
        this.token = token;
        this.timestamp = LocalDateTime.now();
    }
}

