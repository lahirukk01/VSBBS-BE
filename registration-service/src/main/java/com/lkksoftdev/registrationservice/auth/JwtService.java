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
import java.util.Objects;

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
        var userId = Objects.equals(userDetails.getOnlineAccountStatus(), OnlineAccountStatus.ACTIVE.toString()) ? userDetails.getId() : "";
        var issuedAt = Instant.now();
        var expiresAt = issuedAt.plusSeconds(60 * 60);

        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .claim("userId", userId)
                .claim("scope", createScope(authentication))
                .claim("onlineAccountStatus", userDetails.getOnlineAccountStatus())
                .claim("iat", issuedAt.getEpochSecond())
                .claim("exp", expiresAt.getEpochSecond())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String[] createScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    public IntrospectResponseDataDto validateTokenAndGetClaims(String token) {
        CustomUserDetails userDetails;
        LOGGER.info("Token: {}", token);

        try {
            var jwt = jwtDecoder.decode(token);
            String username = jwt.getClaimAsString("sub");
            userDetails = customUserDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            LOGGER.error("Invalid token: {}, error: {}", token, e.getMessage());
            throw new CustomBadRequestException("Invalid token: " + e.getMessage());
        }

        if (userDetails == null) {
            throw new CustomBadRequestException("Invalid token");
        }

        if (!allowedOnlineAccountStatuses.contains(userDetails.getOnlineAccountStatus())){
            throw new CustomBadRequestException("User account is not active");
        }

        String scope = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        return new IntrospectResponseDataDto(userDetails.getId(), scope, userDetails.getOnlineAccountStatus());
    }
}

