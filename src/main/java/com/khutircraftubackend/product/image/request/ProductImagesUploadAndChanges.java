package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.product.image.validated.UniqueUidAndPositionInList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@UniqueUidAndPositionInList
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

            @PositiveOrZero(message = "Позиція не може бути негативною")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) {
    }
}
