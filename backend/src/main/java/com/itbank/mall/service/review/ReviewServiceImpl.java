package com.itbank.mall.service.review;

import com.itbank.mall.entity.review.Review;
import com.itbank.mall.mapper.orders.OrderItemMapper;
import com.itbank.mall.mapper.payment.PaymentMapper;
import com.itbank.mall.mapper.review.ReviewMapper;
import com.itbank.mall.service.mileage.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final PaymentMapper paymentMapper;
    private final OrderItemMapper orderItemMapper;
    private final MileageService mileageService;

    @Override
    public int writeReview(Review review) {
        // 1. 중복 리뷰 방지
        if (hasAlreadyWritten(review.getMemberId(), review.getOrderItemId())) {
            throw new IllegalStateException("이미 해당 주문에 대한 리뷰가 작성되었습니다.");
        }

        // 2. 리뷰 저장
        int result = reviewMapper.insertReview(review);

        // 3. 리뷰 성공 시 해당 주문상품 is_reviewed = true로 변경
        if (result > 0) {
            orderItemMapper.markAsReviewed(review.getOrderItemId());

            // 4. 마일리지 적립
            int paidAmount = paymentMapper.findPaidAmountById(review.getPaymentId());
            int mileageToSave = (int) Math.floor(paidAmount * 0.01);  // 1% 적립
            String description = "리뷰 작성 적립 (결제번호: " + review.getPaymentId() + ")";

            mileageService.saveMileage(review.getMemberId(), review.getPaymentId(), mileageToSave, description);
        }

        return result;
    }
   
    @Override
    public boolean hasAlreadyWritten(Long memberId, Long orderItemId) {
        return reviewMapper.findByMemberIdAndOrderItemId(memberId, orderItemId) != null;
    }
    
    @Override
    public List<Review> getProductReviews(Long productId) {
        return reviewMapper.findByProductId(productId);
    }
    
    @Override
    public boolean updateReview(Review review) {
        // 작성자 검증은 Mapper에서 id + member_id 조건으로 처리 권장
    	int updated = reviewMapper.updateReview(review); 
        return updated > 0;
    }

    @Override
    public boolean deleteReview(Long reviewId, Long memberId) {
        int deleted = reviewMapper.deleteReview(reviewId, memberId);
        // 필요하면 여기서 order_item.is_reviewed = false, 마일리지 회수 로직 추가
        return deleted > 0;
    }
} 


