package com.github.veezyjay.cardverifier.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Represents the response structure when a request for number of hits is being made
 * Shows information about all the cards and the number of request made for each card
 * Supports pagination by providing start and limit
 */
@Data
@Builder
public class CardStatsResponse {
    private boolean success;
    private int start;
    private int limit;
    private long size;
    private Map<String, Long> payload;
}
