package com.lkksoftdev.beneficiaryservice.beneficiary;

import com.lkksoftdev.beneficiaryservice.common.ResponseDto;
import com.lkksoftdev.beneficiaryservice.exception.CustomBadRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(BeneficiaryController.class);

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    // Create a beneficiary by customer
    @PostMapping("/{customerId}/beneficiaries")
    ResponseEntity<?> createBeneficiary(@Valid @RequestBody BeneficiaryBase beneficiaryBase, @PathVariable Long customerId) {
        Beneficiary beneficiary = beneficiaryService.createBeneficiary(beneficiaryBase, customerId);
        var responseData = new BeneficiaryCreateResponseDto(beneficiary.getId(), beneficiary.getStatus());
        return new ResponseEntity<>(new ResponseDto(responseData, null), HttpStatus.CREATED);
    }

    // Get all beneficiaries by customer
    @GetMapping("/{customerId}/beneficiaries")
    ResponseEntity<?> getBeneficiaries(@PathVariable Long customerId) {
        List<Beneficiary> beneficiaries = beneficiaryService.getCustomerBeneficiaries(customerId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(beneficiaries, Beneficiary.class), HttpStatus.OK);
    }

    // Get a beneficiary by customer
    @GetMapping("/{customerId}/beneficiaries/{beneficiaryId}")
    ResponseEntity<?> getBeneficiary(@PathVariable @Min(1) Long customerId, @PathVariable @Min(1) Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryService.getBeneficiaryByCustomer(customerId, beneficiaryId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(beneficiary, Beneficiary.class), HttpStatus.OK);
    }

    // Update a beneficiary by customer
    @PutMapping("/{customerId}/beneficiaries/{beneficiaryId}")
    ResponseEntity<?> updateBeneficiary(@Valid @RequestBody BeneficiaryBase beneficiaryBase, @PathVariable Long customerId, @PathVariable Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryService.updateBeneficiaryByCustomer(beneficiaryBase, customerId, beneficiaryId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(beneficiary, Beneficiary.class), HttpStatus.OK);
    }

    // Delete a beneficiary by customer
    @DeleteMapping("/{customerId}/beneficiaries/{beneficiaryId}")
    ResponseEntity<?> deleteBeneficiary(@PathVariable Long customerId, @PathVariable Long beneficiaryId) {
        beneficiaryService.getBeneficiaryByCustomer(customerId, beneficiaryId);
        beneficiaryService.deleteBeneficiary(customerId, beneficiaryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get all beneficiaries by manager
    @GetMapping("/beneficiaries")
    ResponseEntity<?> getAllBeneficiaries(
            @RequestParam(value = "page", required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "status", required = false) BeneficiaryStatus status) {
        LOGGER.info("Getting beneficiaries with page={}, status={}", page, status);
        List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiaries(page, 10, status);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(beneficiaries, Beneficiary.class), HttpStatus.OK);
    }

    // Approve or disapprove a beneficiary by manager
    @PutMapping("/beneficiaries/{beneficiaryId}")
    ResponseEntity<?> updateBeneficiary(@RequestBody BeneficiaryApproveRequestDto beneficiaryApproveRequestDto, @PathVariable @Min(1) Long beneficiaryId) {
        if (!beneficiaryService.isValidBeneficiaryStatus(beneficiaryApproveRequestDto.status())) {
            throw new CustomBadRequestException("Invalid status");
        }

        Beneficiary beneficiary = beneficiaryService.updateBeneficiaryByManager(beneficiaryApproveRequestDto, beneficiaryId);
        return new ResponseEntity<>(ResponseDto.BuildSuccessResponse(beneficiary, Beneficiary.class), HttpStatus.OK);
    }
}
