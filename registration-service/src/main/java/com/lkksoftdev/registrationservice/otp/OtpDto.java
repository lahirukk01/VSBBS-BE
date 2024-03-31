package com.lkksoftdev.registrationservice.otp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpDto {
    @NotNull
    @Size(min = 6, max = 6)
    private String otp;

    @NotNull
    @Size(min = 30, max = 60)
    private String ownerIdentifier;

    @Override
    public String toString() {
        return "OtpDto{" +
                "otp='" + otp + '\'' +
                ", ownerIdentifier='" + ownerIdentifier + '\'' +
                '}';
    }
}
