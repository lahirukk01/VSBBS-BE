package com.lkksoftdev.registrationservice.otp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lkksoftdev.registrationservice.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@NoArgsConstructor
@Getter
@Setter
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String ownerIdentifier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private User user;

    private boolean verified;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Otp(String code, String ownerIdentifier, User user) {
        this.code = code;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(5);
        this.ownerIdentifier = ownerIdentifier;
        this.verified = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public String toString() {
        return "Otp(id=" + this.getId() + ", code=" + this.getCode()
                + ", createdAt=" + this.getCreatedAt()
                + ", expiresAt=" + this.getExpiresAt()
                + ", ownerIdentifier=" + this.getOwnerIdentifier()
                + ", userId=" + this.getUser().getId() + ")";
    }
}
