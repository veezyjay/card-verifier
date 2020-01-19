package com.github.veezyjay.cardverifier.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds the card data which an external API call will return
 * Must be passed in as the last argument of the getForObject method of org.springframework.web.client.RestTemplate
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardData {
    private String scheme;
    private String type;
    private Bank bank;
}
