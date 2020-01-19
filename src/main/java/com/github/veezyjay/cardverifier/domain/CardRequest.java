package com.github.veezyjay.cardverifier.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a single request for verification made for a particular card
 */
@Entity
@Data
@NoArgsConstructor
public class CardRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_at")
    @CreationTimestamp
    private LocalDateTime requestedAt;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "card_id")
    private Card card;
}
