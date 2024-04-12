package com.lkksoftdev.loanservice.feign;

public record AccountTransferDto(double emiAmount, Long loanId, String description) {
}
