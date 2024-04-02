package com.lkksoftdev.accountservice.account;


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

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    private String transactionType;

    @NotNull
    @Min(value = 0)
    private double amount;

    @NotNull
    @Size(max = 20)
    private String endBankIfsc;

    @NotNull
    private Long endBankAccountId;

    @NotNull
    private LocalDateTime createdAt;
}
