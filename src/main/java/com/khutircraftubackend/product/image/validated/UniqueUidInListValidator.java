package com.khutircraftubackend.product.image.validated;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class UniqueUidInListValidator
    implements ConstraintValidator<UniqueUidInList, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            Method method = value.getClass().getMethod("images");
            List<?> images = (List<?>) method.invoke(value);

            Set<String> uidList = new HashSet<>();
            boolean isValid = true;

            for (Object image : images) {
                Method uidGetter = image.getClass().getMethod("uid");
                String uid = (String) uidGetter.invoke(image);

                if (!uidList.add(uid)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    String.format("UID %s дублюється.", uid))
                            .addPropertyNode("images")
                            .addConstraintViolation();
                    isValid = false;
                }
            }

            return isValid;
        } catch (Exception e) {
            log.error("Validation reflection error", e);
            return false;
        }
    }
}