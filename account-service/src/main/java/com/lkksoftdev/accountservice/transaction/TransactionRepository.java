package com.lkksoftdev.accountservice.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    Page<Transaction> findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long account_id, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
