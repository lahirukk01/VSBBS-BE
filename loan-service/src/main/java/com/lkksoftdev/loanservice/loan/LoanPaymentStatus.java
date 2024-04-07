package com.lkksoftdev.loanservice.loan;

import lombok.Getter;

@Getter
public enum LoanPaymentStatus {
    NA("NA"),
    PENDING("PENDING"),
    PAID("PAID"),
    OVERDUE("OVERDUE");

    private final String value;

    LoanPaymentStatus(String value) {
        this.value = value;
    }

}
