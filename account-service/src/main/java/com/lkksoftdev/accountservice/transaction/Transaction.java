package com.lkksoftdev.accountservice.transaction;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lkksoftdev.accountservice.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @Size(max = 20)
    private String transactionType;

    @NotNull
    @Min(value = 0)
    private double amount;

    @NotNull
    private String description;

    @NotNull
    @Size(max = 20)
    private String transactionMethod;

    @NotNull
    @Size(max = 20)
    private String endBankIfsc;

    @NotNull
    private Long endBankAccountId;

    @NotNull
    private LocalDateTime createdAt;
}
