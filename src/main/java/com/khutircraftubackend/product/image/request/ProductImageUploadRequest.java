package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.common.validation.HasPosition;
import com.khutircraftubackend.product.image.validated.UniquePositionInList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductImageUploadRequest(
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        @UniquePositionInList
        List<UploadImageInfo> images
) {

    public record UploadImageInfo(
            @Positive(message = "Номер позиції має бути додатнім.")
            @Max(value = 5, message = "Максимально допустима позиція зображення - 5")
            int position
    ) implements HasPosition {
    }
}

