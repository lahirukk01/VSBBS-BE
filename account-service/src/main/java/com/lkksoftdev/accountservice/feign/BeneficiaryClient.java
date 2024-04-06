package com.lkksoftdev.accountservice.feign;

import com.lkksoftdev.accountservice.beneficiary.BeneficiaryResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "beneficiaryClient", url = "${services.beneficiary.urls.base}")
public interface BeneficiaryClient {
    @GetMapping("/{customerId}/beneficiaries/{beneficiaryId}")
    ResponseEntity<BeneficiaryResponseDto> getBeneficiaryByCustomer(@PathVariable Long customerId, @PathVariable Long beneficiaryId);
}
