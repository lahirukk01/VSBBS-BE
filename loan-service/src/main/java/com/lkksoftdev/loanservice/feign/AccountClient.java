package com.lkksoftdev.loanservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "accountClient", url = "${services.account.urls.base}")
public interface AccountClient {
    @PostMapping("/{customerId}/accounts/{accountId}/transfer")
    ResponseEntity<AccountTransferResponseDto> transfer(AccountTransferDto accountTransferDto);
}
