package com.lkksoftdev.loanservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "creditRatingClient", url = "${services.external.urls.base}")
public interface CreditRatingClient {
    @PostMapping("/credit-rating")
    ResponseEntity<CreditRatingResponseDto> getCreditRating(CustomerDto customerDto);
}
