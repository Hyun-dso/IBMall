package com.itbank.mall.controller.settlement;

import java.util.List;

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
import com.itbank.mall.service.settlement.SettlementDetailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/settlement")
@RequiredArgsConstructor
public class SettlementDetailController {

    private final SettlementDetailService settlementDetailService;

    // ✅ 1. 정산 저장
    @PostMapping
    public ResponseEntity<String> save(@RequestBody SettlementDetailEntity entity) {
        // 예상정산(type=expected)은 저장 불가
        if (!"actual".equals(entity.getType())) {
            return ResponseEntity.badRequest().body("실정산(type=actual)만 저장할 수 있습니다");
        }

        int result = settlementDetailService.saveSettlement(entity);
        return result > 0
                ? ResponseEntity.ok("정산 저장 완료")
                : ResponseEntity.badRequest().body("배송 완료 상태가 아닙니다");
    }


    // ✅ 2. 전체 정산 목록 조회
    @GetMapping
    public List<SettlementDetailDto> getAll() {
        return settlementDetailService.getAllSettlements();
    }

    // ✅ 3. 타입별 정산 조회 (actual / expected)
    @GetMapping("/type/{type}")
    public List<SettlementDetailDto> getByType(@PathVariable("type") String type) {
        return settlementDetailService.getSettlementsByType(type);
    }

    // ✅ 4. 날짜 범위 정산 조회
    @GetMapping("/range")
    public List<SettlementDetailDto> getByDateRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return settlementDetailService.getSettlementsByDateRange(start, end);
    }

    // ✅ 5. 단건 조회 (orderId 기준)
    @GetMapping("/order/{orderId}")
    public SettlementDetailDto getByOrderId(@PathVariable("orderId") Long orderId) {
        return settlementDetailService.getSettlementByOrderId(orderId);
    }
    
 // ✅ 6. 통합 검색 기능 (/search)
    @GetMapping("/search")
    public List<SettlementDetailDto> search(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "memberId", required = false) Long memberId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "status", required = false) String status
    ) {
        return settlementDetailService.search(type, start, end, memberId, productId, categoryId, status);
    }

    
}
