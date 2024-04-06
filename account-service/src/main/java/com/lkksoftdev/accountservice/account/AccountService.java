package com.lkksoftdev.accountservice.account;

import com.lkksoftdev.accountservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.accountservice.transaction.Transaction;
import com.lkksoftdev.accountservice.transaction.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    public List<AccountDto> getAccountsByCustomerIdAsDto(Long customerId) {
        return convertAccountsToDto(accountRepository.findByCustomerId(customerId));
    }

    public AccountDto getCustomerAccountWithLastTenTransactions(Long customerId, Long accountId) {
        AccountDto account = getCustomerAccountAsDto(customerId, accountId);

        if (account == null) {
            throw new CustomResourceNotFoundException("Account not found");
        }

        List<Transaction> transactions = transactionService.getTenLatestTransactionsByAccountId(accountId);

        account.setTransactions(transactions);

        return account;
    }

    public AccountDto getCustomerAccountAsDto(Long customerId, Long accountId) {
        return convertAccountToDto(accountRepository.findByCustomerIdAndId(customerId, accountId));
    }

    public Account getCustomerAccount(Long customerId, Long accountId) {
        return accountRepository.findByCustomerIdAndId(customerId, accountId);
    }

    @Transactional
    public void createTransaction(Long beneficiaryAccountId, String beneficiaryIfscCode, Account account, TransactionRequestDto transactionRequestDto) {
        transactionService.createTransaction(beneficiaryAccountId, beneficiaryIfscCode, account, transactionRequestDto);

        account.setBalance(account.getBalance() - transactionRequestDto.getAmount());
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private AccountDto convertAccountToDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getCustomerId(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    private List<AccountDto> convertAccountsToDto(List<Account> accounts) {
        return accounts.stream().map(this::convertAccountToDto).collect(Collectors.toList());
    }
}
