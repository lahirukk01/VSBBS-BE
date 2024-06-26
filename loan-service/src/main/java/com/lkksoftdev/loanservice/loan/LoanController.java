package com.lkksoftdev.loanservice.loan;

import com.lkksoftdev.loanservice.common.ResponseDto;
import com.lkksoftdev.loanservice.exception.CustomBadRequestException;
import com.lkksoftdev.loanservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.loanservice.feign.ExternalServiceClient;
import com.lkksoftdev.loanservice.feign.CreditRatingResponseDto;
import com.lkksoftdev.loanservice.feign.CustomerDto;
import com.lkksoftdev.loanservice.payment.Payment;
import com.lkksoftdev.loanservice.payment.PaymentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LoanController {
    private final LoanService loanService;
    private final ExternalServiceClient externalServiceClient;
    private final Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

    public LoanController(LoanService loanService, ExternalServiceClient externalServiceClient) {
        this.loanService = loanService;
        this.externalServiceClient = externalServiceClient;
    }

    @PostMapping("/{customerId}/loans")
    ResponseEntity<?> createLoan(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody LoanBase loanBase, @PathVariable @Min(1) Long customerId) {
        LOGGER.info("""
                Creating loan for customer: {},
                Loan Details: {},
                Auth Header: {}
                """, customerId, loanBase.toString(), authHeader);
        CustomerDto customerDto = loanService.getCustomer(authHeader, customerId);

        if (customerDto == null) {
            throw new CustomResourceNotFoundException("Customer not found");
        }

        Loan loan = loanService.createLoan(loanBase, customerId);

        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loan, Loan.class), HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/loans")
    ResponseEntity<?> getLoans(@PathVariable @Min(1) Long customerId) {
        var loans = loanService.getLoans(customerId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loans, Loan.class), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/loans/{loanId}")
    ResponseEntity<?> getLoan(@PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanByIdAndCustomerId(loanId, customerId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loan, Loan.class), HttpStatus.OK);
    }

    @PutMapping("/{customerId}/loans/{loanId}")
    ResponseEntity<?> updateLoan(@Valid @RequestBody LoanBase loanBase, @PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanByIdAndCustomerId(loanId, customerId);

        if (!loan.getStatus().equals(LoanStatus.PENDING.name())) {
            throw  new CustomBadRequestException("Loan has already been processed");
        }

        loanService.updateLoan(loanBase, loan);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loan, Loan.class), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}/loans/{loanId}")
    ResponseEntity<?> deleteLoan(@PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanByIdAndCustomerId(loanId, customerId);

        if (!loan.getStatus().equals(LoanStatus.PENDING.name())) {
            throw  new CustomBadRequestException("Loan has already been processed");
        }

        loanService.deleteLoan(loan);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{customerId}/loans/{loanId}/payments")
    ResponseEntity<?> createPayment(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PaymentDto paymentDto,
            @PathVariable @Min(1) Long customerId,
            @PathVariable @Min(1) Long loanId) {
        LOGGER.info("""
                Creating payment for customer: {},
                Loan: {},
                Auth Header: {}
                """, customerId, loanId, authorizationHeader);
        Loan loan = loanService.findApprovedLoanByIdAndCustomerId(loanId, customerId);

        var payment = loanService.createPayment(authorizationHeader, loan, paymentDto);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(payment, Payment.class), HttpStatus.CREATED);
    }

    // Manager get all loans
    @GetMapping("/loans")
    ResponseEntity<?> getAllLoans(
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
          @RequestParam(required = false) LoanStatus status) {
        LOGGER.info("Getting all loans with page: {}, status: {}", page, status);
        List<Loan> loans = loanService.getAllLoans(page, status);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loans, Loan.class), HttpStatus.OK);
    }

    // Manager get loan
    @GetMapping("/loans/{loanId}")
    ResponseEntity<?> getLoan(@PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanById(loanId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loan, Loan.class), HttpStatus.OK);
    }

    // Manager get loan credit score
    @GetMapping("/loans/{loanId}/credit-score")
    ResponseEntity<?> getLoanCreditScore(@RequestHeader("Authorization") String authorizationHeader, @PathVariable @Min(1) Long loanId) {
        Loan loan = loanService.findLoanById(loanId);
        CustomerDto customerDto = loanService.getCustomer(authorizationHeader, loan.getCustomerId());

        if (customerDto == null) {
            throw new CustomResourceNotFoundException("Customer not found");
        }

        CreditRatingResponseDto creditRatingResponseDto = externalServiceClient.getCreditRating(customerDto).getBody();

        if (creditRatingResponseDto == null) {
            throw new RuntimeException("Credit rating service is not available");
        }

        return new ResponseEntity<>(new ResponseDto(creditRatingResponseDto, null), HttpStatus.OK);
    }

    // Manager set loan status
    @PutMapping("/loans/{loanId}/status")
    ResponseEntity<?> setLoanStatus(@PathVariable @Min(1) Long loanId, @RequestBody LoanStatusUpdateRequestDto statusDto) {
        if (!LoanStatus.isValidLoanDecisionStatus(statusDto.status())) {
            throw new CustomBadRequestException("Invalid loan status");
        }

        Loan loan = loanService.findLoanById(loanId);
        loanService.setLoanStatus(loan, statusDto);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(loan, Loan.class), HttpStatus.OK);
    }
}
