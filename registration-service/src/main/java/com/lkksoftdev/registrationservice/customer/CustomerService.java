package com.lkksoftdev.registrationservice.customer;

import com.lkksoftdev.registrationservice.exception.CustomBadRequestException;
import com.lkksoftdev.registrationservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.registrationservice.otp.Otp;
import com.lkksoftdev.registrationservice.otp.OtpService;
import com.lkksoftdev.registrationservice.user.OnlineAccountStatus;
import com.lkksoftdev.registrationservice.user.User;
import com.lkksoftdev.registrationservice.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class CustomerService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public CustomerService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    private boolean validateFields(CustomerOnlineRegistrationDto customerOnlineRegistrationDto, User user) {
        return Objects.equals(customerOnlineRegistrationDto.getFirstName(), user.getFirstName())
                && Objects.equals(customerOnlineRegistrationDto.getLastName(), user.getLastName())
                && Objects.equals(customerOnlineRegistrationDto.getEmail(), user.getEmail())
                && Objects.equals(customerOnlineRegistrationDto.getMobile(), user.getMobile());
    }

    public User findCustomerWithRegistrationDetails(CustomerOnlineRegistrationDto customerOnlineRegistrationDto) {
        var user = userRepository.findByUsername(customerOnlineRegistrationDto.getUsername());

        if (user == null || !passwordEncoder.matches(customerOnlineRegistrationDto.getPassword(), user.getPassword())
        || !validateFields(customerOnlineRegistrationDto, user)) {
            return null;
        }

        if (user.getOnlineAccountStatus().equals(OnlineAccountStatus.ACTIVE.toString())) {
            throw new CustomBadRequestException("User is already registered");
        }

        return user;
    }

    public User findCustomerWithUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void registerCustomer(Otp otp, OtpService otpService) {
        var user = otp.getUser();
        user.setOnlineAccountStatus(OnlineAccountStatus.ID_VERIFICATION_PENDING.toString());
        userRepository.save(user);
        otpService.consumeOtp(otp);
    }

    public boolean areProfileDetailsValid(CustomerProfileUpdateDto customerProfileUpdateDto, User user) {
        return user.getId().equals(customerProfileUpdateDto.getId())
                && user.getEmail().equals(customerProfileUpdateDto.getEmail())
                && user.getMobile().equals(customerProfileUpdateDto.getMobile());
    }

    @Transactional
    public Map<String, String> initiateUpdatingCustomerProfile(CustomerProfileUpdateDto customerProfileUpdateDto, User user) {
        user.setFirstName(customerProfileUpdateDto.getFirstName());
        user.setLastName(customerProfileUpdateDto.getLastName());
        user.setUsername(customerProfileUpdateDto.getUsername());
        user.setOnlineAccountStatus(OnlineAccountStatus.UPDATE_REQUESTED.toString());
        userRepository.save(user);

        return otpService.setOtpForCustomer(user, OnlineAccountStatus.UPDATE_REQUESTED);
    }

    @Transactional
    public Map<String, String> completeProfileUpdate(User user, Otp otp) {
        user.setOnlineAccountStatus(OnlineAccountStatus.ACTIVE.toString());
        userRepository.save(user);
        otpService.consumeOtp(otp);

        return Map.of("message", "Profile updated successfully");
    }

    public User findCustomerById(Integer customerId) {
        return userRepository.findById(customerId)
                .orElseThrow(() -> new CustomResourceNotFoundException("Customer not found"));
    }
}
