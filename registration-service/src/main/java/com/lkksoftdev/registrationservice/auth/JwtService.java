package com.lkksoftdev.registrationservice.auth;


import com.lkksoftdev.registrationservice.exception.CustomBadRequestException;
import com.lkksoftdev.registrationservice.user.OnlineAccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    private static final List<String> allowedOnlineAccountStatuses = List.of(
            OnlineAccountStatus.ACTIVE.toString(),
            OnlineAccountStatus.UPDATE_REQUESTED.toString(),
            OnlineAccountStatus.ID_VERIFICATION_PENDING.toString()
    );

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, CustomUserDetailsService customUserDetailsService) {
        super();
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    public String createToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60 * 30))
                .subject(authentication.getName())
                .claim("scope", createScope(authentication))
                .claim("onlineAccountStatus", userDetails.getOnlineAccountStatus())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String[] createScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    public String validateTokenAndGetScope(String token) {
        CustomUserDetails userDetails;
        LOGGER.info("Token: " + token);

        try {
            var jwt = jwtDecoder.decode(token);
            String username = jwt.getClaimAsString("sub");
            userDetails = customUserDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            throw new CustomBadRequestException("Invalid token");
        }

        if (userDetails == null) {
            throw new CustomBadRequestException("Invalid token");
        }

        if (!allowedOnlineAccountStatuses.contains(userDetails.getOnlineAccountStatus())){
            throw new CustomBadRequestException("User account is not active");
        }

        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }
}

