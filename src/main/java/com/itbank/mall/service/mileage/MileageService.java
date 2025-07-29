package com.itbank.mall.service.mileage;

public interface MileageService {

    // 마일리지 적립
    void saveMileage(Long memberId, Long paymentId, int amount, String description);
}
