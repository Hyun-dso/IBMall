package com.itbank.mall.service.mileage;

import com.itbank.mall.mapper.mileage.MileageMapper;
import com.itbank.mall.mapper.payment.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements MileageService {

    private final MileageMapper mileageMapper;
    private final PaymentMapper paymentMapper;

    @Override
    public void saveMileage(Long memberId, Long paymentId, int amount, String description) {
        // 1. 마일리지 로그 저장
        mileageMapper.insertMileageLog(memberId, "SAVE", amount, description, LocalDateTime.now());

        // 2. 누적 마일리지 업데이트 (없으면 insert, 있으면 update)
        boolean exists = mileageMapper.existsMileage(memberId);

        if (exists) {
            mileageMapper.addMileage(memberId, amount);
        } else {
            mileageMapper.insertInitialMileage(memberId, amount);
        }
    }
}
