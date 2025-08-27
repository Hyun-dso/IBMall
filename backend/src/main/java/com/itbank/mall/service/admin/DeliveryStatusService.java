package com.itbank.mall.service.admin;

import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.event.delivery.DeliveryStatusChangedEvent;
import com.itbank.mall.mapper.admin.DeliveryStatusMapper;
import com.itbank.mall.service.orders.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryStatusService {

    private final DeliveryStatusMapper deliveryStatusMapper;
    private final DeliveryService deliveryService;
    private final ApplicationEventPublisher events;

    @Transactional
    public boolean updateStatus(Long orderId, String status) {
        DeliveryEntity current = deliveryService.findByOrderId(orderId);

        // 동일 상태인데 SHIPPING인데 운송장이 없으면 새로 생성
        if (current != null && status.equals(current.getStatus())) {
            if ("SHIPPING".equals(status) && (current.getTrackingNumber() == null || current.getTrackingNumber().isBlank())) {
                String tn = deliveryService.generateAndAssignTrackingNumber(orderId);
                deliveryStatusMapper.updateOrderStatus(orderId, status, tn);
                events.publishEvent(new DeliveryStatusChangedEvent(orderId, status, tn));
            }
            return true;
        }

        String trackingNumber = null;
        if ("SHIPPING".equals(status)) {
            trackingNumber = deliveryService.generateAndAssignTrackingNumber(orderId);
        }

        int updated = deliveryStatusMapper.updateOrderStatus(orderId, status, trackingNumber);
        if (updated > 0) {
            events.publishEvent(new DeliveryStatusChangedEvent(orderId, status, trackingNumber));
            return true;
        }
        return false;
    }

}
