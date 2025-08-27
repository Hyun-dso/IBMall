package com.itbank.mall.controller.orders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.dto.orders.detail.OrderDetailResponse;
import com.itbank.mall.mapper.orders.OrderMapper;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.orders.OrderDetailQueryService;
import com.itbank.mall.service.orders.OrderDetailQueryService.MaskingPolicy;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 회원/비회원 공용 주문 상세 조회 컨트롤러
 * - 경로: /api/orders/{orderUid}/detail
 * - GET  : 회원용(JWT) → 원문 노출 (마스킹 없음)
 * - POST : 비회원용(이름+전화번호) → 마스킹 적용
 */
@RestController
@RequestMapping("/api/orders/{orderUid}/detail")
@RequiredArgsConstructor
public class OrderDetailPublicController {

    private final OrderMapper orderMapper;                    // 소유 검증에 사용 (이미 있는 쿼리 재사용)
    private final OrderDetailQueryService orderDetailQueryService; // 공용 상세 조립(검증/헤더/마스킹)

    /**
     * 회원 주문 상세 (JWT 필요)
     * - 소유 검증: memberId + orderUid
     * - 마스킹: 없음(MEMBER)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getForMember(
            @PathVariable String orderUid,
            @RequestAttribute(name = "memberId", required = false) Long memberId // JWT 필터에서 request attr로 주입된다고 가정
    ) {
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("인증이 필요합니다."));
        }

        // 소유 검증 (존재 여부만 확인)
        var owned = orderMapper.selectMemberOrderDetail(memberId, orderUid);
        if (owned == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("주문을 찾을 수 없습니다."));
        }

        // 공용 상세 조립 (회원 → 마스킹 없음)
        OrderDetailResponse detail = orderDetailQueryService.getOrderDetail(orderUid, MaskingPolicy.MEMBER);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    /**
     * 비회원 주문 상세 (이름+전화번호 본인확인)
     * - 요청 바디: { "name": "...", "phone": "..." }
     * - 소유 검증: 이름+전화+orderUid
     * - 마스킹: 적용(GUEST)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getForGuest(
            @PathVariable String orderUid,
            @RequestBody OwnerVerifyRequest owner
    ) {
        // 소유 검증 (존재 여부만 확인)
        var owned = orderMapper.selectGuestOrderDetail(orderUid, owner.getName(), normalizePhone(owner.getPhone()));
        if (owned == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("주문을 찾을 수 없습니다."));
        }

        // 공용 상세 조립 (비회원 → 마스킹)
        OrderDetailResponse detail = orderDetailQueryService.getOrderDetail(orderUid, MaskingPolicy.GUEST);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    /* ===== Helpers ===== */

    /** 숫자만 남기는 전화번호 정규화 (컨트롤러 단 경량 버전) */
    private static String normalizePhone(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("\\D+", "");
    }

    @Data
    public static class OwnerVerifyRequest {
        private String name;
        private String phone; // 하이픈 포함 가능
    }
}
