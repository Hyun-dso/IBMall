package com.itbank.mall.service.payment;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.orders.GuestOrderItemDto;
import com.itbank.mall.dto.payment.GuestCartPaymentRequestDto;
import com.itbank.mall.dto.payment.GuestPaymentRequestDto;
import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.entity.orders.OrderEntity;
import com.itbank.mall.entity.orders.OrderItemEntity;
import com.itbank.mall.entity.payment.Payment;
import com.itbank.mall.event.payment.PaymentCompletedEvent;
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
public class GuestPaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final DeliveryService deliveryService;
    private final ApplicationEventPublisher events;

    private final ProductMapper productMapper;               // ✅ 신규 주입
    private final ProductOptionMapper productOptionMapper;   // ✅ 신규 주입

    private final PaymentVerificationService verificationService;

    /** 비회원 단일 상품 결제 */
    @Transactional
    public void processGuestPayment(GuestPaymentRequestDto dto) {
    	final String paymentId = dto.getPaymentId();
        final String txId = (dto.getTransactionId() == null || dto.getTransactionId().isBlank())
                ? paymentId : dto.getTransactionId();
        final String orderUid = dto.getOrderUid();

        // (1) 서버 검증
        var v = verificationService.verifyOrThrow(paymentId, txId, dto.getPaidAmount());

        // (2) 멱등 게이트 - txId 기준
        Payment existing = paymentMapper.findByTransactionId(txId);
        if (existing != null) {
            if (existing.getOrderId() != null) {
                log.info("[IDEMPOTENT] payment already linked. txId={}, orderId={}", txId, existing.getOrderId());
                return;
            }
            Long orderId = createOrderAndDeliveryForSingle(dto, v.getAmount());
            paymentMapper.updateOrderIdByTransactionId(txId, orderId);
            publishPaymentMailEvent(dto.getBuyerEmail(), orderUid,
                    List.of(dto.getProductName() + " x " + dto.getQuantity() + "개"),
                    v.getAmount());
            return;
        }

        // (2-1) 멱등/충돌 프리체크 - orderUid 기준
        Payment byUid = paymentMapper.findByOrderUid(orderUid);
        if (byUid != null) {
            // 같은 orderUid로 이미 다른 txId가 사용된 경우 → 정책상 충돌(409 매핑 권장)
            if (byUid.getTransactionId() != null && !byUid.getTransactionId().equals(txId)) {
                throw new IllegalStateException("DUPLICATE_TRANSACTION: orderUid already used with a different txId");
            }
            // 이미 주문에 연결되어 있으면 멱등
            if (byUid.getOrderId() != null) {
                log.info("[IDEMPOTENT][orderUid] already linked. orderId={}", byUid.getOrderId());
                return;
            }
            // 주문 미연결이면 복구: 주문/배송 생성 후 기존 payment(txId)와 연결
            Long orderId = createOrderAndDeliveryForSingle(dto, v.getAmount());
            paymentMapper.updateOrderIdByTransactionId(txId, orderId);
            publishPaymentMailEvent(dto.getBuyerEmail(), orderUid,
                    List.of(dto.getProductName() + " x " + dto.getQuantity() + "개"),
                    v.getAmount());
            return;
        }

        // (3) payment 저장 - 검증값 반영
        Payment payment = new Payment();
        payment.setOrderUid(orderUid);
        payment.setPaymentId(paymentId);
        payment.setProductName(dto.getProductName());
        payment.setOrderPrice(dto.getOrderPrice());
        payment.setPaidAmount(v.getAmount());
        payment.setPaymentMethod(v.getPayMethod());
        payment.setStatus(v.getStatus());
        payment.setTransactionId(txId);
        payment.setPgProvider("INICIS");
        payment.setBuyerName(dto.getBuyerName());
        payment.setBuyerEmail(dto.getBuyerEmail());
        payment.setBuyerPhone(dto.getBuyerPhone());
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // (4) 주문/상세/배송
        Long orderId = createOrderAndDeliveryForSingle(dto, v.getAmount());
        // (5) 링크
        payment.setOrderId(orderId);
        paymentMapper.updateOrderId(payment);

        // (6) 커밋 후 메일
        publishPaymentMailEvent(dto.getBuyerEmail(), orderUid,
                List.of(dto.getProductName() + " x " + dto.getQuantity() + "개"),
                v.getAmount());
    }

    /** 비회원 장바구니 결제 */
    @Transactional
    public void processGuestCartPayment(GuestCartPaymentRequestDto dto) {
    	final String paymentId = dto.getPaymentId();
        final String txId = (dto.getTransactionId() == null || dto.getTransactionId().isBlank())
                ? paymentId : dto.getTransactionId();
        final String orderUid = dto.getOrderUid();

        // (1) 서버 검증
        var v = verificationService.verifyOrThrow(paymentId, txId, dto.getPaidAmount());

        // (2) 멱등 게이트 - txId 기준
        Payment existing = paymentMapper.findByTransactionId(txId);
        if (existing != null) {
            if (existing.getOrderId() != null) {
                log.info("[IDEMPOTENT] payment already linked. txId={}, orderId={}", txId, existing.getOrderId());
                return;
            }
            Long orderId = createOrderAndDeliveryForCart(dto, v.getAmount());
            paymentMapper.updateOrderIdByTransactionId(txId, orderId);
            publishPaymentMailEvent(dto.getBuyerEmail(), orderUid, buildCartLines(dto), v.getAmount());
            return;
        }

        // (2-1) 멱등/충돌 프리체크 - orderUid 기준
        Payment byUid = paymentMapper.findByOrderUid(orderUid);
        if (byUid != null) {
            if (byUid.getTransactionId() != null && !byUid.getTransactionId().equals(txId)) {
                throw new IllegalStateException("DUPLICATE_TRANSACTION: orderUid already used with a different txId");
            }
            if (byUid.getOrderId() != null) {
                log.info("[IDEMPOTENT][orderUid] already linked. orderId={}", byUid.getOrderId());
                return;
            }
            Long orderId = createOrderAndDeliveryForCart(dto, v.getAmount());
            paymentMapper.updateOrderIdByTransactionId(txId, orderId);
            publishPaymentMailEvent(dto.getBuyerEmail(), orderUid, buildCartLines(dto), v.getAmount());
            return;
        }

        // (3) payment 저장 - 검증값 반영
        Payment payment = new Payment();
        payment.setOrderUid(orderUid);
        payment.setPaymentId(paymentId);
        payment.setProductName(dto.getProductName());
        payment.setOrderPrice(dto.getOrderPrice());
        payment.setPaidAmount(v.getAmount());
        payment.setPaymentMethod(v.getPayMethod());
        payment.setStatus(v.getStatus());
        payment.setTransactionId(txId);
        payment.setPgProvider("INICIS");
        payment.setBuyerName(dto.getBuyerName());
        payment.setBuyerEmail(dto.getBuyerEmail());
        payment.setBuyerPhone(dto.getBuyerPhone());
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // (4) 주문/상세/배송
        Long orderId = createOrderAndDeliveryForCart(dto, v.getAmount());
        // (5) 링크
        payment.setOrderId(orderId);
        paymentMapper.updateOrderId(payment);

        // (6) 커밋 후 메일
        publishPaymentMailEvent(dto.getBuyerEmail(), orderUid, buildCartLines(dto), v.getAmount());
    }

    // ===== 내부 유틸 =====

    /** 단일 주문 생성 (서버 계산/재고 차감/옵션ID 저장) */
    private Long createOrderAndDeliveryForSingle(GuestPaymentRequestDto dto, int verifiedPaidAmount) {
        // 1) 서버 단가 계산
    	Integer base = productMapper.getPriceById(dto.getProductId());
    	if (base == null) throw new IllegalArgumentException("상품을 찾을 수 없습니다.");

    	int extra = 0;
    	if (dto.getProductOptionId() != null) {
    	    Integer ep = productOptionMapper.getExtraPriceById(dto.getProductOptionId());
    	    extra = (ep != null) ? ep : 0; // 옵션 ID가 무효면 0 처리(또는 422로 던져도 됨)
    	}
    	int unitPrice = base + extra;
    	
        // 2) 원자적 재고 차감 (옵션 우선)
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
        order.setMemberId(null);
        order.setBuyerName(dto.getBuyerName());
        order.setBuyerPhone(dto.getBuyerPhone());
        order.setTotalPrice(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderType("GUEST");
        order.setStatus("ORDERED");
        order.setOrderUid(dto.getOrderUid());
        orderMapper.insertOrder(order);

        // 5) 주문상세 (price=단가, 옵션ID 저장)
        OrderItemEntity item = new OrderItemEntity();
        item.setOrderId(order.getOrderId());
        item.setProductId(dto.getProductId());
        item.setProductOptionId(dto.getProductOptionId());  // ✅
        item.setQuantity(dto.getQuantity());
        item.setPrice(unitPrice);                           // ✅
        orderMapper.insertOrderItem(item);

        // 6) 배송
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(order.getOrderId());
        delivery.setRecipient(dto.getRecipientName());
        delivery.setPhone(dto.getRecipientPhone());
        delivery.setAddress1(dto.getRecipientAddress1());
        delivery.setAddress2(dto.getRecipientAddress2());
        delivery.setStatus("READY");
        deliveryService.saveDelivery(delivery);

        return order.getOrderId();
    }

    /** 장바구니 주문 생성 (서버 계산/재고 차감/옵션ID 저장) */
    private Long createOrderAndDeliveryForCart(GuestCartPaymentRequestDto dto, int verifiedPaidAmount) {
        // 1) (productId, optionId) 기준 합산
        Map<String, GuestOrderItemDto> merged = new LinkedHashMap<>();
        for (GuestOrderItemDto i : dto.getItems()) {
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

        for (GuestOrderItemDto l : lines) {
        	Integer base = productMapper.getPriceById(l.getProductId());
        	if (base == null) throw new IllegalArgumentException("상품을 찾을 수 없습니다.");

        	int extra = 0;
        	if (l.getProductOptionId() != null) {
        	    Integer ep = productOptionMapper.getExtraPriceById(l.getProductOptionId());
        	    extra = (ep != null) ? ep : 0; // 옵션 ID가 무효면 0 처리(또는 422로 던져도 됨)
        	}
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
        order.setMemberId(null);
        order.setBuyerName(dto.getBuyerName());
        order.setBuyerPhone(dto.getBuyerPhone());
        order.setTotalPrice(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderType("GUEST");
        order.setOrderUid(dto.getOrderUid());
        order.setStatus("ORDERED");
        orderMapper.insertOrder(order);

        // 5) 주문상세 (DTO의 item.price는 무시, 서버 단가 저장)
        for (Line c : calc) {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(order.getOrderId());
            item.setProductId(c.pid());
            item.setProductOptionId(c.oid()); // ✅
            item.setQuantity(c.qty());
            item.setPrice(c.unit());          // ✅ 단가
            orderMapper.insertOrderItem(item);
        }

        // 6) 배송
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(order.getOrderId());
        delivery.setRecipient(dto.getRecipientName());
        delivery.setPhone(dto.getRecipientPhone());
        delivery.setAddress1(dto.getRecipientAddress1());
        delivery.setAddress2(dto.getRecipientAddress2());
        delivery.setStatus("READY");
        deliveryService.saveDelivery(delivery);

        return order.getOrderId();
    }

    // ✅ 공통 이벤트로 발행 (게스트: memberId=null, email 사용)
    private void publishPaymentMailEvent(String email, String orderUid, List<String> productLines, int paidAmount) {
        events.publishEvent(new PaymentCompletedEvent(null, email, orderUid, productLines, paidAmount));
    }

    private List<String> buildCartLines(GuestCartPaymentRequestDto dto) {
        return dto.getItems().stream()
                .map(i -> i.getProductName() + " x " + i.getQuantity() + "개")
                .collect(Collectors.toList());
    }
}
