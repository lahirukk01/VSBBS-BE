package com.lkksoftdev.beneficiaryservice.beneficiary;

import lombok.Getter;

@Getter
public enum BeneficiaryStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String status;

    BeneficiaryStatus(String status) {
        this.status = status;
    }

    public static boolean isValidStatus(String status) {
        for (BeneficiaryStatus beneficiaryStatus : BeneficiaryStatus.values()) {
            if (beneficiaryStatus.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
