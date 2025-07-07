package com.khutircraftubackend.product.image;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProductImagesResponseMessages {

    public static final String ERROR_TOO_MANY_IMAGES =
            "Підвищена кількість файлів. Максимальна кількість - %d файлів.";
    public static final String ERROR_POSITION_EXIST =
            "В єтих позициях уже есть изображения.";
    public static final String ERROR_NAME_NOT_FOUND =
            "Файл %s не знайдено серед переданих.";
    public static final String ERROR_MISSING_UID =
            "За цим UID %s, зображенням не знайдено.";
    public static final String ERROR_UID_EXIST =
            "У цому товарі вже є такий UID";
    public static final String ERROR_SIZE = "" +
            "Не всі імеджі були передані";
    public static final String ERROR_MIME_TYPE =  "Неприпустимий MIME-тип файлу: %s (тип: %s)";
}
