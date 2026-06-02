package com.payplux.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private int statusCode;
    private String errorCode;
    private String path;
    private LocalDateTime timestamp;
}
