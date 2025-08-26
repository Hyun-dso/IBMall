package com.itbank.mall.service.orders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.itbank.mall.dto.orders.detail.OrderDetailResponse;
import com.itbank.mall.mapper.orders.OrderDetailQueryMapper;
import com.itbank.mall.mapper.row.OrderDetailRow;
import com.itbank.mall.mapper.row.OrderItemRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDetailQueryService {

    public enum MaskingPolicy { MEMBER, GUEST }

    private final OrderDetailQueryMapper queryMapper;

    /** 회원/비회원 공용 상세 조회 */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(String orderUid, MaskingPolicy policy) {
        // 1) 헤더 단건
        OrderDetailRow header = queryMapper.selectOrderHeaderByUid(orderUid);
        if (header == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
        }

        // 2) 아이템 리스트
        List<OrderItemRow> rows = queryMapper.selectOrderItemsByOrderId(header.getOrderId());
        if (rows == null) rows = List.of();
        int itemsCount = rows.size();

        // 3) 금액 집계/검증
        int sum = 0;
        for (OrderItemRow r : rows) {
            // unitPrice는 단가(= order_items.price)
            sum += r.getUnitPrice() * r.getQuantity();
        }
        int ordersTotal = header.getTotalPrice();
        if (sum != ordersTotal) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "주문 합계가 일치하지 않습니다.");
        }
        Integer paid = header.getPaidAmount(); // null일 수 있음(LEFT JOIN)
        if (paid != null && paid.intValue() != ordersTotal) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "결제 금액이 주문 총액과 일치하지 않습니다.");
        }

        // 4) 헤더 텍스트 생성
        String headerText = buildHeaderText(rows, ordersTotal);

        // 5) DTO 조립
        OrderDetailResponse res = new OrderDetailResponse();

        // summary
        OrderDetailResponse.Summary summary = new OrderDetailResponse.Summary();
        summary.setOrderUid(header.getOrderUid());
        summary.setCreatedAt(header.getCreatedAt());
        summary.setStatus(header.getStatus());
        summary.setItemsCount(itemsCount);
        summary.setPaidAmount(ordersTotal);
        summary.setHeaderText(headerText);
        res.setSummary(summary);

        // buyer (회원: 원문, 비회원: 마스킹)
        OrderDetailResponse.Buyer buyer = new OrderDetailResponse.Buyer();
        buyer.setName(policy == MaskingPolicy.GUEST ? maskName(header.getBuyerName()) : nvl(header.getBuyerName()));
        buyer.setPhone(policy == MaskingPolicy.GUEST ? maskPhone(header.getBuyerPhone()) : nvl(header.getBuyerPhone()));
        res.setBuyer(buyer);

        // recipient (회원: 원문, 비회원: 마스킹)
        OrderDetailResponse.Recipient recipient = new OrderDetailResponse.Recipient();
        recipient.setName(policy == MaskingPolicy.GUEST ? maskName(header.getRecipient()) : nvl(header.getRecipient()));
        recipient.setPhone(policy == MaskingPolicy.GUEST ? maskPhone(header.getDeliveryPhone()) : nvl(header.getDeliveryPhone()));
        recipient.setAddress1(policy == MaskingPolicy.GUEST ? maskAddress1(header.getAddress1()) : nvl(header.getAddress1()));
        recipient.setAddress2(policy == MaskingPolicy.GUEST ? maskAddress2(header.getAddress2()) : nvl(header.getAddress2()));
        recipient.setTrackingNumber(nvl(header.getTrackingNumber()));
        recipient.setDeliveryStatus(nvl(header.getDeliveryStatus(), "READY"));
        res.setRecipient(recipient);

        // items
        List<OrderDetailResponse.OrderItemLine> itemLines = new ArrayList<>();
        for (OrderItemRow r : rows) {
            OrderDetailResponse.OrderItemLine line = new OrderDetailResponse.OrderItemLine();
            line.setProductId(r.getProductId());
            line.setProductOptionId(r.getProductOptionId());
            line.setProductName(r.getProductName());
            line.setOptionName(r.getOptionName());
            line.setQuantity(r.getQuantity());
            line.setUnitPrice(r.getUnitPrice());
            line.setLineTotal(r.getUnitPrice() * r.getQuantity());
            itemLines.add(line);
        }
        res.setItems(itemLines);

        return res;
    }

    /* ===================== helpers ===================== */

    private static String buildHeaderText(List<OrderItemRow> rows, int paidAmount) {
        if (rows == null || rows.isEmpty()) {
            return "주문내역 · 총 " + formatWon(paidAmount);
        }
        OrderItemRow first = rows.get(0);
        String firstName = nvl(first.getProductName(), "상품");
        String opt = (first.getOptionName() != null && !first.getOptionName().isBlank())
                ? " (" + first.getOptionName() + ")"
                : "";
        String qty = " x" + first.getQuantity();
        int extra = Math.max(0, rows.size() - 1);
        String tail = (extra > 0) ? " 외 " + extra + "개" : "";
        return firstName + opt + qty + tail + " · 총 " + formatWon(paidAmount);
    }

    private static String formatWon(int v) {
        // 간단 포맷(콤마): 4500 -> 4,500원
        return String.format("%,d원", v);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
    private static String nvl(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }

    // ===== 마스킹 유틸(TrackingQueryService와 동일 정책) =====
    private static String maskName(String name) {
        if (name == null || name.isBlank()) return "";
        String n = name.trim();
        if (n.length() <= 1) return "*";
        if (n.length() == 2) return n.charAt(0) + "*";
        return n.charAt(0) + "*" + n.substring(n.length() - 1);
    }

    private static String maskPhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("\\D+", "");
        if (digits.length() < 7) return "****";
        String head = digits.substring(0, 3);
        String tail = digits.substring(digits.length() - 4);
        return head + "****" + tail;
    }

    private static String maskAddress1(String address1) {
        if (address1 == null || address1.isBlank()) return "";
        String a = address1.trim();
        int keep = Math.min(a.length(), 8);
        return a.substring(0, keep) + " ***";
    }

    private static String maskAddress2(String address2) {
        if (address2 == null || address2.isBlank()) return "";
        String a = address2.trim();
        int keep = Math.min(a.length(), 2);
        return a.substring(0, keep) + "***";
    }
}
