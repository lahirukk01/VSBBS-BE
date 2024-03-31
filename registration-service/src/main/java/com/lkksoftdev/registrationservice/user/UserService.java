package com.lkksoftdev.registrationservice.user;

import com.lkksoftdev.registrationservice.auth.JwtResponseDto;
import com.lkksoftdev.registrationservice.auth.JwtService;
import com.lkksoftdev.registrationservice.auth.LoginRequestDto;
import com.lkksoftdev.registrationservice.auth.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       CustomUserDetailsService customUserDetailsService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    public UserDetails findUserWithCredentials(LoginRequestDto loginRequestDto) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequestDto.getUsername());

        if (userDetails == null || !passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
            return null;
        }

        return userDetails;
    }

    public UserDetails findUserWithUsername(String username) {
        return customUserDetailsService.loadUserByUsername(username);
    }

    public JwtResponseDto getJwtResponseDto(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtService.createToken(authentication);
        return new JwtResponseDto(token);
    }
}
