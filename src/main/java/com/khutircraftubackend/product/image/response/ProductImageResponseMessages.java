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
    public static final String ERROR_UNIQUE_POSITION = "Позиція вже зайнята для цього продукту";
    public static final String ERROR_UNIQUE_CONFLICT = "Список варіантів має містити записи лише для одного зображення.";
    public static final String ERROR_CREATE_ORIGIN_IMAGE = "Failed to create origin image";
    public static final String ERROR_IMAGE_TOO_SMALL = "Failed to create small ratio crop";
    public static final String ERROR_RESIZE_IMAGE = "Failed to resize image";
    public static final String ERROR_READ_ORIENT_IMAGE = "Failed to read image with orientation";
    public static final String ERROR_METADATA_READ = "Failed to read image metadata for orientation";
    public static final String ERROR_TO_JPEG_CONVERSION = "Failed to convert image to byte array";
    public static final String ERROR_CREATE_SQUARE_IMAGE = "Failed to create square image";
    public static final String ERROR_READ_INPUT_STREAM = "Cannot read input stream";
    public static final String ERROR_PROCESSING_METADATA = "Error processing image metadata";
    public static final String ERROR_CREATE_IMAGE = "На жаль, ми не змогли обробити зображення під номером %d. Будь ласка, спробуйте інше зображення.";
    public static final String ERROR_UPLOAD_IMAGE_VARIANTS = "Failed to upload image variants";
}
