package com.lkksoftdev.accountservice.transaction;

import com.lkksoftdev.accountservice.account.Account;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void createTransaction(Long beneficiaryAccountId, String beneficiaryIfscCode, Account account, TransactionRequestDto transactionRequestDto) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.DEBIT.getValue());
        transaction.setTransactionMethod(transactionRequestDto.getTransactionMethod());
        transaction.setAmount(transactionRequestDto.getAmount());
        transaction.setEndBankAccountId(beneficiaryAccountId);
        transaction.setEndBankIfsc(beneficiaryIfscCode);
        transaction.setDescription(transactionRequestDto.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}
