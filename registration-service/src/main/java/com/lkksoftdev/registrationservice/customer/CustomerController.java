package com.lkksoftdev.registrationservice.customer;

import com.lkksoftdev.registrationservice.auth.JwtResponseDto;
import com.lkksoftdev.registrationservice.common.ResponseDto;
import com.lkksoftdev.registrationservice.exception.CustomBadRequestException;
import com.lkksoftdev.registrationservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.registrationservice.otp.Otp;
import com.lkksoftdev.registrationservice.otp.OtpDto;
import com.lkksoftdev.registrationservice.otp.OtpService;
import com.lkksoftdev.registrationservice.user.User;
import com.lkksoftdev.registrationservice.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@EnableMethodSecurity
@RequestMapping("/users")
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;
    private final OtpService otpService;

    public CustomerController(CustomerService customerService, UserService userService, OtpService otpService) {
        this.customerService = customerService;
        this.userService = userService;
        this.otpService = otpService;
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER')")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody CustomerProfileActivationDto customerProfileActivationDto, Authentication authentication) {
        var customer = customerService.findCustomerWithUsername(authentication.getName());

        if (customer == null) {
            throw new CustomResourceNotFoundException("Customer not found for the auth token");
        }

        if (!customerService.areProfileDetailsValid(customerProfileActivationDto, customer)) {
            throw new CustomBadRequestException("Invalid customer profile details provided");
        }

        var response = customerService.initiateUpdatingCustomerProfile(customer);
        return new ResponseEntity<>(new ResponseDto(response, null), HttpStatus.OK);
    }

    @PostMapping("/profile/otp")
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER')")
    public ResponseEntity<?> submitOtpForProfileUpdate(@Valid @RequestBody OtpDto otpDto, Authentication authentication) {
        Otp otp = otpService.getOtpByCodeAndOwnerIdentifier(otpDto);
        User user = otp.getUser();

        if (!user.getUsername().equals(authentication.getName())) {
            throw new CustomResourceNotFoundException("Invalid OTP for the user");
        }

        customerService.completeProfileUpdate(user, otp);
        var userDetails = userService.findUserWithUsername(user.getUsername());
        JwtResponseDto response = userService.getJwtResponseDto(userDetails);
        return new ResponseEntity<>(new ResponseDto(response, null), HttpStatus.OK);
    }

    /**
     * Manager can get info of any customer or himself.
     * A customer can get info of himself only.
     * */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCustomerInfo(@PathVariable @Min(0) Integer userId, Authentication authentication) {
        String userScope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new CustomBadRequestException("Invalid token"));

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = jwtAuthenticationToken.getTokenAttributes();

        if (userScope.equals("SCOPE_CUSTOMER") && !Long.valueOf(userId).equals(claims.get("userId"))) {
            throw new CustomBadRequestException("Invalid customer id");
        }

        User user;

        if (userId == 0) {
            user = customerService.findCustomerWithUsername(authentication.getName());
        } else {
            user = customerService.findUserById(userId);
        }

        if (userScope.equals("SCOPE_CUSTOMER") && !claims.get("onlineAccountStatus").equals("ACTIVE")) {
            user.setEmail(null);
            user.setMobile(null);
            user.setId(0);
        }

        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(user, "User"), HttpStatus.OK);
    }
}
