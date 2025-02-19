package io.goorm.youtube.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// ErrorResponse.java
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String detail;
}