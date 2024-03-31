package com.lkksoftdev.registrationservice.customer;

import com.lkksoftdev.registrationservice.common.ResponseDto;
import com.lkksoftdev.registrationservice.exception.CustomResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableMethodSecurity
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER')")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        var customerProfileResponse = customerService.findActiveCustomerProfileWithUsername(authentication.getName());

        if (customerProfileResponse == null) {
            throw new CustomResourceNotFoundException("Customer not found for the auth token");
        }

        return new ResponseEntity<>(new ResponseDto(customerProfileResponse, null), HttpStatus.OK);
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER')")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody CustomerProfileUpdateDto customerProfileUpdateDto, Authentication authentication) {
        var customer = customerService.findActiveCustomerWithUsername(authentication.getName());

        if (customer == null) {
            throw new CustomResourceNotFoundException("Customer not found for the auth token");
        }

        if (!customerService.areProfileDetailsValid(customerProfileUpdateDto, customer)) {
            throw new CustomResourceNotFoundException("Customer profile details do not match the existing profile");
        }

        var response = customerService.updateCustomerProfile(customerProfileUpdateDto, customer);
        return new ResponseEntity<>(new ResponseDto(response, null), HttpStatus.OK);
    }

    @PostMapping("/profile/otp")
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER')")
    public void submitOtpForProfileUpdate() {
        // Submit OTP for profile update
    }
}
