package com.github.veezyjay.cardverifier.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a card that could be verified
 * A card can have many requests for verification
 */
@Entity
@Data
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_type")
    private String cardType;

    private String scheme;

    private String bank;

    @Column(name = "added_at")
    @CreationTimestamp
    private LocalDateTime addedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<CardRequest> requests;

    public Card(String cardNumber, String cardType, String scheme, String bank) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.scheme = scheme;
        this.bank = bank;
    }

    public void addRequest(CardRequest request) {
        if (this.requests == null) {
            this.requests = new ArrayList<>();
        }
        request.setCard(this);
        this.requests.add(request);
    }
}
