package com.pharmacy.ThaiDuongPharmacyAPI.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VietnamesePhoneValidator implements ConstraintValidator<ValidVietnamesePhone, String> {

    private static final String PHONE_REGEX = "^(0[3|5|7|8|9])+([0-9]{8})$";

    @Override
    public void initialize(ValidVietnamesePhone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return value.matches(PHONE_REGEX);
    }
}
