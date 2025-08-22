package com.itbank.mall.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.dto.track.MemberOrderDetailResponse;
import com.itbank.mall.dto.track.MemberOrderSearchRequest;
import com.itbank.mall.dto.track.MemberOrderSearchResponse;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.member.MemberOrderQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member/orders")
@RequiredArgsConstructor
public class MemberOrderTrackingController {

    private final MemberOrderQueryService memberOrderQueryService;

    // GET /api/member/orders?page=0&size=10&from=2025-06-01&to=2025-08-21&status=ALL
    @GetMapping
    public ResponseEntity<ApiResponse<MemberOrderSearchResponse>> list(@ModelAttribute MemberOrderSearchRequest req) {
        Long memberId = currentMemberId();
        var result = memberOrderQueryService.findMyOrders(memberId, req);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // GET /api/member/orders/{orderUid}
    @GetMapping("/{orderUid}")
    public ResponseEntity<ApiResponse<MemberOrderDetailResponse>> detail(@PathVariable String orderUid) {
        Long memberId = currentMemberId();
        var result = memberOrderQueryService.findMyOrderDetail(memberId, orderUid);
        if (result == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("주문을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    private Long currentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new RuntimeException("UNAUTHORIZED");
        Object principal = auth.getPrincipal();

        // 커스텀 Principal에 getMemberId()가 있다고 가정
        try {
            var m = principal.getClass().getMethod("getMemberId");
            Object val = m.invoke(principal);
            if (val instanceof Number n) return n.longValue();
        } catch (Exception ignore) {}

        throw new RuntimeException("memberId를 Principal에서 추출할 수 없습니다. Security 설정을 확인하세요.");
    }
}
