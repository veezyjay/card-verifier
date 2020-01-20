package com.github.veezyjay.cardverifier.service;

import com.github.veezyjay.cardverifier.domain.Card;
import com.github.veezyjay.cardverifier.domain.CardData;
import com.github.veezyjay.cardverifier.exception.CardNotFoundException;
import com.github.veezyjay.cardverifier.repository.CardRepository;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    RestTemplate restTemplate;

    @Mock
    CardRepository cardRepository;

    @InjectMocks
    CardServiceImpl cardService;

    Card card;

    @BeforeEach
    void setUp() {
        card = new Card("532241", "credit", "mastercard", "GT Bank");
    }

    @Test
    void verifyCardWithValidCardNumberFromExternalApi() {
        CardData cardData = new CardData();
        cardData.setType("credit");
        cardData.setScheme("mastercard");

        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), any())).thenReturn(cardData);
        when(cardRepository.save(any())).thenReturn(card);

        CardVerificationResponse result = cardService.verifyCard("532241");

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

    @Test
    void verifyCardWithValidCardNumberFromLocalDatabase() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);

        CardVerificationResponse result = cardService.verifyCard("532241");

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

    @Test
    void verifyCardNumberNotFound() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), any())).thenThrow(CardNotFoundException.class);

        assertThrows(CardNotFoundException.class, () -> cardService.verifyCard("678910"));
    }

    @Test
    void rejectInvalidCardNumberFormat() {
        assertThrows(IllegalArgumentException.class, () -> cardService.verifyCard("416"));
        assertThrows(IllegalArgumentException.class, () -> cardService.verifyCard("xyzabc"));
        assertThrows(IllegalArgumentException.class, () -> cardService.verifyCard("123ab4"));
    }
}