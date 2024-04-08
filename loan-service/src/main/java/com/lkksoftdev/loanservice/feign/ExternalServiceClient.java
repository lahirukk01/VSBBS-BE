package com.lkksoftdev.loanservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "externalServiceClient", url = "${services.external.urls.base}")
public interface ExternalServiceClient {
    @PostMapping("/credit-ratings")
    ResponseEntity<CreditRatingResponseDto> getCreditRating(CustomerDto customerDto);

    @PostMapping("/payments/card")
    ResponseEntity<CardPaymentResponseDto> makeCardPayment(CardPaymentDetailsDto cardPaymentDetailsDto);

    @PostMapping("/payments/upi")
    ResponseEntity<UpiPaymentResponseDto> makeUpiPayment(UpiPaymentDetailsDto upiPaymentDetailsDto);
}
