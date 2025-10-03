package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.product.image.validated.UniquePositionInList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@UniquePositionInList
public record ProductImagesUploadRequest(
        @NotNull(message = "Список зображень не може бути null")
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        List<Image> images
) {
    public record Image(
            @PositiveOrZero(message = "Позиція не може бути негативною")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) {
    }
}
