package com.lkksoftdev.registrationservice.user;

import com.lkksoftdev.registrationservice.auth.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public UserService(BCryptPasswordEncoder passwordEncoder,
                       CustomUserDetailsService customUserDetailsService,
                       JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    public CustomUserDetails findUserWithCredentials(LoginRequestDto loginRequestDto) {
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequestDto.getUsername());

        if (userDetails == null || !passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
            return null;
        }

        return userDetails;
    }

    public CustomUserDetails findUserWithUsername(String username) {
        return customUserDetailsService.loadUserByUsername(username);
    }

    public JwtResponseDto getJwtResponseDto(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtService.createToken(authentication);
        return new JwtResponseDto(token);
    }
}
