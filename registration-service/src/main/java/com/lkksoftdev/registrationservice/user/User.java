package com.lkksoftdev.registrationservice.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lkksoftdev.registrationservice.otp.Otp;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User extends UserBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String onlineAccountStatus;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Otp> otps;

    public String toString() {
        return "User(id=" + this.getId() + ", username=" + this.getUsername()
                + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName()
                + ", mobile=" + this.getMobile() + ", email=" + this.getEmail()
                + ", role=" + this.getRole() + ", onlineAccountStatus=" + this.getOnlineAccountStatus();
    }
}
