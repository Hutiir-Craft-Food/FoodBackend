package com.khutircraftubackend.confirm;

import lombok.Builder;

@Builder
public record ConfirmationResponse(

        boolean confirmed,
        String message
){
}
