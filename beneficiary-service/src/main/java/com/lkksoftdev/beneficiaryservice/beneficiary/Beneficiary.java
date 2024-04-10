package com.lkksoftdev.beneficiaryservice.beneficiary;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
@Getter
@Setter
@NoArgsConstructor
public class Beneficiary extends BeneficiaryBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Long customerId;

    @NotNull
    @Size(max = 20)
    private String status;

    private String comments;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    public Beneficiary(BeneficiaryBase beneficiaryBase, Long customerId) {
        this.name = beneficiaryBase.getName();
        this.accountId = beneficiaryBase.getAccountId();
        this.accountIfscCode = beneficiaryBase.getAccountIfscCode();
        this.email = beneficiaryBase.getEmail();
        this.customerId = customerId;
        this.status = BeneficiaryStatus.PENDING.getStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
