package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.common.validation.HasPosition;
import com.khutircraftubackend.common.validation.HasUid;
import com.khutircraftubackend.product.image.validated.UniquePositionInList;
import com.khutircraftubackend.product.image.validated.UniqueUidInList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductImageChangeRequest(
        @NotNull(message = "Список зображень не може бути null")
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        @UniqueUidInList
        @UniquePositionInList
        List<Image> images
) {
    public record Image(
            @NotNull(message = "UID не може бути null")
            @NotEmpty(message = "UID файлу не може бути порожнім")
            String uid,

            @PositiveOrZero(message = "Номер позиції має бути додатнім або нулем")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) implements HasPosition, HasUid {
    }
}
