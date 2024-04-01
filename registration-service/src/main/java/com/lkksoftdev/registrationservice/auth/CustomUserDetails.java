package com.lkksoftdev.registrationservice.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final String onlineAccountStatus;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String onlineAccountStatus) {
        super(username, password, authorities);
        this.onlineAccountStatus = onlineAccountStatus;
    }

}
