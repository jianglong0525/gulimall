package com.guli.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private Set<Integer> vals = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        for (int val : constraintAnnotation.vals()) {
            vals.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return vals.contains(value);
    }
}
