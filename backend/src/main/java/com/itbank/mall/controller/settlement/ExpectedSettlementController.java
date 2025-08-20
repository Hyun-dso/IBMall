package com.itbank.mall.controller.settlement;

import com.itbank.mall.dto.settlement.ExpectedSettlementDto;
import com.itbank.mall.service.settlement.ExpectedSettlementService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/settlement/expected")
@RequiredArgsConstructor
public class ExpectedSettlementController {

    private final ExpectedSettlementService expectedSettlementService;

    @Getter @Setter
    public static class DateRangeReq {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    // ✅ 예상정산 저장
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveExpectedSettlement(@RequestBody DateRangeReq req) {
        int inserted = expectedSettlementService.saveExpectedSettlements(req.getStartDate(), req.getEndDate());
        return ResponseEntity.ok("예상정산 저장 완료: " + inserted + "건");
    }

    // ✅ 예상정산 리스트 조회
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<ExpectedSettlementDto> getExpectedList(@RequestBody DateRangeReq req) {
        return expectedSettlementService.getExpectedList(req.getStartDate(), req.getEndDate());
    }
}
