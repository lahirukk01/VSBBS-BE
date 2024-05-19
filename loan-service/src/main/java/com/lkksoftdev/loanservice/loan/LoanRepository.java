package com.lkksoftdev.loanservice.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerIdOrderByUpdatedAtDesc(Long customerId);

    Optional<Loan> findByIdAndCustomerId(Long loanId, Long customerId);

    Page<Loan> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Loan> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Optional<Loan> findByIdAndCustomerIdAndStatusAndPaymentStatus(Long loanId, Long customerId, String status, String paymentStatus);
}