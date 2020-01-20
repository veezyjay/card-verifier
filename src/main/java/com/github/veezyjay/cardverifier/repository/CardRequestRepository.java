package com.github.veezyjay.cardverifier.repository;

import com.github.veezyjay.cardverifier.domain.CardRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {

    @Query("SELECT request.card.cardNumber AS cardNumber, COUNT(request.card.cardNumber) AS count " +
            "FROM CardRequest AS request GROUP BY request.card.cardNumber ORDER BY COUNT(request.card.cardNumber) DESC")
    Slice<Map<String, Long>> getNumberOfHits(Pageable pageable);
}
