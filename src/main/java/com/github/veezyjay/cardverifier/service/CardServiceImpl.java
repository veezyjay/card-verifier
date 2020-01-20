package com.github.veezyjay.cardverifier.service;

import com.github.veezyjay.cardverifier.domain.Card;
import com.github.veezyjay.cardverifier.domain.CardData;
import com.github.veezyjay.cardverifier.domain.CardRequest;
import com.github.veezyjay.cardverifier.exception.CardNotFoundException;
import com.github.veezyjay.cardverifier.repository.CardRepository;
import com.github.veezyjay.cardverifier.repository.CardRequestRepository;
import com.github.veezyjay.cardverifier.response.CardStatsResponse;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CardServiceImpl implements CardService {
    private RestTemplate restTemplate;
    private CardRepository cardRepository;
    private CardRequestRepository cardRequestRepository;
    private String binListApiUrl;

    public CardServiceImpl(RestTemplate restTemplate, CardRepository cardRepository,
                           CardRequestRepository cardRequestRepository,
                           @Value("${binlist.api.url}") String binListApiUrl) {
        this.restTemplate = restTemplate;
        this.cardRepository = cardRepository;
        this.cardRequestRepository = cardRequestRepository;
        this.binListApiUrl = binListApiUrl;
    }

    /**
     * Verifies and fetches data about a particular card based on its card number
     * @param cardNumber, The card number to be verified
     * @return Data related to the card if found
     */
    @Override
    public CardVerificationResponse verifyCard(String cardNumber) {
        if ( !cardNumber.matches("\\d{6,16}")) {
            throw new IllegalArgumentException(cardNumber + " does not meet the required format. Must be six or more digits");
        }
        Card theCard;
        Optional<Card> cardOptional = cardRepository.findByCardNumber(cardNumber);
        if (cardOptional.isEmpty()) {
            CardData cardData;
            try {
                cardData = restTemplate.getForObject(binListApiUrl + cardNumber, CardData.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                    throw new CardNotFoundException("Card with the number " + cardNumber + " could not be found");
                } else if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode())) {
                    throw new IllegalArgumentException(cardNumber + " does not meet the required format");
                } else {
                    throw new RuntimeException("Something went wrong. Please try again later");
                }
            }
            String bank = cardData.getBank() != null ? cardData.getBank().getName() : null;
            theCard = new Card(cardNumber, cardData.getType(), cardData.getScheme(), bank);
            log.info("Card gotten from external api");

        } else {
            theCard = cardOptional.get();
            log.info("Card found in local database");
        }
        theCard.addRequest(new CardRequest());
        cardRepository.save(theCard);

        Map<String, String> payload = new HashMap<>();
        payload.put("scheme", theCard.getScheme());
        payload.put("type", theCard.getCardType());
        payload.put("bank", theCard.getBank());
        return CardVerificationResponse.builder().success(true).payload(payload).build();
    }

    /**
     * Fetches all requests being made to every card, and carries out pagination based on the start and limit parameters
     * @param start, representing the page to start from
     * @param limit, represents the maximum size of each page to be returned
     * @return total number of requests made, the pagination arguments, and data about each card
     */
    @Override
    public CardStatsResponse getNumberOfHits(int start, int limit) {
        if (start < 1 || limit < 1) {
            throw new IllegalArgumentException("start and limit must both be greater than zero");
        }
        Pageable pageable = PageRequest.of(start - 1, limit);
        Slice<Map<String, Long>> pagedResult = cardRequestRepository.getNumberOfHits(pageable);
        Map<String, Long> payload = new ConcurrentHashMap<>();

        if (pagedResult.hasContent()) {
            for (Map<String, Long> cardStat : pagedResult) {
                payload.put(String.valueOf(cardStat.get("cardNumber")), cardStat.get("count"));
            }
        }

        long size = cardRequestRepository.findAll().size();
        return CardStatsResponse
                .builder()
                .success(true)
                .start(start)
                .limit(limit)
                .size(size)
                .payload(payload)
                .build();
    }
}
