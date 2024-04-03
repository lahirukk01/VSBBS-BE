package com.lkksoftdev.accountservice.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop10ByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Transaction> findByAccountIdAndCreatedAtBetween(Long account_id, LocalDateTime start, LocalDateTime end);
}
