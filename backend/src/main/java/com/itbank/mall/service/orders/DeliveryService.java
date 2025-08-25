package com.itbank.mall.service.orders;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.mapper.orders.DeliveryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryMapper deliveryMapper;

    private static final SecureRandom RND = new SecureRandom();
    private static final char[] ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray(); // 가독성 좋은 세트
    private static String randomSuffix(int len) {
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) buf[i] = ALPHANUM[RND.nextInt(ALPHANUM.length)];
        return new String(buf);
    }

    /** 결제 직후 호출: READY + tracking=NULL (UPSERT) */
    @Transactional
    public void saveDelivery(DeliveryEntity e) {
        deliveryMapper.insertDelivery(e);
    }

    /** ✅ 관리자: 랜덤 송장 발급 & 할당 (상태는 그대로 유지) */
    @Transactional
    public String generateAndAssignTrackingNumber(Long orderId) {
        // 1) 멱등: 이미 송장이 있으면 그 값 그대로 반환
        var existing = deliveryMapper.findByOrderId(orderId);
        if (existing != null && existing.getTrackingNumber() != null && !existing.getTrackingNumber().isBlank()) {
            return existing.getTrackingNumber();
        }

        // 2) 폴백: 배송행이 없으면 생성(READY, tracking=NULL)
        if (existing == null) {
            DeliveryEntity e = new DeliveryEntity();
            e.setOrderId(orderId);
            // 스키마가 NOT NULL이라 플레이스홀더 사용(관리자 화면에서 수정 가능)
            e.setAddress1("-");
            e.setAddress2("-");
            e.setRecipient("-");
            e.setPhone("-");
            e.setStatus("READY");
            e.setTrackingNumber(null);
            deliveryMapper.insertDelivery(e); // UPSERT
        }

        // 3) 랜덤 송장 생성 + 경합/충돌 재시도
        final String ymd = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int attempts = 0;
        while (attempts++ < 6) {
            String candidate = "IB-" + ymd + "-" + randomSuffix(8);
            try {
                int updated = deliveryMapper.updateTrackingNumber(orderId, candidate); // NULL일 때만 세팅
                if (updated == 1) {
                    return candidate; // 성공
                }
                // 다른 트랜잭션이 먼저 채웠을 가능성 → 현재 값 반환
                var now = deliveryMapper.findByOrderId(orderId);
                if (now != null && now.getTrackingNumber() != null && !now.getTrackingNumber().isBlank()) {
                    return now.getTrackingNumber();
                }
                // 아직도 비었다면 다음 루프로 재시도
            } catch (org.springframework.dao.DataIntegrityViolationException dup) {
                // UNIQUE(tracking_number) 충돌 → 다른 candidate로 재시도
            }
        }
        throw new IllegalStateException("랜덤 송장 생성에 실패했습니다. 다시 시도해주세요.");
    }

    /** 조회 */
    @Transactional(readOnly = true)
    public DeliveryEntity findByTrackingNumber(String trackingNumber) {
        return deliveryMapper.findByTrackingNumber(trackingNumber);
    }

    @Transactional
    public void updateStatus(Long orderId, String status) {
        deliveryMapper.updateStatus(orderId, status);
    }
}
