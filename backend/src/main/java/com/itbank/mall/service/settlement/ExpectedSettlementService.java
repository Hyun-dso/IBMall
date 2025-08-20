package com.itbank.mall.service.settlement;

import com.itbank.mall.dto.settlement.ExpectedSettlementDto;
import com.itbank.mall.mapper.settlement.ExpectedSettlementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpectedSettlementService {

    private final ExpectedSettlementMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    public int saveExpectedSettlements(LocalDateTime startDate, LocalDateTime endDate) {
        mapper.deleteByDateRange(startDate, endDate);
        return mapper.insertByDateRangeFromOrders(startDate, endDate);
    }

    public List<ExpectedSettlementDto> getExpectedList(LocalDateTime startDate, LocalDateTime endDate) {
        return mapper.selectByDateRange(startDate, endDate);
    }
}
