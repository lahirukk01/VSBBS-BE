package com.lkksoftdev.beneficiaryservice.beneficiary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeneficiaryController {
    @GetMapping("/beneficiaries/health")
    public String health() {
        return "Beneficiary service is up and running";
    }
}
