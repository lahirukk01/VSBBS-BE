package com.lkksoftdev.externalservice.dto;

public record CardPaymentDetailsDto(String cardNumber, String cardHolderName, String cardExpiry, String cardCvv, double amount) {
}