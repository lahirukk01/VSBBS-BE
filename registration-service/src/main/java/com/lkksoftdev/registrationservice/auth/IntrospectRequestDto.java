package com.lkksoftdev.registrationservice.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntrospectRequestDto {
    @NotNull
    private String token;
}
