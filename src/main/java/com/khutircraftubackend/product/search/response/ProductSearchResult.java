package com.khutircraftubackend.product.search.response;

public interface ProductSearchResult {
    Long getId();
    
    String getName();
    
    String getThumbnailImage();
    
    boolean isAvailable();
    
    Long getCategoryId();
    
    String getCategoryName();
}
