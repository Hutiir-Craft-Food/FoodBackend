package com.khutircraftubackend.product.image.validated;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component // TODO: Is it necessary?
@RequiredArgsConstructor
public class UniquePositionInListValidator
    implements ConstraintValidator<UniquePositionInList, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            // TODO: Refactor bellow taking into account the comment in UniquePositionInList.java
            Method method = value.getClass().getMethod("images");
            List<?> images = (List<?>) method.invoke(value);

            Set<Integer> positions = new HashSet<>();
            boolean isValid = true;

            for (Object image : images) {
                // TODO: get rid of reflection. Use interface HasPosition.java
                Method posGetter = image.getClass().getMethod("position");
                int position = (Integer) posGetter.invoke(image);

                if (!positions.add(position)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                String.format("Position %d дублюється.", position))
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