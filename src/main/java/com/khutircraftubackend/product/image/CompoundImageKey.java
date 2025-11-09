package com.khutircraftubackend.product.image;

import lombok.Data;

import java.util.Objects;

@Data
public class CompoundImageKey {
    private final Long productId;
    private final String productUid;
    private final Integer position;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundImageKey imageKey = (CompoundImageKey) o;
        return Objects.equals(productId, imageKey.productId) &&
                Objects.equals(productUid, imageKey.productUid) &&
                Objects.equals(position, imageKey.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productUid, position);
    }
}
