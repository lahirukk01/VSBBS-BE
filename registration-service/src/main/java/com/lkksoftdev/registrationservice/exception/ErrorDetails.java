package com.lkksoftdev.registrationservice.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorDetails {
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ErrorDetails(String message, String details, LocalDateTime timestamp) {
        super();
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }
}
