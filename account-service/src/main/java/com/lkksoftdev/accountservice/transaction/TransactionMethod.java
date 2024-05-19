package com.lkksoftdev.accountservice.transaction;

import lombok.Getter;

@Getter
public enum TransactionMethod {
    NEFT("NEFT"),
    UPI("UPI"),
    INTERNAL("INTERNAL"),
    RTGS("RTGS");

    private final String value;

    TransactionMethod(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (TransactionMethod method : TransactionMethod.values()) {
            if (method.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
