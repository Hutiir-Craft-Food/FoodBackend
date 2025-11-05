package com.khutircraftubackend.storage;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StorageResponseMessage {

    public static final String INVALID_FILE = "Файл не надано або він порожній";
    public static final String ERROR_SAVE = "Не вдалося зберегти зображення. Спробуйте пізніше.";
    public static final String ERROR_DELETE = "Не вдалося видалити файл з серверу Cloudinary, publicId: %s, %s";
    public static final String ERROR_DELETE_LOCAL = "Не вдалося видалити файл.";
    public static final String INVALID_ARGUMENT = "URL не відповідає шаблону для зображення: %s";
    public static final String FILE_NOT_FOUND = "Файл з таким URL не знайдено: %s";
    public static final String ERROR_CREATE_DIRECTORY = "Не вдалося створити каталог: %s.";
}
