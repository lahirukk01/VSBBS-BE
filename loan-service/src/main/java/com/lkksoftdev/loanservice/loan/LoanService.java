package com.lkksoftdev.loanservice.loan;

import com.lkksoftdev.loanservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.loanservice.feign.CardPaymentDetailsDto;
import com.lkksoftdev.loanservice.feign.ExternalServiceClient;
import com.lkksoftdev.loanservice.payment.Payment;
import com.lkksoftdev.loanservice.payment.PaymentDto;
import com.lkksoftdev.loanservice.payment.PaymentMethod;
import com.lkksoftdev.loanservice.payment.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class LoanService {
    private static final List<String> ALLOWED_PAYMENT_METHODS = Arrays.stream(PaymentMethod.values())
            .map(PaymentMethod::getValue)
            .toList();

    private final LoanRepository loanRepository;
    private final ExternalServiceClient externalServiceClient;
    private final PaymentService paymentService;

    public LoanService(
            LoanRepository loanRepository,
            ExternalServiceClient externalServiceClient,
            PaymentService paymentService) {
        this.loanRepository = loanRepository;
        this.externalServiceClient = externalServiceClient;
        this.paymentService = paymentService;
    }

    public Long createLoan(LoanBase loanBase, Long customerId) {
        Loan loan = new Loan();
        loan.setCustomerId(customerId);
        loan.setAmount(loanBase.getAmount());
        loan.setPurpose(loanBase.getPurpose());
        loan.setNumberOfEmis(loanBase.getNumberOfEmis());
        loan.setStatus(LoanStatus.IN_PROGRESS.getValue());
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);
        return loan.getId();
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

    public boolean isValidLoanDecisionStatus(String status) {
        if (status == null) {
            return false;
        }

        return status.equals(LoanStatus.APPROVED.getValue()) || status.equals(LoanStatus.REJECTED.getValue());
    }

    public void setLoanStatus(Loan loan, String status) {
        loan.setStatus(status);
        loan.setPaymentStatus(LoanPaymentStatus.PENDING.getValue());
        loanRepository.save(loan);
    }

    public Loan findApprovedLoanByIdAndCustomerId(Long loanId, Long customerId) {
        return loanRepository.findByIdAndCustomerIdAndStatus(loanId, customerId, LoanStatus.APPROVED.getValue())
                .orElseThrow(() -> new CustomResourceNotFoundException("Approved loan with given details not found"));
    }

    public boolean isValidPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }

        return ALLOWED_PAYMENT_METHODS.contains(paymentMethod);
    }

    private Payment processPaymentByCard(PaymentDto paymentDto, Loan loan) {
        double emiAmount = loan.calculateEmi();
        CardPaymentDetailsDto cardPaymentDetailsDto = new CardPaymentDetailsDto(paymentDto.cardNumber(),
                paymentDto.cardHolderName(), paymentDto.cardExpiry(), paymentDto.cardCvv(), emiAmount);

        externalServiceClient.makeCardPayment(cardPaymentDetailsDto).getBody();
        return paymentService.createPayment(loan.getId(), emiAmount, paymentDto.paymentMethod());
    }

    private boolean isValidCardDetails(PaymentDto paymentDto) {
        return paymentDto.cardNumber() != null && paymentDto.cardHolderName() != null
                && paymentDto.cardExpiry() != null && paymentDto.cardCvv() != null;
    }

    private boolean isValidSavingAccountDetails(PaymentDto paymentDto) {
        return paymentDto.savingsAccountNumber() != null && paymentDto.customerId() != null;
    }

    private boolean isValidUpiDetails(PaymentDto paymentDto) {
        return paymentDto.upiId() != null;
    }

    public Payment createPayment(Loan loan, PaymentDto paymentDto) {
        switch (paymentDto.paymentMethod()) {
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                if (!isValidCardDetails(paymentDto)) {
                    throw new CustomResourceNotFoundException("Invalid card details");
                }

                return processPaymentByCard(paymentDto, loan);
            case "SAVINGS_ACCOUNT":
                if (!isValidSavingAccountDetails(paymentDto)) {
                    throw new CustomResourceNotFoundException("Invalid savings account details");
                }
                break;
            case "UPI":
                if (!isValidUpiDetails(paymentDto)) {
                    throw new CustomResourceNotFoundException("Invalid payment method details");
                }
                break;
            default:
                throw new CustomResourceNotFoundException("Invalid payment method");
        }
        return null;
    }
}
