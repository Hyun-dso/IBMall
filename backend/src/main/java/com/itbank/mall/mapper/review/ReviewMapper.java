package com.itbank.mall.mapper.review;

import com.itbank.mall.entity.review.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {

    // 리뷰 작성
    int insertReview(Review review);

    // 유저가 해당 주문 상세(order_item_id)에 대해 이미 리뷰를 작성했는지 확인
    Review findByMemberIdAndOrderItemId(@Param("memberId") Long memberId,
                                        @Param("orderItemId") Long orderItemId);

    // 상품별 리뷰 조회
    List<Review> findByProductId(@Param("productId") Long productId);
    
    // ✅ 리뷰 수정
    int updateReview(Review review);

    // ✅ 리뷰 삭제 (작성자 본인만)
    int deleteReview(@Param("reviewId") Long reviewId,
                     @Param("memberId") Long memberId);
}