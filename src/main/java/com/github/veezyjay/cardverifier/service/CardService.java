package com.github.veezyjay.cardverifier.service;

import com.github.veezyjay.cardverifier.response.CardStatsResponse;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;

public interface CardService {
    CardVerificationResponse verifyCard(String cardNumber);
    CardStatsResponse getNumberOfHits(int start, int limit);
}
