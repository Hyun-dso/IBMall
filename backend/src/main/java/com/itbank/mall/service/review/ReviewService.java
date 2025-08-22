package com.itbank.mall.service.review;

import com.itbank.mall.entity.review.Review;

import java.util.List;

public interface ReviewService {

    int writeReview(Review review);

    boolean hasAlreadyWritten(Long memberId, Long orderItemId);

    List<Review> getProductReviews(Long productId);
    
    // ✅ 새로 추가
    boolean updateReview(Review review);

    boolean deleteReview(Long reviewId, Long memberId);
}

