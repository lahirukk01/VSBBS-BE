package com.lkksoftdev.accountservice.legacy.auth;

import com.lkksoftdev.accountservice.legacy.IntrospectClient;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenValidationFilter extends OncePerRequestFilter {
    private final IntrospectClient introspectClient;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthTokenValidationFilter.class);

    public AuthTokenValidationFilter(IntrospectClient introspectClient) {
        super();
        this.introspectClient = introspectClient;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization header");
            return;
        }

        LOGGER.info("Validating token: {}", authHeader);

        final String token = authHeader.substring(7);
        var introspectRequest = new IntrospectRequestDto(token);
        ResponseEntity<IntrospectResponseDto> introspectResponse;

        try {
            introspectResponse = introspectClient.validateToken(introspectRequest);
        } catch (FeignException e) {
            LOGGER.error("Error while validating token: {}", e.getMessage());

            if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid auth token");
                return;
            }

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while validating token");
            return;
        }

        LOGGER.info("Token validation response: {}", introspectResponse);

        if (introspectResponse.getStatusCode() != HttpStatus.OK) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
