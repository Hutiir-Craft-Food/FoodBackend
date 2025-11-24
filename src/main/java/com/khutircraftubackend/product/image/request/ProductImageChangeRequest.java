package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.common.validation.HasPosition;
import com.khutircraftubackend.product.image.validated.UniquePositionInList;
import com.khutircraftubackend.product.image.validated.UniqueUidInList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductImageChangeRequest(
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        @UniqueUidInList
        @UniquePositionInList
        List<Image> images
) {
    public record Image(
            @NotEmpty(message = "ID файлу не може бути порожнім")
            Long id,

            @PositiveOrZero(message = "Номер позиції має бути додатнім або нулем")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) implements HasPosition {
    }
}
