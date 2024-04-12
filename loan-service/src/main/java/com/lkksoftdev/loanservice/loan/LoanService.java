package com.lkksoftdev.loanservice.loan;

import com.lkksoftdev.loanservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.loanservice.feign.*;
import com.lkksoftdev.loanservice.payment.Payment;
import com.lkksoftdev.loanservice.payment.PaymentDto;
import com.lkksoftdev.loanservice.payment.PaymentService;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final ExternalServiceClient externalServiceClient;
    private final PaymentService paymentService;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;

    public LoanService(
            LoanRepository loanRepository,
            ExternalServiceClient externalServiceClient,
            PaymentService paymentService, AccountClient accountClient,
            CustomerClient customerClient) {
        this.loanRepository = loanRepository;
        this.externalServiceClient = externalServiceClient;
        this.paymentService = paymentService;
        this.accountClient = accountClient;
        this.customerClient = customerClient;
    }

    public Loan createLoan(LoanBase loanBase, Long customerId) {
        Loan loan = new Loan();
        loan.setCustomerId(customerId);
        loan.setAmount(loanBase.getAmount());
        loan.setPurpose(loanBase.getPurpose());
        loan.setNumberOfEmis(loanBase.getNumberOfEmis());
        loan.setStatus(LoanStatus.IN_PROGRESS.getValue());
        loan.setPaymentStatus(LoanPaymentStatus.NA.getValue());
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());
        return loanRepository.save(loan);
    }

    public List<Loan> getLoans(Long customerId) {
        return loanRepository.findByCustomerIdOrderByUpdatedAtDesc(customerId);
    }

    public Loan findLoanByIdAndCustomerId(Long loanId, Long customerId) {
        return loanRepository.findByIdAndCustomerId(loanId, customerId)
                .orElseThrow(() -> new CustomResourceNotFoundException("Loan with given details not found"));
    }

    public void updateLoan(LoanBase loanBase, Loan loan) {
        loan.setAmount(loanBase.getAmount());
        loan.setPurpose(loanBase.getPurpose());
        loan.setNumberOfEmis(loanBase.getNumberOfEmis());
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);
    }

    public void deleteLoan(Loan loan) {
        loanRepository.delete(loan);
    }

    public List<Loan> getAllLoans(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Loan> loans = loanRepository.findAllByOrderByCreatedAtDesc(pageable);
        return loans.getContent();
    }

    public Loan findLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new CustomResourceNotFoundException("Loan with given details not found"));
    }

    public void setLoanStatus(Loan loan, String status) {
        loan.setStatus(status);
        var paymentStatus = status.equals(LoanStatus.APPROVED.getValue()) ? LoanPaymentStatus.PENDING.getValue() : LoanPaymentStatus.NA.getValue();
        loan.setPaymentStatus(paymentStatus);
        loanRepository.save(loan);
    }

    public Loan findApprovedLoanByIdAndCustomerId(Long loanId, Long customerId) {
        return loanRepository.findByIdAndCustomerIdAndStatus(loanId, customerId, LoanStatus.APPROVED.getValue())
            .orElseThrow(() -> new CustomResourceNotFoundException("Approved loan with given details not found"));
    }

    private void processPaymentByCard(double emiAmount, PaymentDto paymentDto) {
        CardPaymentDetailsDto cardPaymentDetailsDto = new CardPaymentDetailsDto(paymentDto.cardNumber(),
                paymentDto.cardHolderName(), paymentDto.cardExpiry(), paymentDto.cardCvv(), emiAmount);

        externalServiceClient.makeCardPayment(cardPaymentDetailsDto).getBody();
    }

    private void processPaymentBySavingsAccount(String authorizationHeader, double emiAmount, Long customerId, Long savingsAccountId) {
        var accountTransferDto = new AccountTransferDto(emiAmount);
        accountClient.transferFromSavingAccount(authorizationHeader, customerId, savingsAccountId, accountTransferDto);
    }

    public Payment createPayment(String authorizationHeader, Loan loan, PaymentDto paymentDto) {
        double emiAmount = loan.calculateEmi();

        switch (paymentDto.paymentMethod()) {
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                processPaymentByCard(emiAmount, paymentDto);
            case "SAVINGS_ACCOUNT":
                processPaymentBySavingsAccount(authorizationHeader, emiAmount, loan.getCustomerId(), paymentDto.savingsAccountId());
            case "UPI":
                break;
            default:
                throw new CustomResourceNotFoundException("Invalid payment method");
        }
        return paymentService.createPayment(loan.getId(), emiAmount, paymentDto.paymentMethod());
    }

    public CustomerDto getCustomer(String authorizationHeader, Long customerId) {
        String customerResponseString = customerClient.getCustomer(authorizationHeader, customerId).getBody();
        var customer = getCustomerObject(customerResponseString);

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        return new CustomerDto((String) customer.get("firstName"), (String) customer.get("lastName"), (String) customer.get("mobile"));
    }

    private static JSONObject getCustomerObject(String customerResponseString) {
        if (customerResponseString == null) {
            throw new CustomResourceNotFoundException("Customer not found");
        }

        JSONObject customerResponseJson = new JSONObject(customerResponseString);
        var data = customerResponseJson.get("data");

        if (customerResponseJson.get("data") == null) {
            throw new CustomResourceNotFoundException("Customer not found");
        }

        if (data == null) {
            throw new RuntimeException("Customer not found");
        }
        return (JSONObject)((JSONObject)data).get("customer");
    }
}
