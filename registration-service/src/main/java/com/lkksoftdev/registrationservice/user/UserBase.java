package com.lkksoftdev.registrationservice.user;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class UserBase {
    @NotNull
    @Size(min = 5, max = 20)
    protected String username;

    @NotNull
    @Size(min = 1, max = 20)
    protected String firstName;
    protected String lastName;

    @NotNull
    @Email
    protected String email;

    @NotNull
    @Pattern(regexp="(^$|(\\+)?[0-9]{1,14})")
    protected String mobile;
}
