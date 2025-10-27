package com.khutircraftubackend.category.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryExceptionMessages{
    public static final String CATEGORY_NOT_FOUND = "Категорія не знайдена";
    public static final String CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS =
            "Категорія має підкатегорії або продукти і не може бути видалена";
    public static final String CATEGORY_ALREADY_EXISTS = "Категорія з такою назвою '%s' вже існує";
    public static final String COULD_NOT_UPLOAD_ICON = "Не вдалося завантажити іконку категорії";
    public static final String COULD_NOT_DELETE_ICON = "Не вдалося видалити іконку категорії з URL: ";
    
}
