package com.itbank.mall.service.track;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.orders.GuestOrderItemDto;
import com.itbank.mall.dto.track.GuestOrderDetailResponse;
import com.itbank.mall.dto.track.GuestOrderSearchRequest;
import com.itbank.mall.dto.track.GuestOrderSearchResponse;
import com.itbank.mall.dto.track.GuestOrderSummaryDto;
import com.itbank.mall.mapper.orders.DeliveryMapper;
import com.itbank.mall.mapper.orders.OrderMapper;

// 아래 Row 클래스들은 다음 단계에서 함께 제공할 예정
import com.itbank.mall.mapper.row.OrderSummaryRow;
import com.itbank.mall.mapper.row.OrderDetailRow;
import com.itbank.mall.mapper.row.OrderItemRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrackingQueryService {

    private static final int DEFAULT_LOOKBACK_DAYS = 90;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final OrderMapper orderMapper;
    private final DeliveryMapper deliveryMapper;

    /* ===========================
     *  Public APIs
     * =========================== */

    /** 비회원 주문 목록 조회 (이름+전화번호, 기간/페이징) */
    @Transactional(readOnly = true)
    public GuestOrderSearchResponse findGuestOrders(GuestOrderSearchRequest req) {
        String name = safeTrim(req.getName());
        String phone = normalizePhone(req.getPhone());
        validateRequired(name, "name");
        validateRequired(phone, "phone");

        LocalDate to = (req.getTo() != null) ? req.getTo() : LocalDate.now();
        LocalDate from = (req.getFrom() != null) ? req.getFrom() : to.minusDays(DEFAULT_LOOKBACK_DAYS);

        int page = (req.getPage() != null) ? Math.max(0, req.getPage()) : DEFAULT_PAGE;
        int size = (req.getSize() != null) ? clamp(req.getSize(), 1, MAX_SIZE) : DEFAULT_SIZE;
        int offset = page * size;

        long total = orderMapper.countGuestOrdersByNamePhone(name, phone, from, to);
        List<OrderSummaryRow> rows = orderMapper.selectGuestOrdersByNamePhone(name, phone, from, to, offset, size);

        GuestOrderSearchResponse res = new GuestOrderSearchResponse();
        res.setPage(page);
        res.setSize(size);
        res.setTotalElements(total);
        res.setTotalPages(calcTotalPages(total, size));
        res.setContent(rows.stream().map(this::toSummaryDto).toList());
        return res;
    }

    /** 비회원 주문 상세 조회 (이름+전화번호 소유 검증 포함) */
    @Transactional(readOnly = true)
    public GuestOrderDetailResponse findGuestOrderDetail(String orderUid, String nameRaw, String phoneRaw) {
        String name = safeTrim(nameRaw);
        String phone = normalizePhone(phoneRaw);
        validateRequired(orderUid, "orderUid");
        validateRequired(name, "name");
        validateRequired(phone, "phone");

        OrderDetailRow order = orderMapper.selectGuestOrderDetail(orderUid, name, phone);
        if (order == null) {
            // 소유 불일치 또는 주문 없음 → 정보 노출 방지
            return null;
        }

        List<OrderItemRow> itemRows = orderMapper.selectOrderItems(order.getOrderId());
        var items = itemRows.stream().map(this::toItemDto).toList();

        var delivery = deliveryMapper.findByOrderId(order.getOrderId());

        GuestOrderDetailResponse res = new GuestOrderDetailResponse();
        res.setOrderUid(order.getOrderUid());
        res.setCreatedAt(order.getCreatedAt());
        res.setStatus(order.getStatus());
        res.setTotalPrice(order.getTotalPrice());
        res.setItems(items);

        GuestOrderDetailResponse.DeliveryBrief brief = new GuestOrderDetailResponse.DeliveryBrief();
        if (delivery != null) {
            brief.setRecipientMasked(maskName(delivery.getRecipient()));
            brief.setPhoneMasked(maskPhone(delivery.getPhone()));
            brief.setAddressMasked(maskAddress(delivery.getAddress()));
            brief.setTrackingNumber(delivery.getTrackingNumber());
            brief.setStatus(delivery.getStatus());
        } else {
            brief.setStatus("READY"); // 배송 행이 아직 없으면 준비중 가정(정책에 맞게 조정 가능)
        }
        res.setDelivery(brief);

        return res;
    }

    /* ===========================
     *  Mapping helpers
     * =========================== */

    private GuestOrderSummaryDto toSummaryDto(OrderSummaryRow r) {
        GuestOrderSummaryDto dto = new GuestOrderSummaryDto();
        dto.setOrderUid(r.getOrderUid());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setStatus(r.getStatus());
        dto.setTotalPrice(r.getTotalPrice());

        // itemSummary는 쿼리에서 미리 만들어 오도록 할 예정 (예: "맥북 외 1개")
        dto.setItemSummary(r.getItemSummary());

        dto.setBuyerNameMasked(maskName(r.getBuyerName()));
        dto.setBuyerPhoneMasked(maskPhone(r.getBuyerPhone()));
        dto.setHasDelivery(r.isHasDelivery());
        return dto;
    }

    private GuestOrderItemDto toItemDto(OrderItemRow r) {
        GuestOrderItemDto dto = new GuestOrderItemDto();
        dto.setProductId(r.getProductId());
        dto.setProductOptionId(r.getProductOptionId());
        dto.setProductName(r.getProductName());
        dto.setQuantity(r.getQuantity());
        dto.setPrice(r.getUnitPrice());
        return dto;
    }

    /* ===========================
     *  Utils (지역 유틸: 파일 분리는 원하면 나중에)
     * =========================== */

    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static void validateRequired(String v, String field) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("필수 입력 누락: " + field);
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int calcTotalPages(long total, int size) {
        if (size <= 0) return 0;
        return (int) ((total + size - 1) / size);
    }

    /** 숫자만 남기는 전화번호 정규화 */
    private static String normalizePhone(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D+", "");
        return digits;
    }

    /** 이름 마스킹: 홍길동 → 홍*동 (2글자 이하일 때는 마지막만 *) */
    private static String maskName(String name) {
        if (name == null || name.isBlank()) return "";
        String n = name.trim();
        if (n.length() <= 1) return "*";
        if (n.length() == 2) return n.charAt(0) + "*";
        return n.charAt(0) + "*" + n.substring(n.length() - 1);
    }

    /** 전화번호 마스킹: 01012345678 → 010****5678 (길이 짧으면 가운데 마스킹) */
    private static String maskPhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("\\D+", "");
        if (digits.length() < 7) return "****";
        String head = digits.substring(0, 3);
        String tail = digits.substring(digits.length() - 4);
        return head + "****" + tail;
    }

    /** 주소 마스킹: 마지막 3~5자 정도 치환 (단순 정책) */
    private static String maskAddress(String address) {
        if (address == null || address.isBlank()) return "";
        String a = address.trim();
        if (a.length() <= 5) return "***";
        return a.substring(0, a.length() - 3) + "***";
    }
}
