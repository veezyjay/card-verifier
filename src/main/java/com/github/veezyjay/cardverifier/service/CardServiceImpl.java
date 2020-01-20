package com.github.veezyjay.cardverifier.service;

import com.github.veezyjay.cardverifier.domain.Card;
import com.github.veezyjay.cardverifier.domain.CardData;
import com.github.veezyjay.cardverifier.domain.CardRequest;
import com.github.veezyjay.cardverifier.exception.CardNotFoundException;
import com.github.veezyjay.cardverifier.repository.CardRepository;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CardServiceImpl implements CardService {
    private RestTemplate restTemplate;
    private CardRepository cardRepository;
    private String binListApiUrl;

    public CardServiceImpl(RestTemplate restTemplate, CardRepository cardRepository,
                           @Value("${binlist.api.url}") String binListApiUrl) {
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
}
