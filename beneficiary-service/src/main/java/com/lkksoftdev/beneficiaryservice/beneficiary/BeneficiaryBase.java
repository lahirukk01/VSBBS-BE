package com.lkksoftdev.beneficiaryservice.beneficiary;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BeneficiaryBase {
    @NotNull
    @Size(max = 50)
    protected String name;

    @NotNull
    @Min(1)
    protected Long accountId;

    @NotNull
    @Size(max = 20)
    protected String accountIfscCode;

    @NotNull
    @Email
    @Size(max = 50)
    protected String email;
}
