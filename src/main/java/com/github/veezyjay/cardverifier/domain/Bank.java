package com.github.veezyjay.cardverifier.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A necessary property of the CardData class
 * Holds the bank information which an external API call will return
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bank {
    private String name;
}
