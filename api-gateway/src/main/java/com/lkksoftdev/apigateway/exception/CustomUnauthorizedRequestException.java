package com.lkksoftdev.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class CustomUnauthorizedRequestException extends RuntimeException{
    public CustomUnauthorizedRequestException(String message) {
        super(message);
    }
}
