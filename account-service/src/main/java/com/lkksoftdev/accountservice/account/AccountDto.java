package com.lkksoftdev.accountservice.account;

import com.lkksoftdev.accountservice.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AccountDto {
    private Long id;
    private Long customerId;
    private String accountType;
    private double balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Transaction> transactions;

    public AccountDto(Long id, Long customerId, String accountType,
                      double balance,
                      LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactions = null;
    }

    public AccountDto(Long id, Long customerId, String accountType,
                      double balance,
                      LocalDateTime createdAt, LocalDateTime updatedAt,
                      List<Transaction> transactions
    ) {
        this.id = id;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactions = transactions;
    }
}
