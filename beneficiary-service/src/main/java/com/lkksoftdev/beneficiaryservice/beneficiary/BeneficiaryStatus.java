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
}
