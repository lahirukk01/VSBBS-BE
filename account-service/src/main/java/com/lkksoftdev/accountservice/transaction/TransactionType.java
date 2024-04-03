package com.lkksoftdev.accountservice.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    CREDIT("CREDIT"),
    DEBIT("DEBIT");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }
}
