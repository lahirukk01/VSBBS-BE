package com.lkksoftdev.beneficiaryservice.beneficiary;

import lombok.Getter;

@Getter
public enum BeneficiaryStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static boolean isValidStatus(String status) {
        for (BeneficiaryStatus beneficiaryStatus : BeneficiaryStatus.values()) {
            if (beneficiaryStatus.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
