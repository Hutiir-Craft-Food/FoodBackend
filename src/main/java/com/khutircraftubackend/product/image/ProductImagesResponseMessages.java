package com.khutircraftubackend.product.image;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProductImagesResponseMessages {

    public static final String ERROR_TOO_MANY_IMAGES =
            "Підвищена кількість файлів. Максимальна кількість - %d файлів.";
    public static final String ERROR_POSITION_ALREADY_EXISTS =
            "В єтих позициях уже есть изображения.";
    public static final String ERROR_IMAGE_NOT_FOUND_BY_UID =
            "За цим UID %s, зображенням не знайдено.";
    public static final String ERROR_IMAGES_COUNT_MISMATCH =
            "Кількість переданих зображень %d не відповідає очікуваним %d.";

    // ----- Other Exception Messages -----
    public static final String NO_PRIVILEGES = "You do not have permission for this operation!";
    public static final String POSITION_ALREADY_EXISTS = "Position %d already exists!";
    public static final String INVALID_POSITION = "Position must be a positive integer!";
    public static final String IMAGE_NOT_FOUND = "Image with id %s not found!";
}
