package com.khutircraftubackend.confirm;

import lombok.Builder;

@Builder
public record ConfirmResponse (

        Boolean confirmed,
        String message
){
}
