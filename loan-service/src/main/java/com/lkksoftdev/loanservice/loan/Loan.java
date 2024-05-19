package com.lkksoftdev.loanservice.loan;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@NoArgsConstructor
@Getter
@Setter
public class Loan extends LoanBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Long customerId;

    @NotNull
    private String status;

    @Min(1)
    private Integer creditRating;

    @NotNull
    private String paymentStatus;

    private String remarks;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private Integer paidEmis = 0;

    public double calculateEmi() {
        if (numberOfEmis == null || numberOfEmis == 0) {
            throw new IllegalArgumentException("Number of EMIs cannot be zero");
        }

        if (amount == null || amount == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }

        // Including 1% processing fee
        return Math.round(101 * amount / numberOfEmis) / 100.0;
    }
}
