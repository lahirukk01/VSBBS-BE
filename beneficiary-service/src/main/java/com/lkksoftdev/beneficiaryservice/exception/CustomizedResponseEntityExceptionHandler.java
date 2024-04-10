package com.lkksoftdev.beneficiaryservice.exception;

import com.lkksoftdev.beneficiaryservice.common.ErrorDetails;
import com.lkksoftdev.beneficiaryservice.common.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ResponseEntity<ResponseDto> buildResponseEntity(String errorMessage, WebRequest request, HttpStatus status) {
        ErrorDetails errorDetails = new ErrorDetails(
                errorMessage,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(new ResponseDto(null, errorDetails), status);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ResponseDto> handleAllExceptions(Exception ex, WebRequest request) {
        LOGGER.error("Exception occurred: {}", ex.toString());
        return buildResponseEntity("Something went wrong", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomResourceNotFoundException.class)
    public final ResponseEntity<ResponseDto> handleCustomResourceNotFoundException(
            CustomResourceNotFoundException ex, WebRequest request) {
        return buildResponseEntity(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public final ResponseEntity<ResponseDto> handleCustomBadRequestException(
            CustomBadRequestException ex, WebRequest request) {
        return buildResponseEntity(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<ResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        LOGGER.error("MethodArgumentTypeMismatchException occurred: {}", ex.toString());
        return buildResponseEntity("Invalid path parameter type", request, HttpStatus.BAD_REQUEST);
    }
}
