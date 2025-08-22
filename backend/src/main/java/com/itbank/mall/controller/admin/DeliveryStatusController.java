// src/main/java/com/itbank/mall/controller/admin/DeliveryStatusController.java
package com.itbank.mall.controller.admin;

import com.itbank.mall.dto.admin.delivery.DeliveryStatusDto;
import com.itbank.mall.service.admin.DeliveryStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class DeliveryStatusController {

    private final DeliveryStatusService deliveryStatusService;

    @PostMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
    		@PathVariable("orderId") Long orderId,
            @RequestBody DeliveryStatusDto dto) {

        boolean result = deliveryStatusService.updateStatus(orderId, dto.getStatus());

        return result
                ? ResponseEntity.ok("배송 상태 변경 성공")
                : ResponseEntity.badRequest().body("배송 상태 변경 실패");
    }
}
