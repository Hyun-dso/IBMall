package com.itbank.mall.service.admin;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.admin.grade.GradeBenefitUpdateRequestDto;
import com.itbank.mall.entity.grade.GradeBenefit;
import com.itbank.mall.mapper.admin.GradeBenefitMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeBenefitService {

    private final GradeBenefitMapper gradeBenefitMapper;

    public List<GradeBenefit> getAll() {
        return gradeBenefitMapper.findAll();
    }

    public GradeBenefit getByGrade(String gradeName) {
        return gradeBenefitMapper.findByGrade(gradeName);
    }

    public int update(GradeBenefit benefit) {
        return gradeBenefitMapper.updateBenefit(benefit);
    }
    
    // PathVar gradeName 사용 + 검증 + 대문자 정규화
    public void updateByGradeName(String gradeName, GradeBenefitUpdateRequestDto dto) {
        if (dto == null) throw new IllegalArgumentException("요청 본문이 필요합니다.");
        Integer discount = dto.getDiscountPercent();
        Integer mileage = dto.getMileageRate();

        if (discount == null || mileage == null)
            throw new IllegalArgumentException("discountPercent, mileageRate는 필수입니다.");
        if (discount < 0 || discount > 100 || mileage < 0 || mileage > 100)
            throw new IllegalArgumentException("discountPercent, mileageRate는 0~100 범위여야 합니다.");

        String norm = Objects.requireNonNull(gradeName, "gradeName").toUpperCase();

        GradeBenefit exists = gradeBenefitMapper.findByGrade(norm);
        if (exists == null) {
            // 운영정책: 존재하지 않으면 404로 매핑되도록 예외 메시지 명확히
            throw new IllegalArgumentException("등급(" + norm + ")의 혜택이 존재하지 않습니다.");
        }

        GradeBenefit toUpdate = new GradeBenefit();
        toUpdate.setGradeName(norm);
        toUpdate.setDiscountPercent(discount);
        toUpdate.setMileageRate(mileage);

        gradeBenefitMapper.updateBenefit(toUpdate);
    }
}
