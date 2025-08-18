package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.product.image.validated.UniquePositionInList;
import com.khutircraftubackend.product.image.validated.UniqueUidInList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@UniqueUidInList
@UniquePositionInList
public record ProductImagesChanges(
        @NotNull(message = "Список зображень не може бути null")
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        List<Images> images
) {
    public record Images(
            @NotNull(message = "UID не може бути null")
            @NotEmpty(message = "UID файлу не може бути порожнім")
            String uid,

            @PositiveOrZero(message = "Позиція не може бути негативною")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) {
    }
}
