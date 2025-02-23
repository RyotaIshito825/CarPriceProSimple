package com.techacademy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "pricecards")
public class PriceCard {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;

    // プライスカード名
    @Column(length = 100, nullable = false)
    private String priceCardName;

    // ファイルパス
    @Column(nullable = false)
    private String filePath;
}
