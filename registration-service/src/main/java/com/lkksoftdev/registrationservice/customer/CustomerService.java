package com.lkksoftdev.registrationservice.customer;

import com.lkksoftdev.registrationservice.exception.CustomBadRequestException;
import com.lkksoftdev.registrationservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.registrationservice.otp.Otp;
import com.lkksoftdev.registrationservice.otp.OtpService;
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

        if (!user.getOnlineAccountStatus().equals(User.OnlineAccountStatus.PENDING.toString())) {
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
        user.setOnlineAccountStatus(User.OnlineAccountStatus.ID_VERIFICATION_PENDING.toString());
        userRepository.save(user);
        otpService.consumeOtp(otp);
    }

    public boolean areProfileDetailsValid(CustomerProfileActivationDto customerProfileActivationDto, User user) {
        return user.getId().equals(customerProfileActivationDto.getId())
                && user.getEmail().equals(customerProfileActivationDto.getEmail())
                && user.getMobile().equals(customerProfileActivationDto.getMobile());
    }

    @Transactional
    public Map<String, String> initiateUpdatingCustomerProfile(User user) {
        user.setOnlineAccountStatus(User.OnlineAccountStatus.UPDATE_REQUESTED.toString());
        userRepository.save(user);
        return otpService.setOtpForCustomer(user, User.OnlineAccountStatus.UPDATE_REQUESTED);
    }

    @Transactional
    public void completeProfileUpdate(User user, Otp otp) {
        user.setOnlineAccountStatus(User.OnlineAccountStatus.ACTIVE.toString());
        userRepository.save(user);
        otpService.consumeOtp(otp);
    }

    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomResourceNotFoundException("User not found"));
    }
}
