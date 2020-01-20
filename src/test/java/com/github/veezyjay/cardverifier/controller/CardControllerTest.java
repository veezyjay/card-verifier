package com.github.veezyjay.cardverifier.controller;

import com.github.veezyjay.cardverifier.exception.CardNotFoundException;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import com.github.veezyjay.cardverifier.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    CardService cardService;

    @InjectMocks
    CardController cardController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @Test
    void verifyCardWithValidCardNumber() throws Exception {
        CardVerificationResponse response = CardVerificationResponse.builder().success(true).payload(new HashMap<>()).build();

        when(cardService.verifyCard(anyString())).thenReturn(response);
        mockMvc.perform(get("/card-scheme/verify/{cardNumber}", "532212"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.payload").exists());
    }

    @Test
    void handleCardNotFound() throws Exception {
        when(cardService.verifyCard(anyString())).thenReturn(null);

        try {
            mockMvc.perform(get("/card-scheme/verify/{cardNumber}", "678910"));
        } catch (NestedServletException e) {
            assertEquals(CardNotFoundException.class, e.getRootCause().getClass());
        }
    }

    @Test
    void handleCardWithInvalidCardNumber() throws Exception {
        when(cardService.verifyCard(anyString())).thenThrow(IllegalArgumentException.class);

        try {
            mockMvc.perform(get("/card-scheme/verify/{cardNumber}", "abc456"));
        } catch (NestedServletException e) {
            assertEquals(IllegalArgumentException.class, e.getRootCause().getClass());
        }
    }
}