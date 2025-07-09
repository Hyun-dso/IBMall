package com.itbank.mall.entity;

import jakarta.persistence.*;

@Entity
public class Option {

    @Id @GeneratedValue
    private Long id;

    private String name;      // e.g., "Size:M", "Color:Red"
    private int priceDiff;
    private int stock;

    @ManyToOne
    private Product product;

    // Getter/Setter 생략 가능 (Lombok 사용 시)
}
