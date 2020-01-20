package com.github.veezyjay.cardverifier.service;

import com.github.veezyjay.cardverifier.domain.Card;
import com.github.veezyjay.cardverifier.domain.CardData;
import com.github.veezyjay.cardverifier.exception.CardNotFoundException;
import com.github.veezyjay.cardverifier.repository.CardRepository;
import com.github.veezyjay.cardverifier.repository.CardRequestRepository;
import com.github.veezyjay.cardverifier.response.CardStatsResponse;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    @Mock
    CardRequestRepository cardRequestRepository;

    @InjectMocks
    CardServiceImpl cardService;

    @Test
    void verifyCardWithValidCardNumberFromExternalApi() {
        Card card = new Card("532241", "credit", "mastercard", "GT Bank");
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
        Card card = new Card("532241", "credit", "mastercard", "GT Bank");
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

    @Test
    void getNumberOfHitsWithValidArguments() {
        Map<String, Long> data1 = new HashMap<>();
        data1.put("cardNumber", 416712L);
        data1.put("count", 4L);

        Map<String, Long> data2 = new HashMap<>();
        data2.put("cardNumber", 416796L);
        data2.put("count", 2L);

        List<Map<String, Long>> content = new ArrayList<>();
        content.add(data1);
        content.add(data2);
        Slice<Map<String, Long>> page = new SliceImpl<>(content);

        when(cardRequestRepository.getNumberOfHits(any())).thenReturn(page);
        when(cardRequestRepository.findAll()).thenReturn(new ArrayList<>());

        CardStatsResponse fullData = cardService.getNumberOfHits(1, 5);
        assertTrue(fullData.isSuccess());
        assertTrue(fullData.getSize() > -1);
        assertNotNull(fullData.getPayload());
    }

    @Test
    void rejectRequestForNumberOfHitsWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> cardService.getNumberOfHits(-5, 8));
        assertThrows(IllegalArgumentException.class, () -> cardService.getNumberOfHits(1, -4));
    }
}