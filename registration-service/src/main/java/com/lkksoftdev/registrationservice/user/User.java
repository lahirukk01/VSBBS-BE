package com.lkksoftdev.registrationservice.user;

import com.lkksoftdev.registrationservice.otp.Otp;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private OnlineAccountStatus onlineAccountStatus;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Otp> otps;

    public String toString() {
        return "User(id=" + this.getId() + ", username=" + this.getUsername()
                + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName()
                + ", mobile=" + this.getMobile() + ", email=" + this.getEmail()
                + ", role=" + this.getRole() + ", onlineAccountStatus=" + this.getOnlineAccountStatus();
    }
}
