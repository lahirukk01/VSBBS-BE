package com.lkksoftdev.loanservice.loan;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class LoanBase {

    @NotNull
    @Min(0)
    private double amount;

    @NotNull
    private String purpose;

    @NotNull
    @Min(1)
    private int numberOfEmis;

}
