package com.khutircraftubackend.exception.product;

public class ProductValidationMessages {
    public static final String NAME_NULL_OR_EMPTY = "Product name cannot be null or empty";
    public static final String DESCRIPTION_NULL = "Product description cannot be null";
    public static final String IMAGE_NULL_OR_EMPTY = "Product image cannot be null or empty";
    public static final String THUMBNAIL_IMAGE_NULL_OR_EMPTY = "Product thumbnail image cannot be null or empty";
    public static final String AVAILABLE_NULL = "Product availability status cannot be null";
    public static final String SELLER_NULL = "Seller cannot be null";

    private ProductValidationMessages() {
    }

}
