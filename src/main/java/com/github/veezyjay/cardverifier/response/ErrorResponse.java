package com.github.veezyjay.cardverifier.response;

import lombok.Data;

/**
 * Response structure for displaying errors to the client
 */
@Data
public class ErrorResponse {
    private boolean success;
    private String message;

    public ErrorResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
