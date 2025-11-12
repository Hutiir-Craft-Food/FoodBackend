package com.khutircraftubackend.category.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryExceptionMessages{
    public static final String CATEGORY_NOT_FOUND = "Категорія не знайдена";
    public static final String CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS =
            "Категорія має підкатегорії або продукти і не може бути видалена";
    public static final String CATEGORY_ALREADY_EXISTS = "Категорія з такою назвою '%s' вже існує";
    public static final String CATEGORY_NAME_INVALID = "Назва категорії '%s' є недійсною або порожньою";
}
