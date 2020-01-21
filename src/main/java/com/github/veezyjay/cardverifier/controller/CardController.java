package com.github.veezyjay.cardverifier.controller;

import com.github.veezyjay.cardverifier.response.CardStatsResponse;
import com.github.veezyjay.cardverifier.response.CardVerificationResponse;
import com.github.veezyjay.cardverifier.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card-scheme")
@CrossOrigin
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

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public CardStatsResponse getNumberOfHits(@RequestParam(defaultValue = "1") int start,
                                             @RequestParam(defaultValue = "5") int limit) {
        return cardService.getNumberOfHits(start, limit);
    }
}
