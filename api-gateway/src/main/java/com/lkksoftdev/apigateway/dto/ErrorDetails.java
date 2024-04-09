package com.lkksoftdev.apigateway.dto;

import java.time.LocalDateTime;

public record ErrorDetails(String message, String details, LocalDateTime timestamp) {
}
