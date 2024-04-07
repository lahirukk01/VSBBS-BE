package com.lkksoftdev.loanservice.loan;

import com.lkksoftdev.loanservice.common.ResponseDto;
import com.lkksoftdev.loanservice.exception.CustomBadRequestException;
import com.lkksoftdev.loanservice.feign.CreditRatingClient;
import com.lkksoftdev.loanservice.feign.CreditRatingResponseDto;
import com.lkksoftdev.loanservice.feign.CustomerClient;
import com.lkksoftdev.loanservice.feign.CustomerDto;
import com.lkksoftdev.loanservice.payment.PaymentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LoanController {
    private final LoanService loanService;
    private final CreditRatingClient creditRatingClient;
    private final CustomerClient customerClient;

    public LoanController(LoanService loanService, CreditRatingClient creditRatingClient, CustomerClient customerClient) {
        this.loanService = loanService;
        this.creditRatingClient = creditRatingClient;
        this.customerClient = customerClient;
    }

    @PostMapping("/{customerId}/loans")
    ResponseEntity<?> createLoan(@Valid @RequestBody LoanBase loanBase, @PathVariable @Min(1) Long customerId) {
        Long loanId = loanService.createLoan(loanBase, customerId);
        return new ResponseEntity<>(new LoanMutateResponseDto(loanId), HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/loans")
    ResponseEntity<?> getLoans(@PathVariable @Min(1) Long customerId) {
        var loans = loanService.getLoans(customerId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loans, Loan.class), HttpStatus.OK);
    }

    @PutMapping("/{customerId}/loans/{loanId}")
    ResponseEntity<?> updateLoan(@Valid @RequestBody LoanBase loanBase, @PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanByIdAndCustomerId(loanId, customerId);

        if (!loan.getStatus().equals(LoanStatus.IN_PROGRESS.getValue())) {
            throw  new CustomBadRequestException("Loan has already been processed");
        }

        loanService.updateLoan(loanBase, loan);
        return new ResponseEntity<>(new LoanMutateResponseDto(loan.getId()), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}/loans/{loanId}")
    ResponseEntity<?> deleteLoan(@PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanByIdAndCustomerId(loanId, customerId);

        if (!loan.getStatus().equals(LoanStatus.IN_PROGRESS.getValue())) {
            throw  new CustomBadRequestException("Loan has already been processed");
        }

        loanService.deleteLoan(loan);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{customerId}/loans/{loanId}/payments")
    ResponseEntity<?> createPayment(@RequestBody PaymentDto paymentDto, @PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findApprovedLoanByIdAndCustomerId(loanId, customerId);

        if (!loanService.isValidPaymentMethod(paymentDto.paymentMethod())) {
            throw new CustomBadRequestException("Invalid payment method");
        }

        loanService.createPayment(loan, paymentDto);
        return new ResponseEntity<>(new LoanMutateResponseDto(loan.getId()), HttpStatus.CREATED);
    }

    // Manager get all loans
    @GetMapping("/loans")
    ResponseEntity<?> getAllLoans(@RequestParam(required = false) @Min(1) int page, @RequestParam(required = false) @Min(1) int size) {
        var loans = loanService.getAllLoans(page, size);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loans, Loan.class), HttpStatus.OK);
    }

    // Manager get loan credit score
    @GetMapping("/loans/{loanId}/credit-score")
    ResponseEntity<?> getLoanCreditScore(@PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanById(loanId);
        CustomerDto customerDto = customerClient.getCustomer(loan.getCustomerId()).getBody();
        CreditRatingResponseDto creditRatingResponseDto = creditRatingClient.getCreditRating(customerDto).getBody();

        if (creditRatingResponseDto == null) {
            throw new RuntimeException("Credit rating service is not available");
        }

        Map<String, ?> creditScore = Map.of("creditScore", creditRatingResponseDto.creditRating());
        return new ResponseEntity<>(new ResponseDto(creditScore, null), HttpStatus.OK);
    }

    // Manager set loan status
    @PutMapping("/loans/{loanId}/status")
    ResponseEntity<?> setLoanStatus(@PathVariable @Min(1) Long loanId, @RequestBody LoanStatusUpdateRequestDto statusDto) {
        if (!loanService.isValidLoanDecisionStatus(statusDto.status())) {
            throw new CustomBadRequestException("Invalid loan status");
        }

        Loan loan = loanService.findLoanById(loanId);
        loanService.setLoanStatus(loan, statusDto.status());
        return new ResponseEntity<>(new LoanMutateResponseDto(loan.getId()), HttpStatus.OK);
    }
}
