package com.lkksoftdev.loanservice.payment;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("CREDIT_CARD"),
    DEBIT_CARD("DEBIT_CARD"),
    UPI("UPI"),
    SAVINGS_ACCOUNT("SAVINGS_ACCOUNT");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

}
