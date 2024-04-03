package com.lkksoftdev.accountservice.transaction;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByAccountIdBetweenDates(Long accountId, LocalDate fromDate, LocalDate toDate) {
        return transactionRepository.findByAccountIdAndCreatedAtBetween(
                accountId,
                fromDate.atStartOfDay(),
                toDate.atTime(23, 59, 59));
    }

    public List<Transaction> getTenLatestTransactionsByAccountId(Long accountId) {
        return transactionRepository.findTop10ByAccountIdOrderByCreatedAtDesc(accountId);
    }
}