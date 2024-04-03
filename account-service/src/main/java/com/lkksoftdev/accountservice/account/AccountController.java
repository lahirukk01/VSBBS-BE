package com.lkksoftdev.accountservice.account;

import com.lkksoftdev.accountservice.common.ResponseDto;
import com.lkksoftdev.accountservice.exception.CustomBadRequestException;
import com.lkksoftdev.accountservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.accountservice.transaction.Transaction;
import com.lkksoftdev.accountservice.transaction.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

//    @GetMapping("/accounts/health")
//    public String health() {
//        return "Account service is up and running";
//    }

    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<?> getAccountsByCustomerId(@PathVariable Long customerId) {
        var accounts = accountService.getAccountsByCustomerId(customerId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(accounts, Account.class), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/accounts/{accountId}")
    public ResponseEntity<?> getCustomerAccount(@PathVariable Long customerId, @PathVariable Long accountId) {
        var account = accountService.getCustomerAccountWithLastTenTransactions(customerId, accountId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(account, Account.class), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/accounts/{accountId}/search")
    public ResponseEntity<?> searchCustomerAccount(
           @PathVariable Long customerId,
           @PathVariable Long accountId,
           @RequestParam(value = "onDate", required = false) LocalDate onDate,
           @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
           @RequestParam(value = "toDate", required = false) LocalDate toDate){

        var account = accountService.getCustomerAccount(customerId, accountId);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account not found");
        }

        List<Transaction> transactions;

        if (onDate != null) {
            transactions = transactionService.getTransactionsByAccountIdBetweenDates(accountId, onDate, onDate);
        } else if (fromDate != null) {
            if (toDate == null) {
                toDate = LocalDate.now();
            }

            transactions = transactionService.getTransactionsByAccountIdBetweenDates(accountId, fromDate, toDate);
        } else {
            throw new CustomBadRequestException("Invalid search parameters");
        }
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(transactions, Transaction.class), HttpStatus.OK);
    }
}
