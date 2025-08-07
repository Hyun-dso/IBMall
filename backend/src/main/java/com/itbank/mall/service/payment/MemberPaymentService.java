package com.itbank.mall.service.payment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.payment.MemberPaymentRequestDto;
import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.entity.orders.OrderEntity;
import com.itbank.mall.entity.orders.OrderItemEntity;
import com.itbank.mall.entity.payment.Payment;
import com.itbank.mall.mapper.orders.OrderMapper;
import com.itbank.mall.mapper.payment.PaymentMapper;
import com.itbank.mall.service.orders.DeliveryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final DeliveryService deliveryService;

    @Transactional
    public void processSinglePayment(MemberPaymentRequestDto dto, Long memberId) {
        // 1. payment 저장
        Payment payment = new Payment();
        payment.setOrderUid(dto.getOrderUid());
        payment.setProductName("상품결제");  // 상세 명 필요 시 확장 가능
        payment.setOrderPrice(dto.getOriginalAmount());  // 총 금액 (할인 전)
        payment.setPaidAmount(dto.getPaidAmount());      // 실제 결제 금액 (할인 적용)
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());
        payment.setTransactionId(dto.getTransactionId());
        payment.setPgProvider(dto.getPgProvider());
        payment.setMemberId(memberId);
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // 2. orders 저장
        OrderEntity order = new OrderEntity();
        order.setMemberId(memberId);
        order.setBuyerName(null);
        order.setBuyerPhone(null);  // 회원의 기본 정보 DB에 따로 있을 경우 생략 가능
        order.setTotalPrice(dto.getPaidAmount());
        order.setOrderType("MEMBER");
        order.setStatus("주문완료");
        orderMapper.insertOrder(order);

        // 3. order_items 저장
        OrderItemEntity item = new OrderItemEntity();
        item.setOrderId(order.getOrderId());
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPaidAmount());  // 할인 적용 후 금액 (단일 상품 기준)
        orderMapper.insertOrderItem(item);

        // 4. deliveries 저장
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(order.getOrderId());
        delivery.setRecipient(dto.getRecipientName());
        delivery.setPhone(dto.getRecipientPhone());
        delivery.setAddress(dto.getRecipientAddress());
        delivery.setStatus("배송준비중");
        deliveryService.saveDelivery(delivery);
    }
}
