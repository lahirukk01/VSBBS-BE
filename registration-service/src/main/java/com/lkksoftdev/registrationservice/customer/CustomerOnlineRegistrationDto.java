package com.lkksoftdev.registrationservice.customer;

import com.lkksoftdev.registrationservice.user.UserBase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CustomerOnlineRegistrationDto extends UserBase {
    @NotNull
    @Size(min = 5, max = 20)
    private String password;

    public String getSafeSummaryString() {
        return "Summary {" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }

    public Object getSafeSummaryObject() {
        return new Object() {
            public String username = CustomerOnlineRegistrationDto.this.username;
            public String firstName = CustomerOnlineRegistrationDto.this.firstName;
            public String lastName = CustomerOnlineRegistrationDto.this.lastName;
            public String email = CustomerOnlineRegistrationDto.this.email;
            public String mobile = CustomerOnlineRegistrationDto.this.mobile;
        };
    }
}
