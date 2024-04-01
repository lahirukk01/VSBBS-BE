package com.lkksoftdev.registrationservice.auth;

import com.lkksoftdev.registrationservice.common.ResponseDto;
import com.lkksoftdev.registrationservice.customer.CustomerOnlineRegistrationDto;
import com.lkksoftdev.registrationservice.customer.CustomerService;
import com.lkksoftdev.registrationservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.registrationservice.otp.OtpDto;
import com.lkksoftdev.registrationservice.otp.OtpService;
import com.lkksoftdev.registrationservice.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final CustomerService customerService;
    private final OtpService otpService;
    private final UserService userService;

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    public AuthController(CustomerService customerService, OtpService otpService,
                          UserService userService) {
        this.customerService = customerService;
        this.otpService = otpService;
        this.userService = userService;
    }

    @PostMapping("/registration/customer")
    public ResponseEntity<?> register(@Valid @RequestBody CustomerOnlineRegistrationDto customerOnlineRegistrationDto) {
        var user = customerService.findCustomerWithRegistrationDetails(customerOnlineRegistrationDto);

        if (user == null) {
            throw new CustomResourceNotFoundException("Customer not found with given details: " + customerOnlineRegistrationDto.getSafeSummaryString());
        }

        var response = otpService.setOtpForCustomer(user);
        return new ResponseEntity<>(new ResponseDto(response, null), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        var userDetails = userService.findUserWithCredentials(loginRequestDto);

        if (userDetails == null) {
            LOGGER.error("Invalid credentials: " + loginRequestDto.getUsername());
            throw new CustomResourceNotFoundException("Invalid credentials");
        }

        JwtResponseDto response = userService.getJwtResponseDto(userDetails);
        return new ResponseEntity<>(new ResponseDto(response, null), HttpStatus.OK);
    }

    @PostMapping("/otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpDto otpDto) {
        var otp = otpService.getOtpByCodeAndOwnerIdentifier(otpDto);

        customerService.activateCustomer(otp, otpService);

        var userDetails = userService.findUserWithUsername(otp.getUser().getUsername());
        JwtResponseDto response = userService.getJwtResponseDto(userDetails);

        return new ResponseEntity<>(new ResponseDto(response, null), HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("Ok");
    }
}
