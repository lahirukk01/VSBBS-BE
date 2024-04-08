package com.lkksoftdev.loanservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "externalServiceClient", url = "${services.external.urls.base}")
public interface ExternalServiceClient {
    @PostMapping("/credit-rating")
    ResponseEntity<CreditRatingResponseDto> getCreditRating(CustomerDto customerDto);

    @PostMapping("/payment/card")
    ResponseEntity<CardPaymentResponseDto> makeCardPayment(CardPaymentDetailsDto cardPaymentDetailsDto);

    @PostMapping("/payment/upi")
    ResponseEntity<UpiPaymentResponseDto> makeUpiPayment(UpiPaymentDetailsDto upiPaymentDetailsDto);
}
