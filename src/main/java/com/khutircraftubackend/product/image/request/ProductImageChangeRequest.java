package com.khutircraftubackend.product.image.request;

import com.khutircraftubackend.common.validation.HasPosition;
import com.khutircraftubackend.product.image.validated.UniquePositionInList;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductImageChangeRequest(
        @Schema(description = "List of images with updated positions", required = true)
        @NotEmpty(message = "Список зображень не може бути порожнім")
        @Valid
        @UniquePositionInList
        List<Image> images
) {
    public record Image(
            @Schema(description = "Image ID", example = "42", required = true)
            @NotEmpty(message = "ID файлу не може бути порожнім")
            Long id,

            @Schema(description = "New position (0–4)", example = "1", minimum = "0", maximum = "4", required = true)
            @PositiveOrZero(message = "Номер позиції має бути додатнім або нулем")
            @Max(value = 4, message = "Максимально допустима позиція зображення - 4")
            int position
    ) implements HasPosition {
    }
}
