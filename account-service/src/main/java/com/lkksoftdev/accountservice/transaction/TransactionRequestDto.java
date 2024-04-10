package com.lkksoftdev.accountservice.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRequestDto {
    @NotNull
    @Min(value = 0)
    private double amount;

    @NotNull
    @Min(1)
    private Long beneficiaryId;

    @NotNull
    private String description;

    @NotNull
    private String transactionMethod;
}
