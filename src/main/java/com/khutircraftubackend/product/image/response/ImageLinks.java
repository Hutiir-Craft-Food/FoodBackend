package com.khutircraftubackend.product.image.response;

public interface ImageLinks {
    String getThumbnail();
    void setThumbnail(String thumbnail);

    String getSmall();
    void setSmall(String small);

    String getMedium();
    void setMedium(String medium);

    String getLarge();
    void setLarge(String large);
}
