package com.lkksoftdev.loanservice.loan;

import lombok.Getter;

@Getter
public enum LoanStatus {
    IN_PROGRESS("IN_PROGRESS"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    LoanStatus(String value) {
        this.value = value;
    }

    public static boolean isValidLoanDecisionStatus(String decision) {
        if (decision == null) {
            return false;
        }

        return decision.equals(APPROVED.getValue()) || decision.equals(REJECTED.getValue());
    }
}
