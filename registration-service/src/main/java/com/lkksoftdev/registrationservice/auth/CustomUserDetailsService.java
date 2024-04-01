package com.lkksoftdev.registrationservice.auth;

import com.lkksoftdev.registrationservice.user.User;
import com.lkksoftdev.registrationservice.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);
        return new CustomUserDetails(user.getUsername(), user.getPassword(), authorities, user.getOnlineAccountStatus(), user.getId());
    }
}