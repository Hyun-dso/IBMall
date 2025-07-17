package com.itbank.mall.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.PaymentRequestDto;
import com.itbank.mall.entity.OrderEntity;
import com.itbank.mall.entity.OrderItemEntity;
import com.itbank.mall.entity.Payment;
import com.itbank.mall.mapper.PaymentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderService orderService;

    @Transactional
    public void savePaymentLog(PaymentRequestDto dto, String transactionId, Long memberId) {
        
        // ✅ 유효성 검사
    	if (dto.getAmount() < 0) {
    	    throw new IllegalArgumentException("결제 금액은 0원 이상이어야 합니다");
    	}

    	if (dto.getProductId() == null) {
    	    throw new IllegalArgumentException("상품 ID가 누락되었습니다");
    	}

    	if (dto.getBuyer_tel() == null || dto.getBuyer_tel().isBlank()) {
    	    throw new IllegalArgumentException("구매자 전화번호는 필수입니다");
    	}

    	if (dto.getBuyer_address() == null || dto.getBuyer_address().isBlank()) {
    	    throw new IllegalArgumentException("배송지 주소는 필수입니다");
    	}

        // ✅ 결제 정보 저장
        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setOrderUid(dto.getMerchant_uid());
        payment.setProductName(dto.getName());
        payment.setOrderPrice(dto.getAmount());
        payment.setPaidAmount(dto.getAmount());
        payment.setPaymentMethod("card");
        payment.setStatus("paid");
        payment.setTransactionId(transactionId);
        payment.setPgProvider(dto.getPgProvider());
        payment.setCreatedAt(LocalDateTime.now());

        paymentMapper.insert(payment);

        // ✅ 주문 정보 저장
        OrderEntity order = new OrderEntity();
        order.setMemberId(memberId);
        order.setTotalPrice(dto.getAmount());
        order.setBuyerPhone(dto.getBuyer_tel());
        order.setBuyerAddress(dto.getBuyer_Address());  // 회원도 주소 직접 입력 가능
        order.setOrderType(memberId != null ? "MEMBER" : "GUEST");

        OrderItemEntity item = new OrderItemEntity();
        item.setProductId(dto.getProductId());
        item.setQuantity(1);
        item.setPrice(dto.getAmount());

        order.setOrderItems(List.of(item));

        orderService.createOrder(order);
    }

}
