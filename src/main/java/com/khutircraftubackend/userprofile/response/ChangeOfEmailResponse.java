package com.khutircraftubackend.userprofile.response;

import lombok.Builder;

@Builder
public record ChangeOfEmailResponse(

        String newEmail,
        String confirmToken
) {
}
