package com.github.veezyjay.cardverifier.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response structure for displaying errors to the client
 */
@Data
@Builder
public class ErrorResponse {
    private String success;
    private String message;
}
