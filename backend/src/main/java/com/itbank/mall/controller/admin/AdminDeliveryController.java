package com.itbank.mall.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.orders.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/admin/deliveries")
@RequiredArgsConstructor
public class AdminDeliveryController {

    private final DeliveryService deliveryService;

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/tracking/generate")
    public ResponseEntity<ApiResponse<GenerateTrackingResponse>> generateTracking(@PathVariable Long orderId) {
        String tracking = deliveryService.generateAndAssignTrackingNumber(orderId);
        var body = new GenerateTrackingResponse(orderId, tracking, "READY");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(body));
    }

    @Data
    public static class GenerateTrackingResponse {
        private final Long orderId;
        private final String trackingNumber;
        private final String status; // 현재 정책: READY 유지
    }
}
