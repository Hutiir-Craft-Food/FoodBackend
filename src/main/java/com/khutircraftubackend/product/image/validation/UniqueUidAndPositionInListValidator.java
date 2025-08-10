package com.khutircraftubackend.product.image.validation;

import static com.khutircraftubackend.product.image.request.ProductImageUploadRequest.Image;

import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class UniqueUidAndPositionInListValidator
    implements ConstraintValidator<UniqueUidAndPositionInList, ProductImageUploadRequest> {

    @Override
    public boolean isValid(ProductImageUploadRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;
        
        Set<String> uids = new HashSet<>();
        Set<Integer> positions = new HashSet<>();
        boolean isValid = true;

        for (Image image : request.images()) {
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