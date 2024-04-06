package com.lkksoftdev.accountservice.beneficiary;

import com.lkksoftdev.accountservice.feign.BeneficiaryClient;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService {
    private final BeneficiaryClient beneficiaryClient;

    public BeneficiaryService(BeneficiaryClient beneficiaryClient) {
        this.beneficiaryClient = beneficiaryClient;
    }

    public BeneficiaryResponseDto fetchBeneficiary(Long customerId, Long beneficiaryId) {
        return beneficiaryClient.getBeneficiaryByCustomer(customerId, beneficiaryId).getBody();
    }
}
