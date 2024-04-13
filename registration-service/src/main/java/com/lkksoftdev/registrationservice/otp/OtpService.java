package com.lkksoftdev.registrationservice.otp;

import com.lkksoftdev.registrationservice.exception.CustomResourceNotFoundException;
import com.lkksoftdev.registrationservice.user.OnlineAccountStatus;
import com.lkksoftdev.registrationservice.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(OtpService.class);
    private final AwsClientService awsClientService;

    public OtpService(OtpRepository otpRepository, AwsClientService awsClientService) {
        this.otpRepository = otpRepository;
        this.awsClientService = awsClientService;
    }

    private String generateOtpCode() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    public Map<String, String> setOtpForCustomer(User user, OnlineAccountStatus currentStatus) {
        String ownerIdentifier = UUID.randomUUID().toString();
        var otp = new Otp(generateOtpCode(), ownerIdentifier, user);
        LOGGER.info("Generated OTP: {}, Status: {}", otp, currentStatus.toString());
        otpRepository.save(otp);

        String message;
        String sesEmailSubject;

        if (currentStatus.equals(OnlineAccountStatus.PENDING)) {
            message = String.format("Otp for registration is %s and client id: %s", otp.getCode(), user.getId());
            sesEmailSubject = "Otp for registration";
        } else {
            message = "Otp for profile update is " + otp.getCode();
            sesEmailSubject = "Otp for profile update";
        }

        awsClientService.sendOtpToMobile(user.getMobile(), message);
        awsClientService.sendOtpToEmail(user.getEmail(), sesEmailSubject, message);

        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("ownerIdentifier", ownerIdentifier);
        response.put("message", "Otp sent to email and mobile");
        return response;
    }

    public Otp getOtpByCodeAndOwnerIdentifier(OtpDto otpDto) {
        LOGGER.info("Received OTP: {}", otpDto.toString());
        String code = otpDto.getOtp();
        String ownerIdentifier = otpDto.getOwnerIdentifier();
        Otp otp = otpRepository.findTopByCodeAndOwnerIdentifierOrderByCreatedAtDesc(code, ownerIdentifier);

        if (otp == null) {
            throw new CustomResourceNotFoundException("Invalid otp: " + otpDto.getOtp());
        }

        if (otp.isExpired()) {
            throw new CustomResourceNotFoundException("Otp expired: " + otpDto.getOtp());
        }

        if (otp.isVerified()) {
            throw new CustomResourceNotFoundException("Otp already verified: " + otpDto.getOtp());
        }

        return otp;
    }

    public void consumeOtp(Otp otp) {
        otp.setVerified(true);
        otpRepository.save(otp);
    }
}
