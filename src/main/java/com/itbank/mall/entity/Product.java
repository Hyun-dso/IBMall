package com.itbank.mall.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private String imageUrl;
    private int price;
    private int stock;

    private int views = 0;
    private int likes = 0;
    private int dislikes = 0;

    private boolean isTimeDeal = false;
    private double mileageRate = 0.005;

    @ElementCollection
    private List<String> tags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Option> options;

    // Getter/Setter (Lombok 사용 시 생략 가능)
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ... (다른 필드도 같은 방식)
}
