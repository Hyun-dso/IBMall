package com.itbank.mall.service.member;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.track.MemberOrderDetailResponse;
import com.itbank.mall.dto.track.MemberOrderItemDto;
import com.itbank.mall.dto.track.MemberOrderSearchRequest;
import com.itbank.mall.dto.track.MemberOrderSearchResponse;
import com.itbank.mall.dto.track.MemberOrderSummaryDto;
import com.itbank.mall.mapper.orders.DeliveryMapper;
import com.itbank.mall.mapper.orders.OrderMapper;
import com.itbank.mall.mapper.row.OrderDetailRow;
import com.itbank.mall.mapper.row.OrderItemRow;
import com.itbank.mall.mapper.row.OrderSummaryRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberOrderQueryService {

    private static final int DEFAULT_LOOKBACK_DAYS = 90;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final OrderMapper orderMapper;
    private final DeliveryMapper deliveryMapper;

    /**
     * 회원 주문 목록 조회 (기간/상태/페이징)
     * - from/to가 없으면 최근 90일
     * - status = "ALL"이면 상태 필터 미적용
     */
    @Transactional(readOnly = true)
    public MemberOrderSearchResponse findMyOrders(Long memberId, MemberOrderSearchRequest req) {
        if (memberId == null) throw new IllegalArgumentException("memberId is required");

        LocalDate to = (req.getTo() != null) ? req.getTo() : LocalDate.now();
        LocalDate from = (req.getFrom() != null) ? req.getFrom() : to.minusDays(DEFAULT_LOOKBACK_DAYS);
        String status = (req.getStatus() == null) ? "ALL" : req.getStatus().trim().toUpperCase();

        int page = (req.getPage() != null) ? Math.max(0, req.getPage()) : DEFAULT_PAGE;
        int size = (req.getSize() != null) ? clamp(req.getSize(), 1, MAX_SIZE) : DEFAULT_SIZE;
        int offset = page * size;

        long total = orderMapper.countOrdersByMemberId(memberId, from, to, statusEqualsAll(status) ? null : status);
        List<OrderSummaryRow> rows = orderMapper.selectOrdersByMemberId(
                memberId, from, to, statusEqualsAll(status) ? null : status, offset, size);

        MemberOrderSearchResponse res = new MemberOrderSearchResponse();
        res.setPage(page);
        res.setSize(size);
        res.setTotalElements(total);
        res.setTotalPages(calcTotalPages(total, size));
        res.setContent(rows.stream().map(this::toSummaryDto).toList());
        return res;
    }

    /**
     * 회원 주문 상세 조회 (소유 검증 포함)
     */
    @Transactional(readOnly = true)
    public MemberOrderDetailResponse findMyOrderDetail(Long memberId, String orderUid) {
        if (memberId == null) throw new IllegalArgumentException("memberId is required");
        if (orderUid == null || orderUid.isBlank()) throw new IllegalArgumentException("orderUid is required");

        OrderDetailRow header = orderMapper.selectMemberOrderDetail(memberId, orderUid);
        if (header == null) {
            // 소유 불일치 or 존재하지 않음
            return null;
        }

        var itemRows = orderMapper.selectOrderItems(header.getOrderId());
        var items = itemRows.stream().map(this::toItemDto).toList();

        var delivery = deliveryMapper.findByOrderId(header.getOrderId());

        MemberOrderDetailResponse res = new MemberOrderDetailResponse();
        res.setOrderUid(header.getOrderUid());
        res.setCreatedAt(header.getCreatedAt());
        res.setStatus(header.getStatus());
        res.setTotalPrice(header.getTotalPrice());
        res.setItems(items);

        MemberOrderDetailResponse.Delivery d = new MemberOrderDetailResponse.Delivery();
        if (delivery != null) {
            d.setRecipient(delivery.getRecipient());
            d.setPhone(delivery.getPhone());
            d.setAddress1(delivery.getAddress1());
            d.setAddress2(delivery.getAddress2());
            d.setTrackingNumber(delivery.getTrackingNumber());
            d.setStatus(delivery.getStatus());
        } else {
            d.setStatus("READY");
        }
        res.setDelivery(d);
        return res;
    }

    /* ===== Mapping helpers ===== */

    private MemberOrderSummaryDto toSummaryDto(OrderSummaryRow r) {
        MemberOrderSummaryDto dto = new MemberOrderSummaryDto();
        dto.setOrderUid(r.getOrderUid());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setStatus(r.getStatus());
        dto.setTotalPrice(r.getTotalPrice());
        dto.setItemSummary(r.getItemSummary());  // 쿼리에서 조립
        dto.setHasDelivery(r.isHasDelivery());
        return dto;
    }

    private MemberOrderItemDto toItemDto(OrderItemRow r) {
        MemberOrderItemDto dto = new MemberOrderItemDto();
        dto.setProductId(r.getProductId());
        dto.setProductOptionId(r.getProductOptionId());
        dto.setProductName(r.getProductName());
        dto.setOptionName(r.getOptionName());
        dto.setQuantity(r.getQuantity());
        dto.setUnitPrice(r.getUnitPrice());
        return dto;
    }

    /* ===== Utils ===== */

    private static boolean statusEqualsAll(String s) {
        return "ALL".equalsIgnoreCase(s);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int calcTotalPages(long total, int size) {
        if (size <= 0) return 0;
        return (int) ((total + size - 1) / size);
    }
}
