package com.khutircraftubackend.product.image.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductImagesUploadAndChanges(
        @NotNull(message = "Список зображень не може бути null")
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        List<ImagesUploadAndChanges> images
) {
    public record ImagesUploadAndChanges(
            @NotNull(message = "UID не може бути null")
            @NotEmpty(message = "UID файлу не може бути порожнім")
            String uid,

            @PositiveOrZero
            @Max(4)
            int position
    ) {
    }
}
