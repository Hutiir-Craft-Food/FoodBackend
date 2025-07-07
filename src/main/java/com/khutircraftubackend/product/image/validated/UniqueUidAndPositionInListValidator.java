package com.khutircraftubackend.product.image.validated;

import static com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges.ImagesUploadAndChanges;

import com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class UniqueUidAndPositionInListValidator
    implements ConstraintValidator<UniqueUidAndPositionInList, ProductImagesUploadAndChanges> {

    @Override
    public boolean isValid(ProductImagesUploadAndChanges request, ConstraintValidatorContext context) {
        if (request == null) return true;
        
        Set<String> uids = new HashSet<>();
        Set<Integer> positions = new HashSet<>();
        boolean isValid = true;

        for (ImagesUploadAndChanges image : request.images()) {
            if (!uids.add(image.uid())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("UID " + image.uid() + " дублюється")
                        .addPropertyNode("images")
                        .addConstraintViolation();
                isValid = false;
            }
            if (!positions.add(image.position())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Position " + image.position() + " дублюється")
                        .addPropertyNode("images")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}