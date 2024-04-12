package com.lkksoftdev.loanservice.payment;

public record PaymentDto(
        String paymentMethod, String cardNumber,
        String cardHolderName, String cardExpiry, String cardCvv,
        String upiId, Long savingsAccountId) {
}
