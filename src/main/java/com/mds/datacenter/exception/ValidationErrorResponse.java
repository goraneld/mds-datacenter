package com.mds.datacenter.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ValidationErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<FieldValidationError> errors;

    public ValidationErrorResponse(int status, String error, List<FieldValidationError> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.errors = errors;
    }

}
