package com.lkksoftdev.externalservice;

import com.lkksoftdev.externalservice.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ExternalServiceController {
    private final Logger LOGGER = LoggerFactory.getLogger(ExternalServiceController.class);

    @PostMapping("/payments/card")
    public Mono<ResponseEntity<CardPaymentResponseDto>> makeCardPayment(@RequestBody CardPaymentDetailsDto cardPaymentDetailsDto) {
        LOGGER.info("Processing card payment for customer: {}", cardPaymentDetailsDto.toString());
        return Mono.just(ResponseEntity.ok(new CardPaymentResponseDto("OK")));
    }

    @PostMapping("/payments/upi")
    public Mono<ResponseEntity<CardPaymentResponseDto>> makeUpiPayment(@RequestBody UpiPaymentDetailsDto upiPaymentDetailsDto) {
        LOGGER.info("Processing UPI payment for customer: {}", upiPaymentDetailsDto.toString());
        return Mono.just(ResponseEntity.ok(new CardPaymentResponseDto("OK")));
    }

    @PostMapping("/credit-ratings")
    public Mono<ResponseEntity<CreditRatingResponseDto>> getCreditRating(@RequestBody CustomerDto customerDto) {
        LOGGER.info("Getting credit rating for customer: {}", customerDto.toString());
        return Mono.just(ResponseEntity.ok(new CreditRatingResponseDto(250)));
    }
}
