package com.lkksoftdev.accountservice.account;

public record LoanPaymentRequestDto(double emiAmount, Long loanId, String description) {
}
