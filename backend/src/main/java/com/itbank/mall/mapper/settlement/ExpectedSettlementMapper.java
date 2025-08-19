package com.itbank.mall.mapper.settlement;

import com.itbank.mall.dto.settlement.ExpectedSettlementDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExpectedSettlementMapper {

    int deleteByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    int insertByDateRangeFromOrders(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    List<ExpectedSettlementDto> selectByDateRange(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
}
