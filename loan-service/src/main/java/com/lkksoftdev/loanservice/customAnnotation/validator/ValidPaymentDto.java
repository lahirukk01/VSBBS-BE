package com.lkksoftdev.loanservice.customAnnotation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentDtoValidator.class)
public @interface ValidPaymentDto {
    String message() default "Invalid payment dto";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
