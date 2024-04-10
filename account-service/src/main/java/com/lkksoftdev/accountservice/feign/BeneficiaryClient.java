package com.lkksoftdev.accountservice.feign;

import com.lkksoftdev.accountservice.common.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "beneficiaryClient", url = "${services.beneficiary.urls.base}")
public interface BeneficiaryClient {
    @GetMapping("/{customerId}/beneficiaries/{beneficiaryId}")
    ResponseEntity<ResponseDto> getBeneficiaryByCustomer(
            @PathVariable Long customerId,
            @PathVariable Long beneficiaryId,
            @RequestHeader("Authorization") String authHeader);
}
