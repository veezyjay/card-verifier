package com.github.veezyjay.cardverifier.service;

import com.github.veezyjay.cardverifier.repository.CardRepository;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CardServiceImpl implements CardService {
    private RestTemplate restTemplate;
    private CardRepository cardRepository;
    private String binListApiUrl;

    public CardServiceImpl(RestTemplate restTemplate, CardRepository cardRepository,
                           @Value("${binlist.api.url") String binListApiUrl) {
        this.restTemplate = restTemplate;
        this.cardRepository = cardRepository;
        this.binListApiUrl = binListApiUrl;
    }

    /**
     * Verifies and fetches data about a particular card based on its card number
     * @param cardNumber, The card number to be verified
     * @return Data related to the card if found
     */
    @Override
    public CardVerificationResponse verifyCard(String cardNumber) {
        return null;
    }
}
