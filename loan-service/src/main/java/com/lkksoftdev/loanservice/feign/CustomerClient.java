package com.lkksoftdev.loanservice.feign;

import jakarta.validation.constraints.Min;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "customerClient", url = "${services.customer.urls.base}")
public interface CustomerClient {
    @GetMapping("/customers/{customerId}")
    ResponseEntity<CustomerDto> getCustomer(
        @RequestHeader("Authorization") String bearerToken,
        @PathVariable @Min(1) Long customerId);
}
