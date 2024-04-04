package com.lkksoftdev.beneficiaryservice.beneficiary;

public record BeneficiaryApproveRequestDto(BeneficiaryStatus status, String comments) {
}
