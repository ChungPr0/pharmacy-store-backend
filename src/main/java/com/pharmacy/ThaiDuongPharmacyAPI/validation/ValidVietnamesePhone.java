package com.pharmacy.ThaiDuongPharmacyAPI.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = VietnamesePhoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVietnamesePhone {
    String message() default "Số điện thoại không đúng định dạng của Việt Nam!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
