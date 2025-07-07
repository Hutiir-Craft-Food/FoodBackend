package com.khutircraftubackend.product.image;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProductImagesResponseMessages {

    protected static final String ERROR_TOO_MANY_IMAGES =
            "Підвищена кількість файлів. Максимальна кількість - %d файлів";
    protected static final String ERROR_POSITION_EXIST =
            "В єтих позициях уже есть изображения";
    protected static final String ERROR_NAME_NOT_FOUND =
            "Файл %s не знайдено серед переданих";
}
