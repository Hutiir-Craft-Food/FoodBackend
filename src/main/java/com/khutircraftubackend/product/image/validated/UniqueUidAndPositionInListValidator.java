package com.khutircraftubackend.product.image.validated;

import static com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges.ImagesUploadAndChanges;

import com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class UniqueUidAndPositionInListValidator
    implements ConstraintValidator<UniqueUidAndPositionInList, ProductImagesUploadAndChanges> {

    @Override
    public boolean isValid(ProductImagesUploadAndChanges request, ConstraintValidatorContext context) {
        if (request == null) return true;
        
        Set<String> uids = new HashSet<>();
        Set<Integer> positions = new HashSet<>();

        for (ImagesUploadAndChanges image : request.images()) {
            if (!uids.add(image.uid())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("UID " + image.uid() + " дублюється")
                        .addConstraintViolation();
                return false;
            }
            if (!positions.add(image.position())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Position " + image.position() + " дублюється")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}