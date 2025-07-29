package com.itbank.mall.service.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.itbank.mall.mapper.admin.DeliveryStatusMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryStatusService {

    private final DeliveryStatusMapper deliveryStatusMapper;

    public boolean updateStatus(Long orderId, String status) {
        String trackingNumber = null;

        // 배송중일 때만 운송장 번호 생성
        if ("배송중".equals(status)) {
            trackingNumber = generateTrackingNumber();
        }

        int result = deliveryStatusMapper.updateOrderStatus(orderId, status, trackingNumber);
        return result > 0;
    }

    private String generateTrackingNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));  // 날짜+시간
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();  // UUID 일부
        return "IB" + datePart + uuidPart;
    }
}
