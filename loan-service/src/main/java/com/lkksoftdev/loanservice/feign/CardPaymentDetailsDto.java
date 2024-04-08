package com.lkksoftdev.loanservice.feign;

public record CardPaymentDetailsDto(String cardNumber, String cardHolderName, String cardExpiry, String cardCvv, double amount) {
}
