package com.khutircraftubackend.search.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "categoryId", "categoryName",
        "productId", "productName",
        "available", "thumbnailImage"})
public interface ProductSearchView {
    Long getCategoryId();
    String getCategoryName();
    Long getProductId();
    String getProductName();
    String getThumbnailImage();
    boolean isAvailable();
}
