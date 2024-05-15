package com.lkksoftdev.registrationservice.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProfileActivationDto {
    @NotNull
    private Integer id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(regexp="(^$|(\\+)?[0-9]{1,14})")
    private String mobile;
}
