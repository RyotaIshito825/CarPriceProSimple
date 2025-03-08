package com.techacademy.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techacademy.entity.PriceCard;
import com.techacademy.repository.PriceCardRepository;

@Service
public class PriceCardService {

    private final PriceCardRepository priceCardRepository;


    public PriceCardService(PriceCardRepository priceCardRepository) {
        this.priceCardRepository = priceCardRepository;
    }

    // プライスカード1件検索
    public PriceCard findByEmployeeId(Integer employeeId) {
        Optional<PriceCard> option = priceCardRepository.findByEmployeeId(employeeId);
        PriceCard priceCard = option.orElse(null);
        // 取得できなかった場合はnullを返す
        return priceCard;
    }

    // プライスカード登録処理
    public void save(PriceCard card) {
        PriceCard priceCard = findByEmployeeId(card.getEmployee().getId());
        if (priceCard != null) {
            priceCard.setPriceCardName(card.getPriceCardName());
            priceCardRepository.save(priceCard);
        } else {
            priceCardRepository.save(card);
        }
    }

}
