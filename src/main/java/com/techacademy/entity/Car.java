package com.techacademy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "cars")
public class Car {

    public static enum Classification {
        NEW("新車・未使用車"), USED("中古車");

        private String name;
        private Classification(String name) {
            this.name = name;
        }
        public String getValue() {
            return this.name;
        }
    }
    public static enum JapaneseCalendar {
        REIWA("R"), HEISEI("H");

        private String name;
        private JapaneseCalendar(String name) {
            this.name = name;
        }
        public String getValue() {
            return this.name;
        }
    }

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // メーカー
    @Column(nullable = false)
    private String maker;

    // 車種
    @Column(nullable = false)
    private String carModel;

    // グレード
    @Column(nullable = false)
    private String grade;

    // 区分
    @Column(columnDefinition="VARCHAR(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Classification classification;

    // 初度登録和暦
    @Column(columnDefinition="VARCHAR(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private JapaneseCalendar firstJc;

    // 初度登録年
    @Column(length = 2, nullable = false)
    private Integer registrationYear;

    // 初度登録月
    @Column(length = 2, nullable = false)
    private Integer registrationMonth;

    // 車検和暦
    @Column(columnDefinition="VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    private JapaneseCalendar viJc;

    // 車検年
    @Column(length = 2)
    private Integer viYear;

    // 車検月
    @Column(length = 2)
    private Integer viMonth;

    // プライス
    @Column(length = 4, nullable = false)
    private Integer price;

    // プライスの小数点
    @Column(length = 1, nullable = false)
    private Integer priceDpf;

    // 総額金額
    @Column(length = 4, nullable = false)
    private Integer totalPrice;

    // 総額金額の小数点
    @Column(length = 1, nullable = false)
    private Integer totalPriceDpf;

    // 走行距離
    @Column(length = 6, nullable = false)
    private Integer mileage;
}
