package com.khutircraftubackend.product.image.response;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProductImageResponseMessages {

    public static final String ERROR_TOO_MANY_IMAGES =
            "Завантажено забагато зображень. Максимальна кількість - %d файлів.";
    public static final String ERROR_POSITION_ALREADY_EXISTS =
            "Дані позиції вже містять зображення.";
    public static final String ERROR_IMAGE_NOT_FOUND_BY_UID =
            "За цим UID %s, зображення не знайдено.";
    public static final String ERROR_IMAGES_COUNT_MISMATCH =
            "Кількість переданих зображень %d не відповідає очікуваним %d.";
    public static final String ERROR_LIST_EMPTY = "Список зображень не може бути порожнім.";
}
