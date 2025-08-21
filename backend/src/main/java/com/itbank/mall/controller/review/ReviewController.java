package com.itbank.mall.controller.review;

import com.itbank.mall.entity.review.Review;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성 API
     * @param review 리뷰 정보 (productId, memberId, paymentId, orderItemId 등)
     * @return 등록 결과 메시지
     */
    @PostMapping
    public ApiResponse<String> writeReview(@RequestBody Review review) {
        boolean alreadyWritten = reviewService.hasAlreadyWritten(review.getMemberId(), review.getOrderItemId());

        if (alreadyWritten) {
            return ApiResponse.fail("이미 해당 주문에 리뷰를 작성하셨습니다.");
        }

        reviewService.writeReview(review);
        return ApiResponse.ok("리뷰가 등록되었습니다.");
    }

    /**
     * 상품별 리뷰 목록 조회 API
     * @param productId 리뷰를 조회할 상품 ID
     * @return 해당 상품에 대한 공개 리뷰 목록
     */
    // 상품별 리뷰 목록 조회
    @GetMapping("/product/{productId}")
    public ApiResponse<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ApiResponse.ok(reviews);
    }
}
