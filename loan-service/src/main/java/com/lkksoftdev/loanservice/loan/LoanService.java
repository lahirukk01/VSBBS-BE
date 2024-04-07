package com.lkksoftdev.loanservice.loan;

import com.lkksoftdev.loanservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.loanservice.payment.PaymentDto;
import com.lkksoftdev.loanservice.payment.PaymentMethod;
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

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
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

    public void createPayment(Loan loan, PaymentDto paymentDto) {

    }
}
