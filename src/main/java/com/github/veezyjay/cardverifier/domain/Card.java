package com.github.veezyjay.cardverifier.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public Card(String cardNumber, String cardType, String scheme, String bank) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.scheme = scheme;
        this.bank = bank;
    }
}
