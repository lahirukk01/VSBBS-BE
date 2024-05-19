package com.lkksoftdev.loanservice.loan;

import lombok.Getter;

@Getter
public enum LoanStatus {
    PENDING, APPROVED, REJECTED;

    public static boolean isValidLoanDecisionStatus(String decision) {
        if (decision == null) {
            return false;
        }

        return decision.equals(APPROVED.name()) || decision.equals(REJECTED.name());
    }
}
