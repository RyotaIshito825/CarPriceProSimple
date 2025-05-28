package com.techacademy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.PriceCard;

public interface PriceCardRepository extends JpaRepository<PriceCard, Integer> {

    Optional<PriceCard> findByPriceCardName(String priceCardName);

}
