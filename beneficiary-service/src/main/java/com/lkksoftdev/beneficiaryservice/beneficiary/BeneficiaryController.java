package com.lkksoftdev.beneficiaryservice.beneficiary;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/beneficiaries/health")
    public String health() {
        return "Beneficiary service is up and running";
    }

    @PostMapping("/{customerId}/beneficiaries")
    ResponseEntity<?> createBeneficiary(@Valid @RequestBody BeneficiaryBase beneficiaryBase, @PathVariable Long customerId) {
        Beneficiary beneficiary = beneficiaryService.createBeneficiary(beneficiaryBase, customerId);
        var responseData = new BeneficiaryCreateResponseDto(beneficiary.getId(), beneficiary.getStatus());
        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }
}
