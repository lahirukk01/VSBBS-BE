package com.lkksoftdev.beneficiaryservice.beneficiary;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryService {
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public Beneficiary createBeneficiary(BeneficiaryBase beneficiaryBase, Long customerId) {
        Beneficiary beneficiary = new Beneficiary(beneficiaryBase, customerId);
        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(Long customerId) {
        return beneficiaryRepository.findAllByCustomerId(customerId);
    }
}
