package com.khutircraftubackend.product.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductResponseMessage {
    
    public static final String PRODUCT_NOT_FOUND = "Продукт з зазначеним значенням: %s не знайдено.";
    public static final String NOT_ACCESS = "Не маєте доступу для змін в цій компанії.";
    public static final String DUPLICATE = "Продукт з таким ідентифікатором: %s і кількістю: %s вже існує в цій компанії.";
    public static final String INVALID_UNIT = "Одиниця виміру з ідентифікатором: %s не знайдена.";
    public static final String PRICE_NOT_FOUND = "Ціна з ідентифікатором: %s не знайдена.";
    public static final String UNIT_NOT_BLANK = "Назва одиниці виміру не може бути порожньою.";
    
}
