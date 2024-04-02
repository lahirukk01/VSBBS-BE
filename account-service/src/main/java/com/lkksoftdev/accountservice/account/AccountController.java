package com.lkksoftdev.accountservice.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/hello")
    public ResponseEntity<?> getAccountsByCustomerId() {
//        return accountService.getAccountsByCustomerId(customerId);
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
