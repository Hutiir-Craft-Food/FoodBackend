package com.khutircraftubackend.storage;

public class StorageResponseMessage {
    
    public static final String INVALID_FILE = "Файл не надано або він порожній";
    public static final String ERROR_SAVE = "Помилка при збереженні файлу %s.";
    public static final String ERROR_DELETE = "Не вдалося видалити файл з серверу Cloudinary, publicId: %s, %s";
    private StorageResponseMessage() {
    }
}
