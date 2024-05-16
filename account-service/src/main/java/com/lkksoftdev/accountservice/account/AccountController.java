package com.lkksoftdev.accountservice.account;

import com.lkksoftdev.accountservice.beneficiary.BeneficiaryService;
import com.lkksoftdev.accountservice.beneficiary.BeneficiaryStatus;
import com.lkksoftdev.accountservice.common.ResponseDto;
import com.lkksoftdev.accountservice.exception.CustomBadRequestException;
import com.lkksoftdev.accountservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.accountservice.transaction.Transaction;
import com.lkksoftdev.accountservice.transaction.TransactionMethod;
import com.lkksoftdev.accountservice.transaction.TransactionRequestDto;
import com.lkksoftdev.accountservice.transaction.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BeneficiaryService beneficiaryService;

    private final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

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

    @PostMapping("/{customerId}/accounts/{accountId}/loan-payment")
    public ResponseEntity<?> makeLoanPayment(
            @PathVariable @Min(1) Long customerId,
            @PathVariable @Min(1) Long accountId,
            @RequestBody LoanPaymentRequestDto loanPaymentRequestDto) {
        double emiAmount = loanPaymentRequestDto.emiAmount();

        if (emiAmount <= 0) {
            throw new CustomBadRequestException("Invalid amount");
        }

        var account = accountService.getCustomerAccount(customerId, accountId);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account for the customer not found");
        }

        if (account.getBalance() < emiAmount) {
            throw new CustomBadRequestException("Insufficient balance");
        }

        accountService.makeLoanPayment(account, loanPaymentRequestDto);

        return new ResponseEntity<>(new ResponseDto(Map.of("message", "Loan payment successful"), null), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/accounts/{accountId}/transactions")
    public ResponseEntity<?> addTransaction(
            @PathVariable @Min(1) Long customerId,
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto, @RequestHeader("Authorization") String authHeader) {
        if (!TransactionMethod.isValid(transactionRequestDto.getTransactionMethod())) {
            throw new CustomBadRequestException("Invalid transaction method");
        }

        var account = accountService.getCustomerAccount(customerId, accountId);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account not found");
        }

        if (account.getBalance() < transactionRequestDto.getAmount()) {
            throw new CustomBadRequestException("Insufficient balance");
        }

        ResponseDto beneficiaryResponse = beneficiaryService.fetchBeneficiary(
            customerId,
            transactionRequestDto.getBeneficiaryId(),
            authHeader);

        var data = beneficiaryResponse.getData();

        if (data == null) {
            LOGGER.error("Beneficiary not found for given data: customerId: {}, beneficiaryId: {}, accountId: {}",
                    customerId, transactionRequestDto.getBeneficiaryId(), accountId);
            throw new CustomBadRequestException("Beneficiary not found for given data");
        }

        Map<String, Object> beneficiaryData = (Map<String, Object>) ((Map<String, Object>) data).get("beneficiary");

        if (!beneficiaryData.get("status").equals(BeneficiaryStatus.APPROVED.getStatus())) {
            throw new CustomBadRequestException("Beneficiary is not approved");
        }

        Long beneficiaryAccountId = ((Integer) beneficiaryData.get("accountId")).longValue();
        String beneficiaryAccountIfscCode = (String) beneficiaryData.get("accountIfscCode");

        accountService.createTransaction(beneficiaryAccountId,
                beneficiaryAccountIfscCode, account, transactionRequestDto);

        Map<String, String> responseData = Map.of("message", "Transaction completed successfully");
        return new ResponseEntity<>(new ResponseDto(responseData, null), HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/accounts/{accountId}/transactions")
    public ResponseEntity<?> searchCustomerAccount(
           @PathVariable Long customerId,
           @PathVariable Long accountId,
           @RequestParam(value = "onDate", required = false) LocalDate onDate,
           @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
           @RequestParam(value = "toDate", required = false) LocalDate toDate,
           @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {

        var account = accountService.getCustomerAccountAsDto(customerId, accountId);
        LOGGER.info("Account: {}, Customer: {}, onDate: {}, fromDate: {}, toDate: {}, page: {}",
                accountId, customerId, onDate, fromDate, toDate, page);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account not found");
        }

        Page<Transaction> transactions;

        if (onDate != null) {
            if (fromDate != null || toDate != null) {
                throw new CustomBadRequestException("Invalid search parameters. onDate is already provided.");
            }

            transactions = transactionService.getTransactionsByAccountIdBetweenDates(accountId, onDate, onDate, page);
        } else if (fromDate != null) {
            if (toDate == null) {
                toDate = LocalDate.now();
            } else if (toDate.isBefore(fromDate)) {
                throw new CustomBadRequestException("toDate should be after fromDate");
            }

            transactions = transactionService.getTransactionsByAccountIdBetweenDates(accountId, fromDate, toDate, page);
        } else {
            transactions = transactionService.getTransactionsByAccountIdOrderByCreatedAtDesc(accountId, page);
        }

        Map<String, Object> responseData = Map.of(
                "totalPages", transactions.getTotalPages(),
                "currentPage", transactions.getNumber(),
                "transactions", transactions.getContent());

        return new ResponseEntity<>(new ResponseDto(responseData, null), HttpStatus.OK);
    }
}
