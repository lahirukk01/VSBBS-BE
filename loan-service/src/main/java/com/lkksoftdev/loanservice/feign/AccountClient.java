package com.lkksoftdev.loanservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "accountClient", url = "${services.account.urls.base}")
public interface AccountClient {
    @PostMapping("/{customerId}/accounts/{accountId}/loan-payment")
    ResponseEntity<AccountTransferResponseDto> transferFromSavingAccount(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long customerId,
            @PathVariable Long accountId,
            AccountTransferDto accountTransferDto);
}
