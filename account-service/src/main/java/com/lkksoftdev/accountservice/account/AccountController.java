package com.lkksoftdev.accountservice.account;

import com.lkksoftdev.accountservice.beneficiary.Beneficiary;
import com.lkksoftdev.accountservice.beneficiary.BeneficiaryService;
import com.lkksoftdev.accountservice.beneficiary.BeneficiaryStatus;
import com.lkksoftdev.accountservice.common.ResponseDto;
import com.lkksoftdev.accountservice.exception.CustomBadRequestException;
import com.lkksoftdev.accountservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.accountservice.transaction.Transaction;
import com.lkksoftdev.accountservice.transaction.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BeneficiaryService beneficiaryService;

    public AccountController(
            AccountService accountService,
            TransactionService transactionService,
            BeneficiaryService beneficiaryService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<?> getAccountsByCustomerId(@PathVariable Long customerId) {
        var accounts = accountService.getAccountsByCustomerIdAsDto(customerId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(accounts, Account.class), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/accounts/{accountId}")
    public ResponseEntity<?> getCustomerAccount(@PathVariable Long customerId, @PathVariable Long accountId) {
        var account = accountService.getCustomerAccountWithLastTenTransactions(customerId, accountId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(account, Account.class), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/accounts/{accountId}/transactions")
    public ResponseEntity<?> addTransaction(
            @PathVariable @Min(1) Long customerId,
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        var account = accountService.getCustomerAccount(customerId, accountId);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account not found");
        }

        if (account.getBalance() < transactionRequestDto.getAmount()) {
            throw new CustomBadRequestException("Insufficient balance");
        }

        Beneficiary beneficiary = beneficiaryService.fetchBeneficiary(
            customerId,
            transactionRequestDto.getBeneficiaryId()).block();

        if (beneficiary == null || !beneficiary.getStatus().equals(BeneficiaryStatus.APPROVED.getStatus())) {
            throw new CustomBadRequestException("Beneficiary not found or not approved");
        }

        accountService.createTransaction(beneficiary, account, transactionRequestDto);

        Map<String, String> responseData = Map.of("message", "Transaction successful");
        return new ResponseEntity<>(new ResponseDto(responseData, null), HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/accounts/{accountId}/search")
    public ResponseEntity<?> searchCustomerAccount(
           @PathVariable Long customerId,
           @PathVariable Long accountId,
           @RequestParam(value = "onDate", required = false) LocalDate onDate,
           @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
           @RequestParam(value = "toDate", required = false) LocalDate toDate){

        var account = accountService.getCustomerAccountAsDto(customerId, accountId);

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
