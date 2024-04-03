package com.lkksoftdev.accountservice.account;

import com.lkksoftdev.accountservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.accountservice.transaction.Transaction;
import com.lkksoftdev.accountservice.transaction.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    public List<AccountDto> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findAccountsByCustomerId(customerId);
    }

    public AccountDto getCustomerAccountWithLastTenTransactions(Long customerId, Long accountId) {
        AccountDto account = getCustomerAccount(customerId, accountId);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account not found");
        }

        List<Transaction> transactions = transactionService.getTenLatestTransactionsByAccountId(accountId);

        account.setTransactions(transactions);

        return account;
    }

    public AccountDto getCustomerAccount(Long customerId, Long accountId) {
        return accountRepository.findByCustomerIdAndId(customerId, accountId);
    }
}
