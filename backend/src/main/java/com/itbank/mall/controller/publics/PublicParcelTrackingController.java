package com.itbank.mall.controller.publics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.orders.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
public class PublicParcelTrackingController  {

    private final DeliveryService deliveryService;

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<ApiResponse<TrackingLookupResponse>> find(@PathVariable String trackingNumber) {
        DeliveryEntity d = deliveryService.findByTrackingNumber(trackingNumber);
        if (d == null) {
            return ResponseEntity.ok(ApiResponse.ok(new TrackingLookupResponse(
                trackingNumber, "NOT_FOUND", "해당 송장을 찾을 수 없습니다."
            )));
        }
        String status = d.getStatus();
        String message = switch (status) {
            case "READY"     -> "상품 준비중입니다.";
            case "SHIPPING"  -> "배송 중입니다.";
            case "DELIVERED" -> "배송이 완료되었습니다.";
            case "RETURNED"  -> "반송 처리되었습니다.";
            case "CANCELLED" -> "배송이 취소되었습니다.";
            default          -> "상태 확인 중입니다.";
        };
        return ResponseEntity.ok(ApiResponse.ok(new TrackingLookupResponse(trackingNumber, status, message)));
    }

    @Data
    public static class TrackingLookupResponse {
        private final String trackingNumber;
        private final String status;
        private final String message;
    }
}
