package com.lkksoftdev.registrationservice.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProfileUpdateDto extends CustomerOnlineRegistrationDto{
    @NotNull
    private Integer id;
}
