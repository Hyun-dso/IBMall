package com.itbank.mall.service.payment;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.orders.MemberOrderItemDto;
import com.itbank.mall.dto.payment.MemberCartPaymentRequestDto;
import com.itbank.mall.dto.payment.MemberPaymentRequestDto;
import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.entity.orders.OrderEntity;
import com.itbank.mall.entity.orders.OrderItemEntity;
import com.itbank.mall.entity.payment.Payment;
import com.itbank.mall.event.payment.PaymentCompletedEvent;
import com.itbank.mall.mapper.member.MemberMapper;
import com.itbank.mall.mapper.orders.OrderMapper;
import com.itbank.mall.mapper.payment.PaymentMapper;
import com.itbank.mall.mapper.product.ProductMapper;
import com.itbank.mall.mapper.product.ProductOptionMapper;
import com.itbank.mall.service.orders.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberPaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final DeliveryService deliveryService;
    private final PaymentVerificationService verificationService;
    private final ProductMapper productMapper;               // ✅ 신규 주입
    private final ProductOptionMapper productOptionMapper;   // ✅ 신규 주입
    private final MemberMapper memberMapper;

    private final ApplicationEventPublisher events;

    /** 회원 단일 상품 결제 */
    @Transactional
    public void processSinglePayment(MemberPaymentRequestDto dto, Long memberId) {
        final String txId = dto.getTransactionId();
        final String orderUid = dto.getOrderUid();
        

        // (1) 서버 검증
        var v = verificationService.verifyOrThrow(txId, dto.getPaidAmount());

        // (2) 멱등 게이트
        Payment existing = paymentMapper.findByTransactionId(txId);
        if (existing != null) {
            if (existing.getOrderId() != null) {
                log.info("[IDEMPOTENT] payment already linked. txId={}, orderId={}", txId, existing.getOrderId());
                return;
            }
            Long orderId = createOrderAndDeliveryForSingle(dto, memberId, v.getAmount());
            paymentMapper.updateOrderIdByTransactionId(txId, orderId);

            // 커밋 후 메일(멤버: email은 리스너에서 memberId로 조회)
            events.publishEvent(new PaymentCompletedEvent(
                    memberId, null, orderUid,
                    List.of("상품결제 x " + dto.getQuantity() + "개"),
                    v.getAmount()
            ));
            return;
        }

        // (3) payment 저장 — 검증값 반영
        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setOrderUid(orderUid);
        payment.setProductName("상품결제");
        payment.setOrderPrice(dto.getOriginalAmount());
        payment.setPaidAmount(v.getAmount());
        payment.setPaymentMethod(v.getPayMethod());
        payment.setStatus(v.getStatus());
        payment.setTransactionId(txId);
        payment.setPgProvider("INICIS");
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // (4) 주문/상세/배송
        Long orderId = createOrderAndDeliveryForSingle(dto, memberId, v.getAmount());
        // (5) 링크
        payment.setOrderId(orderId);
        paymentMapper.updateOrderId(payment);

        // (6) 커밋 후 메일 발행
        events.publishEvent(new PaymentCompletedEvent(
                memberId, null, orderUid,
                List.of("상품결제 x " + dto.getQuantity() + "개"),
                v.getAmount()
        ));
    }

    /** 회원 장바구니 결제 */
    @Transactional
    public void processCartPayment(MemberCartPaymentRequestDto dto, Long memberId) {
        final String txId = dto.getTransactionId();
        final String orderUid = dto.getOrderUid();

        // (1) 서버 검증
        var v = verificationService.verifyOrThrow(txId, dto.getPaidAmount());

        // (2) 멱등 게이트
        Payment existing = paymentMapper.findByTransactionId(txId);
        if (existing != null) {
            if (existing.getOrderId() != null) {
                log.info("[IDEMPOTENT] payment already linked. txId={}, orderId={}", txId, existing.getOrderId());
                return;
            }
            Long orderId = createOrderAndDeliveryForCart(dto, memberId, v.getAmount());
            paymentMapper.updateOrderIdByTransactionId(txId, orderId);

            events.publishEvent(new PaymentCompletedEvent(
                    memberId, null, orderUid,
                    dto.getItems().stream()
                            .map(i -> i.getProductName() + " x " + i.getQuantity() + "개")
                            .collect(Collectors.toList()),
                    v.getAmount()
            ));
            return;
        }

        // (3) payment 저장 — 검증값 반영
        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setOrderUid(orderUid);
        payment.setProductName("장바구니 결제");
        payment.setOrderPrice(dto.getOriginalAmount());
        payment.setPaidAmount(v.getAmount());
        payment.setPaymentMethod(v.getPayMethod());
        payment.setStatus(v.getStatus());
        payment.setTransactionId(txId);
        payment.setPgProvider("INICIS");
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // (4) 주문/상세/배송
        Long orderId = createOrderAndDeliveryForCart(dto, memberId, v.getAmount());
        // (5) 링크
        payment.setOrderId(orderId);
        paymentMapper.updateOrderId(payment);

        // (6) 커밋 후 메일 발행
        events.publishEvent(new PaymentCompletedEvent(
                memberId, null, orderUid,
                dto.getItems().stream()
                        .map(i -> i.getProductName() + " x " + i.getQuantity() + "개")
                        .collect(Collectors.toList()),
                v.getAmount()
        ));
    }

    // ===== 내부 유틸 =====

    /** 단일 주문 생성 (서버 계산/재고 차감/옵션ID 저장) */
    private Long createOrderAndDeliveryForSingle(MemberPaymentRequestDto dto, Long memberId, int verifiedPaidAmount) {
    	var m = memberMapper.findById(memberId);
    	
    	// 폴백: 프로필 → 수령자
    	String buyerName  = (m != null && m.getName()  != null && !m.getName().isBlank())
    	        ? m.getName()  : dto.getRecipientName();
    	String buyerPhone = (m != null && m.getPhone() != null && !m.getPhone().isBlank())
    	        ? m.getPhone() : dto.getRecipientPhone();

    	// (필수) 최종 안전장치
    	if (buyerName == null || buyerName.isBlank() || buyerPhone == null || buyerPhone.isBlank()) {
    	    throw new IllegalArgumentException("수령자 이름/전화번호가 필요합니다.");
    	}
    	
        // 1) 서버 단가 계산
        int base = productMapper.getPriceById(dto.getProductId());
        int extra = (dto.getProductOptionId() != null)
                ? productOptionMapper.getExtraPriceById(dto.getProductOptionId())
                : 0;
        int unitPrice = base + extra;

        // 2) 원자적 재고 차감
        int affected = (dto.getProductOptionId() != null)
                ? productOptionMapper.decreaseStock(dto.getProductOptionId(), dto.getQuantity())
                : productMapper.decreaseStock(dto.getProductId(), dto.getQuantity());
        if (affected != 1) {
            // ControllerAdvice에서 409로 매핑 권장
            throw new IllegalStateException("재고 부족 또는 동시성으로 실패");
        }

        // 3) 합계 검증
        int total = unitPrice * dto.getQuantity();
        if (total != verifiedPaidAmount) {
            // ControllerAdvice에서 422로 매핑 권장
            throw new IllegalArgumentException("결제 금액 불일치(total != verifiedPaidAmount)");
        }

        // 4) 주문
        OrderEntity order = new OrderEntity();
        order.setMemberId(memberId);
        order.setBuyerName(buyerName);
        order.setBuyerPhone(buyerPhone);
        order.setTotalPrice(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderType("MEMBER");
        order.setStatus("주문완료");
        orderMapper.insertOrder(order);

        // 5) 주문상세 (price=단가, 옵션ID 저장)
        OrderItemEntity item = new OrderItemEntity();
        item.setOrderId(order.getOrderId());
        item.setProductId(dto.getProductId());
        item.setProductOptionId(dto.getProductOptionId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(unitPrice);
        orderMapper.insertOrderItem(item);

        // 6) 배송
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(order.getOrderId());
        delivery.setRecipient(dto.getRecipientName());
        delivery.setPhone(dto.getRecipientPhone());
        delivery.setAddress(dto.getRecipientAddress());
        delivery.setStatus("배송준비중");
        deliveryService.saveDelivery(delivery);

        return order.getOrderId();
    }

    /** 장바구니 주문 생성 (서버 계산/재고 차감/옵션ID 저장) */
    private Long createOrderAndDeliveryForCart(MemberCartPaymentRequestDto dto, Long memberId, int verifiedPaidAmount) {
    	
    	var m = memberMapper.findById(memberId);
    	
    	// 폴백: 프로필 → 수령자
    	String buyerName  = (m != null && m.getName()  != null && !m.getName().isBlank())
    	        ? m.getName()  : dto.getRecipientName();
    	String buyerPhone = (m != null && m.getPhone() != null && !m.getPhone().isBlank())
    	        ? m.getPhone() : dto.getRecipientPhone();

    	// (필수) 최종 안전장치
    	if (buyerName == null || buyerName.isBlank() || buyerPhone == null || buyerPhone.isBlank()) {
    	    throw new IllegalArgumentException("수령자 이름/전화번호가 필요합니다.");
    	}

        // 1) (productId, optionId) 기준 합산
        Map<String, MemberOrderItemDto> merged = new LinkedHashMap<>();
        for (MemberOrderItemDto i : dto.getItems()) {
            String key = i.getProductId() + ":" + (i.getProductOptionId() == null ? "-" : i.getProductOptionId());
            merged.compute(key, (k, v) -> {
                if (v == null) return i;
                v.setQuantity(v.getQuantity() + i.getQuantity());
                return v;
            });
        }

        // 2) 정렬(교착 최소화) + 원자적 재고 차감 + 단가 계산
        var lines = merged.values().stream()
                .sorted((a, b) -> {
                    int c = Long.compare(a.getProductId(), b.getProductId());
                    if (c != 0) return c;
                    long ao = a.getProductOptionId() == null ? 0L : a.getProductOptionId();
                    long bo = b.getProductOptionId() == null ? 0L : b.getProductOptionId();
                    return Long.compare(ao, bo);
                })
                .toList();

        int total = 0;
        record Line(long pid, Long oid, int qty, int unit) {}
        java.util.List<Line> calc = new java.util.ArrayList<>();

        for (MemberOrderItemDto l : lines) {
            int base = productMapper.getPriceById(l.getProductId());
            int extra = (l.getProductOptionId() != null)
                    ? productOptionMapper.getExtraPriceById(l.getProductOptionId())
                    : 0;
            int unit = base + extra;

            int affected = (l.getProductOptionId() != null)
                    ? productOptionMapper.decreaseStock(l.getProductOptionId(), l.getQuantity())
                    : productMapper.decreaseStock(l.getProductId(), l.getQuantity());
            if (affected != 1) {
                throw new IllegalStateException("재고 부족 또는 동시성으로 실패");
            }

            total += unit * l.getQuantity();
            calc.add(new Line(l.getProductId(), l.getProductOptionId(), l.getQuantity(), unit));
        }

        // 3) 검증 금액 일치 확인
        if (total != verifiedPaidAmount) {
            throw new IllegalArgumentException("결제 금액 불일치(total != verifiedPaidAmount)");
        }

        // 4) 주문
        OrderEntity order = new OrderEntity();
        order.setMemberId(memberId);
        order.setBuyerName(buyerName);
        order.setBuyerPhone(buyerPhone);
        order.setTotalPrice(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderType("MEMBER");
        order.setStatus("주문완료");
        orderMapper.insertOrder(order);

        // 5) 주문상세 (DTO 가격 무시, 서버 단가 저장)
        for (Line c : calc) {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(order.getOrderId());
            item.setProductId(c.pid());
            item.setProductOptionId(c.oid());  // ✅
            item.setQuantity(c.qty());
            item.setPrice(c.unit());           // ✅ 단가
            orderMapper.insertOrderItem(item);
        }

        // 6) 배송
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(order.getOrderId());
        delivery.setRecipient(dto.getRecipientName());
        delivery.setPhone(dto.getRecipientPhone());
        delivery.setAddress(dto.getRecipientAddress());
        delivery.setStatus("배송준비중");
        deliveryService.saveDelivery(delivery);

        return order.getOrderId();
    }
}
