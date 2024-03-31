package com.lkksoftdev.registrationservice.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Otp findTopByCodeAndOwnerIdentifierOrderByCreatedAtDesc(String code, String ownerIdentifier);
}
