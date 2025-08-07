package com.itbank.mall.mapper.mileage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface MileageMapper {

    // 마일리지 로그 기록
    int insertMileageLog(@Param("memberId") Long memberId,
                         @Param("type") String type,        // "SAVE", "USE" 등
                         @Param("amount") int amount,
                         @Param("description") String description,
                         @Param("createdAt") LocalDateTime createdAt);

    // 누적 마일리지 존재 여부
    boolean existsMileage(@Param("memberId") Long memberId);

    // 누적 마일리지 업데이트
    int addMileage(@Param("memberId") Long memberId, @Param("amount") int amount);

    // 마일리지 최초 insert
    int insertInitialMileage(@Param("memberId") Long memberId, @Param("amount") int amount);
}
