package com.github.veezyjay.cardverifier.controller;

import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import com.github.veezyjay.cardverifier.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card-scheme")
public class CardController {
    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/verify/{cardNumber}")
    @ResponseStatus(HttpStatus.OK)
    public CardVerificationResponse verifyCard(@PathVariable String cardNumber) {
        return cardService.verifyCard(cardNumber);
    }
}
