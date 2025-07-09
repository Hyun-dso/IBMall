package com.itbank.mall.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Event {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String bannerImage;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;

    @ManyToMany
    private List<Product> productList;
}
