package com.lkksoftdev.accountservice.beneficiary;

public record BeneficiaryResponseDto(
        Long id, Long customerId, String status, String comments, String createdAt, String updatedAt,
        String name, Long accountId, String accountIfscCode, String email) {
}
