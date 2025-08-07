package com.itbank.mall.service.orders;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.orders.OrderDetailDto;
import com.itbank.mall.mapper.orders.OrderDetailMapper;

import lombok.RequiredArgsConstructor;

/**
 * 주문 상세 조회 전용 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class OrderDetailService {

    // 주문 상세 Mapper 주입
    private final OrderDetailMapper orderDetailMapper;

    /**
     * 특정 주문 ID에 대한 상세 정보 조회
     * @param orderId 주문번호
     * @return 주문에 포함된 상품들 + 배송 정보
     */
    public OrderDetailDto getOrderDetailById(Long orderId) {
        return orderDetailMapper.findOrderDetailById(orderId);
    }

}
