package com.github.veezyjay.cardverifier.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CardVerificationResponse {
    private boolean success;
    private Map<String, String> payload;
}
