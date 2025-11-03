package com.khutircraftubackend.product.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductResponseMessage {
    
    public static final String PRODUCT_NOT_FOUND = "Продукт з вказаним ідентифікатором: %s не знайдено.";
    public static final String PRODUCT_ACCESS_DENIED = "Ви не маєте доступу для зміни цього продукту.";
    public static final String UNIT_NOT_FOUND = "Вказана одиниця виміру не існує.";
    public static final String UNIT_INVALID_NAME = "Одиниця виміру з таким ім'ям: %s вже існує.";
    public static final String UNIT_NOT_BLANK = "Одиниця виміру не може бути порожньою.";
    
}
