package com.itbank.mall.controller.settlement;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.settlement.SettlementDetailDto;
import com.itbank.mall.entity.settlement.SettlementDetailEntity;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.settlement.SaveResult;
import com.itbank.mall.service.settlement.SettlementDetailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/settlement")
@RequiredArgsConstructor
public class SettlementDetailController {

    private final SettlementDetailService settlementDetailService;

    // ✅ 1. 정산 저장 (ApiResponse + 에러 매핑)
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> save(@RequestBody SettlementDetailEntity entity) {
        try {
            SaveResult res = settlementDetailService.saveSettlement(entity);
            switch (res.getStatus()) {
                case SUCCESS:
                    // 201 Created
                    return ResponseEntity.status(201).body(ApiResponse.ok(null, "정산 저장 완료"));

                case INVALID_TYPE:
                    // 400 Bad Request
                    return ResponseEntity.badRequest().body(ApiResponse.fail("실정산(type=actual)만 저장할 수 있습니다"));

                case PAYMENT_NOT_PAID:
                case DELIVERY_NOT_DELIVERED:
                case AMOUNT_MISMATCH:
                    // 422 Unprocessable Entity (사전조건/검증 실패)
                    return ResponseEntity.status(422).body(ApiResponse.fail(res.getReason()));

                case INSERT_FAILED:
                default:
                    // 500
                    return ResponseEntity.status(500).body(ApiResponse.fail("서버 내부 오류"));
            }
        } catch (DuplicateKeyException e) {
            // (다음 단계에서 UNIQUE 키 도입 시) 멱등/중복 충돌
            return ResponseEntity.status(409).body(ApiResponse.fail("이미 처리된 정산입니다(중복)"));
        }
    }

 // ✅ 2. 전체 정산 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettlementDetailDto>>> getAll() {
        List<SettlementDetailDto> list = settlementDetailService.getAllSettlements();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    // ✅ 3. 타입별 정산 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<SettlementDetailDto>>> getByType(@PathVariable("type") String type) {
        List<SettlementDetailDto> list = settlementDetailService.getSettlementsByType(type);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    // ✅ 4. 날짜 범위 정산 조회
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<SettlementDetailDto>>> getByDateRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        List<SettlementDetailDto> list = settlementDetailService.getSettlementsByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    // ✅ 5. 단건 조회
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<SettlementDetailDto>> getByOrderId(@PathVariable("orderId") Long orderId) {
        SettlementDetailDto dto = settlementDetailService.getSettlementByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    // ✅ 6. 통합 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SettlementDetailDto>>> search(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "memberId", required = false) Long memberId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "status", required = false) String status
    ) {
        List<SettlementDetailDto> list = settlementDetailService.search(type, start, end, memberId, productId, categoryId, status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    
}
