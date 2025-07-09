package com.itbank.mall.entity;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id @GeneratedValue
    private Long id;

    private String content;
    private int rating;
    private boolean isVerifiedBuyer;

    @ManyToOne
    private User writer;

    @ManyToOne
    private Product product;
}
