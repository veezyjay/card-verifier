package com.github.veezyjay.cardverifier.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Represents the response structure when a card verification request is being made
 */
@Data
@Builder
public class CardVerificationResponse {
    private boolean success;
    private Map<String, String> payload;
}
