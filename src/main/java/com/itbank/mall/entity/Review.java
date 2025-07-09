package com.itbank.mall.entity;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id @GeneratedValue
    private Long id;

    private String content;            // 리뷰 본문
    private int rating;                // 별점 (1~5)
    private boolean isVerifiedBuyer;   // 실제 구매자 여부

    @ManyToOne
    private User writer;               // 리뷰 작성자 (User 엔티티 필요)

    @ManyToOne
    private Product product;           // 어떤 상품에 대한 리뷰인지

    // Getter / Setter
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isVerifiedBuyer() {
        return isVerifiedBuyer;
    }

    public void setVerifiedBuyer(boolean verifiedBuyer) {
        isVerifiedBuyer = verifiedBuyer;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
