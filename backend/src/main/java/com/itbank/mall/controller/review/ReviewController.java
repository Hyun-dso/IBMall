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
    @GetMapping("/product/{productId}")
    public ApiResponse<List<Review>> getProductReviews(@PathVariable("productId") Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ApiResponse.ok(reviews);
    }

    /**
     * 리뷰 수정 API
     * @param reviewId 수정할 리뷰 ID (path)
     * @param review   수정할 내용(평점/내용 등). memberId는 권한 확인에 사용
     * @return 수정 결과 메시지
     */
    @PutMapping("/{reviewId}")
    public ApiResponse<String> updateReview(@PathVariable("reviewId") Long reviewId,
                                            @RequestBody Review review) {
        review.setId(reviewId); // path ID 강제 매핑
        boolean updated = reviewService.updateReview(review); // (작성자/권한 체크는 서비스)
        if (!updated) {
            return ApiResponse.fail("리뷰를 찾을 수 없거나 권한이 없습니다.");
        }
        return ApiResponse.ok("리뷰가 수정되었습니다.");
    }

    /**
     * 리뷰 삭제 API (작성자 본인만)
     * @param reviewId 삭제할 리뷰 ID (path)
     * @param memberId 작성자 본인 확인용(간단 테스트용)
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/{reviewId}")
    public ApiResponse<String> deleteReview(@PathVariable("reviewId") Long reviewId,
                                            @RequestParam("memberId") Long memberId) {
        boolean deleted = reviewService.deleteReview(reviewId, memberId);
        if (!deleted) {
            return ApiResponse.fail("리뷰를 찾을 수 없거나 권한이 없습니다.");
        }
        return ApiResponse.ok("리뷰가 삭제되었습니다.");
    }
}
