package com.lkksoftdev.accountservice.transaction;

public enum TransactionMethod {
    NEFT("NEFT"),
    IMPS("IMPS"),
    RTGS("RTGS");

    private final String value;

    TransactionMethod(String value) {
        this.value = value;
    }
}
