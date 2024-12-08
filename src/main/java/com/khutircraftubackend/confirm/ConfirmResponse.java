package com.khutircraftubackend.confirm;

import lombok.Builder;

@Builder
public record ConfirmResponse (

        boolean confirmed,
        String message
){
}
