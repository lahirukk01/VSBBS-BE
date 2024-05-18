package com.lkksoftdev.beneficiaryservice.beneficiary;

import lombok.Getter;

@Getter
public enum BeneficiaryStatus {
//    PENDING("PENDING"),
//    APPROVED("APPROVED"),
//    REJECTED("REJECTED");
    PENDING,
    APPROVED,
    REJECTED;

//    private final String status;
//
//    BeneficiaryStatus(String status) {
//        this.status = status;
//    }

    public static boolean isValidStatus(String status) {
        for (BeneficiaryStatus beneficiaryStatus : BeneficiaryStatus.values()) {
            if (beneficiaryStatus.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
