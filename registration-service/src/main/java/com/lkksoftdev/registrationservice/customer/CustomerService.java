package com.lkksoftdev.registrationservice.customer;

import com.lkksoftdev.registrationservice.otp.Otp;
import com.lkksoftdev.registrationservice.otp.OtpService;
import com.lkksoftdev.registrationservice.user.OnlineAccountStatus;
import com.lkksoftdev.registrationservice.user.User;
import com.lkksoftdev.registrationservice.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomerService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomerService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static boolean validateFields(CustomerOnlineRegistrationDto customerOnlineRegistrationDto, User user) {
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

        return user;
    }

    @Transactional
    public void activateCustomer(Otp otp, OtpService otpService) {
        var user = otp.getUser();
        user.setOnlineAccountStatus(OnlineAccountStatus.ACTIVE);
        userRepository.save(user);
        otpService.consumeOtp(otp);
    }
}
