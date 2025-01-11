package com.khutircraftubackend.category.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class CategoryExceptionMessages {
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS =
            "Category has subcategories or products, can't be deleted";
}
