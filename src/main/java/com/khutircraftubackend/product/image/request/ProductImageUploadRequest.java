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
        List<ImageUpload> images
) {

    public record ImageUpload(
            @PositiveOrZero(message = "Номер позиції має бути додатнім або нулем")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) implements HasPosition {
    }
}

