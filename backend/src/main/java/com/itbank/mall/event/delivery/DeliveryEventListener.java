package com.itbank.mall.event.delivery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.itbank.mall.mapper.delivery.DeliveryNotifyMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

    private final DeliveryNotifyMapper notifyMapper;
    private final com.itbank.mall.service.notify.EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(DeliveryStatusChangedEvent event) {
        DeliveryNotifyInfo info = notifyMapper.selectNotifyInfoByOrderId(event.getOrderId());
        if (info == null || info.getMemberEmail() == null) return;

        String status = event.getNewStatus();   // ← 반드시 newStatus 사용
        String subject;
        String body;

        switch (status) {
            case "SHIPPING":
                subject = "[IBMall] 주문 #" + event.getOrderId() + " Shipping";
                body =
                    info.getMemberNickname() + "님,\n\n" +
                    "주문 #" + event.getOrderId() + "의 배송이 시작되었습니다.\n" +
                    "운송장 번호: " + event.getTrackingNumber() + "\n\n" +
                    "IBMall 드림";
                break;
            case "DELIVERED":
                subject = "[IBMall] 주문 #" + event.getOrderId() + " Delivered";
                body =
                    info.getMemberNickname() + "님,\n\n" +
                    "주문 #" + event.getOrderId() + "의 배송이 완료되었습니다.\n" +
                    "이용해 주셔서 감사합니다.\n\n" +
                    "IBMall 드림";
                break;
            default:
                return;
        }

        emailService.send(info.getMemberEmail(), subject, body);
    }
}
