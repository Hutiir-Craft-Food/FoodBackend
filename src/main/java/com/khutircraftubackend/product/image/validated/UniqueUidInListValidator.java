package com.khutircraftubackend.product.image.validated;

import com.khutircraftubackend.common.validation.HasUid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class UniqueUidInListValidator
        implements ConstraintValidator<UniqueUidInList, List<? extends HasUid>> {

    @Override
    public boolean isValid(List<? extends HasUid> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        Set<String> seen = new HashSet<>();
        for (HasUid item : value) {
            if (!seen.add(item.uid())) {
                return false;
            }
        }
        return true;
    }
}