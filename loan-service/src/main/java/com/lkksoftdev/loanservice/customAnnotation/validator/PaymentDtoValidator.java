package com.lkksoftdev.loanservice.customAnnotation.validator;

import com.lkksoftdev.loanservice.payment.PaymentDto;
import com.lkksoftdev.loanservice.payment.PaymentMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class PaymentDtoValidator implements ConstraintValidator<ValidPaymentDto, PaymentDto> {
    private static final List<String> ALLOWED_PAYMENT_METHODS = Arrays.stream(PaymentMethod.values())
            .map(PaymentMethod::getValue)
            .toList();

    private boolean isValidPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }

        return ALLOWED_PAYMENT_METHODS.contains(paymentMethod);
    }

    @Override
    public boolean isValid(PaymentDto paymentDto, ConstraintValidatorContext constraintValidatorContext) {
        if (!isValidPaymentMethod(paymentDto.paymentMethod())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                .buildConstraintViolationWithTemplate("Invalid payment method")
                .addPropertyNode("paymentMethod")
                .addConstraintViolation();
            return false;
        }

        switch (paymentDto.paymentMethod()) {
            case "DEBIT_CARD":
            case "CREDIT_CARD":
                if (paymentDto.cardNumber() == null || paymentDto.cardHolderName() == null ||
                        paymentDto.cardExpiry() == null || paymentDto.cardCvv() == null) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Invalid card payment details")
                        .addPropertyNode("cardNumber")
                        .addPropertyNode("cardHolderName")
                        .addPropertyNode("cardExpiry")
                        .addPropertyNode("cardCvv")
                        .addConstraintViolation();
                    return false;
                }
                break;
            case "SAVINGS_ACCOUNT":
                if (paymentDto.savingsAccountId() == null || paymentDto.customerId() == null) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Invalid savings account payment details")
                        .addPropertyNode("savingsAccountId")
                        .addPropertyNode("customerId")
                        .addConstraintViolation();
                    return false;
                }
                break;
            case "UPI":
                if (paymentDto.upiId() == null) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Invalid UPI payment details")
                        .addPropertyNode("upiId")
                        .addConstraintViolation();
                    return false;
                }
                break;
            default:
                return false;
        }

        return true;
    }
}
