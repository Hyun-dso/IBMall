package com.itbank.mall.controller.settlement;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.settlement.ExpectedSettlementDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.settlement.ExpectedSettlementService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    // ✅ 예상정산 저장 (결제 PAID + 결제시각 기준)
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Integer>>> saveExpectedSettlement(@RequestBody DateRangeReq req) {
        int inserted = expectedSettlementService.saveExpectedSettlements(req.getStartDate(), req.getEndDate());
        Map<String, Integer> data = new HashMap<>();
        data.put("inserted", inserted);
        return ResponseEntity.status(201).body(ApiResponse.ok(data, "예상정산 저장 완료"));
    }

    // ✅ 예상정산 리스트 조회
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ExpectedSettlementDto>>> getExpectedList(@RequestBody DateRangeReq req) {
        List<ExpectedSettlementDto> list =
                expectedSettlementService.getExpectedList(req.getStartDate(), req.getEndDate());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
