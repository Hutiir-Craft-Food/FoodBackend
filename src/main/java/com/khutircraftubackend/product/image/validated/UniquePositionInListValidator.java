package com.khutircraftubackend.product.image.validated;

import com.khutircraftubackend.common.validation.HasPosition;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class UniquePositionInListValidator
        implements ConstraintValidator<UniquePositionInList, List<? extends HasPosition>> {

    @Override
    public boolean isValid(List<? extends HasPosition> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        Set<Integer> seen = new HashSet<>();
        for (HasPosition item : value) {
            if (!seen.add(item.getPosition())) {
                return false;
            }
        }
        return true;
    }
}