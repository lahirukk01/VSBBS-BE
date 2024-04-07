package com.lkksoftdev.loanservice.common;

import java.time.LocalDateTime;

public record ErrorDetails(String message, String details, LocalDateTime timestamp) {
}
