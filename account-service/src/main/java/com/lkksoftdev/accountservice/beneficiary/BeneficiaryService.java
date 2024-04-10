package com.lkksoftdev.accountservice.beneficiary;

import com.lkksoftdev.accountservice.common.ResponseDto;
import com.lkksoftdev.accountservice.feign.BeneficiaryClient;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService {
    private final BeneficiaryClient beneficiaryClient;

    public BeneficiaryService(BeneficiaryClient beneficiaryClient) {
        this.beneficiaryClient = beneficiaryClient;
    }

    public ResponseDto fetchBeneficiary(Long customerId, Long beneficiaryId, String authHeader) {
        return beneficiaryClient.getBeneficiaryByCustomer(customerId, beneficiaryId, authHeader).getBody();
    }
}
