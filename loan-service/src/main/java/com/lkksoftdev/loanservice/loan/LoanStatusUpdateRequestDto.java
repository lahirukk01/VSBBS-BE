package com.lkksoftdev.loanservice.loan;

public record LoanStatusUpdateRequestDto(String status, int creditRating, String remarks) {
}
