package com.itbank.mall.service.payment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.orders.GuestOrderItemDto;
import com.itbank.mall.dto.payment.GuestCartPaymentRequestDto;
import com.itbank.mall.dto.payment.GuestPaymentRequestDto;
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
public class GuestPaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final DeliveryService deliveryService;

    @Transactional
    public void processGuestPayment(GuestPaymentRequestDto dto) {
        // 1. payment 저장
        Payment payment = new Payment();
        payment.setOrderUid(dto.getOrderUid());
        payment.setProductName(dto.getProductName());
        payment.setOrderPrice(dto.getOrderPrice());
        payment.setPaidAmount(dto.getPaidAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());
        payment.setTransactionId(dto.getTransactionId());
        payment.setPgProvider(dto.getPgProvider());
        payment.setBuyerName(dto.getBuyerName());
        payment.setBuyerEmail(dto.getBuyerEmail());
        payment.setBuyerPhone(dto.getBuyerPhone());
        payment.setBuyerAddress(dto.getBuyerAddress());
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // 2. orders 저장
        OrderEntity order = new OrderEntity();
        order.setMemberId(null); // 비회원
        order.setBuyerPhone(dto.getBuyerPhone());
        order.setBuyerAddress(dto.getBuyerAddress());
        order.setTotalPrice(dto.getPaidAmount());
        order.setOrderType("GUEST");
        order.setStatus("주문완료");
        orderMapper.insertOrder(order); // order_id 생성

        // 3. order_items 저장
        OrderItemEntity item = new OrderItemEntity();
        item.setOrderId(order.getOrderId());
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPaidAmount());
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
    
    @Transactional
    public void processGuestCartPayment(GuestCartPaymentRequestDto dto) {
        // 1. payment 저장
        Payment payment = new Payment();
        payment.setOrderUid(dto.getOrderUid());
        payment.setProductName(dto.getProductName());
        payment.setOrderPrice(dto.getOrderPrice());
        payment.setPaidAmount(dto.getPaidAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());
        payment.setTransactionId(dto.getTransactionId());
        payment.setPgProvider(dto.getPgProvider());
        payment.setBuyerName(dto.getBuyerName());
        payment.setBuyerEmail(dto.getBuyerEmail());
        payment.setBuyerPhone(dto.getBuyerPhone());
        payment.setBuyerAddress(dto.getBuyerAddress());
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // 2. orders 저장
        OrderEntity order = new OrderEntity();
        order.setMemberId(null); // 비회원
        order.setBuyerPhone(dto.getBuyerPhone());
        order.setBuyerAddress(dto.getBuyerAddress());
        order.setTotalPrice(dto.getPaidAmount());  // 총 금액
        order.setOrderType("GUEST");
        order.setStatus("주문완료");
        orderMapper.insertOrder(order);  // order_id 생성됨

        // 3. order_items 저장 (여러 개)
        for (GuestOrderItemDto i : dto.getItems()) {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(order.getOrderId());
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());  // 단일 상품 가격 × 수량
            orderMapper.insertOrderItem(item);
        }

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
