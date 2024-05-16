package com.lkksoftdev.accountservice.transaction;

import com.lkksoftdev.accountservice.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getTransactionsByAccountIdBetweenDates(Long accountId, LocalDate fromDate, LocalDate toDate, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return transactionRepository.findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                accountId,
                fromDate.atStartOfDay(),
                toDate.atTime(23, 59, 59), pageable);
    }

    public Page<Transaction> getTransactionsByAccountIdOrderByCreatedAtDesc(Long accountId, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
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

    public void createLoanPaymentTransaction(
            Account account,
            double emiAmount,
            Long loanId,
            String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.DEBIT.getValue());
        transaction.setTransactionMethod(TransactionMethod.INTERNAL.getValue());
        transaction.setAmount(emiAmount);
        transaction.setEndBankAccountId(loanId);
        transaction.setEndBankIfsc("SELF");
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}
