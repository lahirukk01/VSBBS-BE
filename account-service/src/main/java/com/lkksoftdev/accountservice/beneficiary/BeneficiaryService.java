package com.lkksoftdev.accountservice.beneficiary;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BeneficiaryService {
    private final WebClient beneficiaryWebClient;

    public BeneficiaryService(WebClient beneficiaryWebClient) {
        this.beneficiaryWebClient = beneficiaryWebClient;
    }

    public Mono<Beneficiary> fetchBeneficiary(Long customerId, Long beneficiaryId) {
        String uri = String.format("/%d/beneficiaries/%d", customerId, beneficiaryId);

        return beneficiaryWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Beneficiary.class);
    }
}
