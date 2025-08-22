package com.itbank.mall.controller.publics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.dto.track.GuestOrderDetailResponse;
import com.itbank.mall.dto.track.GuestOrderSearchRequest;
import com.itbank.mall.dto.track.GuestOrderSearchResponse;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.track.TrackingQueryService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/track/guest")
@RequiredArgsConstructor
public class GuestOrderTrackingController {

    private final TrackingQueryService trackingQueryService;

    /**
     * 비회원 주문 목록 검색 (이름+전화번호, 기간/페이징)
     * POST /api/track/guest/orders/search
     */
    @PostMapping("/orders/search")
    public ResponseEntity<ApiResponse<GuestOrderSearchResponse>> searchGuestOrders(
            @RequestBody GuestOrderSearchRequest req) {

        var result = trackingQueryService.findGuestOrders(req);
        // 빈 결과도 200으로 일관 응답 (정보 노출 방지)
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * 비회원 주문 상세 조회 (소유 검증: 이름+전화번호)
     * POST /api/track/guest/orders/{orderUid}
     */
    @PostMapping("/orders/{orderUid}")
    public ResponseEntity<ApiResponse<?>> getGuestOrderDetail(
            @PathVariable String orderUid,
            @RequestBody OwnerVerifyRequest owner) {

        var detail = trackingQueryService.findGuestOrderDetail(orderUid, owner.getName(), owner.getPhone());
        if (detail == null) {
            // 소유 불일치 또는 주문 없음 → 구체 정보 노출 없이 404 또는 200 빈결과 정책 중 택1
            // 여기선 404로 처리
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("주문을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    /**
     * 상세 조회용 간단 요청 바디 (이름+전화번호)
     * 외부 DTO 추가 생성 없이 컨트롤러 내부 클래스로 사용
     */
    @Data
    public static class OwnerVerifyRequest {
        private String name;
        private String phone; // 하이픈 포함 가능 (서비스에서 정규화)
    }
}
