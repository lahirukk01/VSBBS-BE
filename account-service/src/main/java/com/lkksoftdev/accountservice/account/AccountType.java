package com.lkksoftdev.accountservice.account;

import lombok.Getter;

@Getter
public enum AccountType {
    CURRENT("CURRENT"),
    SAVINGS("SAVINGS"),
    FIXED_DEPOSIT("FIXED_DEPOSIT"),
    TERM_DEPOSIT("TERM_DEPOSIT");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }
}