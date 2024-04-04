package com.lkksoftdev.beneficiaryservice.beneficiary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findAllByCustomerId(Long customerId);

    Page<Beneficiary> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Beneficiary> findAllByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Optional<Beneficiary> findByIdAndCustomerId(Long beneficiaryId, Long customerId);

}
