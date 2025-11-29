package com.khutircraftubackend.product.image.response;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProductImageResponseMessages {

    public static final String ERROR_TOO_MANY_IMAGES =
            "Завантажено забагато зображень. Максимальна кількість - %d файлів.";
    public static final String ERROR_POSITION_ALREADY_EXISTS =
            "Дані позиції вже містять зображення.";
    public static final String ERROR_IMAGE_NOT_FOUND_BY_ID =
            "За цим ID %s, зображення не знайдено.";
    public static final String ERROR_IMAGES_COUNT_MISMATCH =
            "Кількість переданих зображень %d не відповідає очікуваним %d.";
    public static final String ERROR_INVALID_POSITION = "Неприпустимі позиції зображень: %s. Допустимий діапазон: 0–4.";
    public static final String ERROR_NOT_FOUND_POSITION = "Продукт не має зображень за позиціями: %s.";
}
