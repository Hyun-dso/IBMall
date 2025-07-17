package com.itbank.mall.mapper;

import com.itbank.mall.dto.CartDto;
import com.itbank.mall.entity.CartEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {

    // 장바구니 목록 조회
    List<CartDto> selectCartByMemberId(Long memberId);

    // 장바구니에 상품 추가
    int insertCart(CartEntity cart);

    // 장바구니 삭제
    int deleteCart(CartEntity cart);
}
