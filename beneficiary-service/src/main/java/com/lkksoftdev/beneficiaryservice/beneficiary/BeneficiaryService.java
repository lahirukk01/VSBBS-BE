package com.lkksoftdev.beneficiaryservice.beneficiary;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Beneficiary> getCustomerBeneficiaries(Long customerId) {
        return beneficiaryRepository.findAllByCustomerId(customerId);
    }

    public List<Beneficiary> getBeneficiaries(Integer page, Integer size, String status) {
        Sort sort = Sort.by("updatedAt").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (status != null) {
            return beneficiaryRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable).getContent();
        }

        return beneficiaryRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }

    public Beneficiary updateBeneficiaryByManager(BeneficiaryApproveRequestDto beneficiaryApproveRequestDto, Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId).orElseThrow();
        beneficiary.setStatus(beneficiaryApproveRequestDto.status().getStatus());
        beneficiary.setComments(beneficiaryApproveRequestDto.comments());
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return beneficiaryRepository.save(beneficiary);
    }

    public Beneficiary updateBeneficiaryByCustomer(BeneficiaryBase beneficiaryBase, Long customerId, Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndCustomerId(beneficiaryId, customerId).orElseThrow();
        beneficiary.setName(beneficiaryBase.getName());
        beneficiary.setAccountId(beneficiaryBase.getAccountId());
        beneficiary.setAccountIfscCode(beneficiaryBase.getAccountIfscCode());
        beneficiary.setEmail(beneficiaryBase.getEmail());
        beneficiary.setStatus(BeneficiaryStatus.PENDING.getStatus());
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(Long customerId, Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndCustomerId(beneficiaryId, customerId).orElseThrow();
        beneficiaryRepository.delete(beneficiary);
    }

    public Beneficiary getBeneficiaryByCustomer(Long customerId, Long beneficiaryId) {
        return beneficiaryRepository.findByIdAndCustomerId(beneficiaryId, customerId).orElseThrow();
    }
}
