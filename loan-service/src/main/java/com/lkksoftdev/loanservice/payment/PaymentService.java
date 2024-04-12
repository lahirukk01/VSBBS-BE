package com.lkksoftdev.loanservice.payment;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Long id, double emiAmount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setLoanId(id);
        payment.setAmount(emiAmount);
        payment.setMethod(paymentMethod);
        payment.setPaymentTimestamp(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public int getTotalNumberOfPayments(Long id) {
        return paymentRepository.countAllByLoanId(id);
    }
}
