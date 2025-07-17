package com.itbank.mall.service;

import com.itbank.mall.dto.CartDto;
import com.itbank.mall.entity.CartEntity;
import com.itbank.mall.mapper.CartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartMapper cartMapper;

    public CartService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    // 장바구니 조회
    public List<CartDto> getCartList(Long memberId) {
        return cartMapper.selectCartByMemberId(memberId);
    }

    // 장바구니 추가
    public int addCart(CartEntity cart) {
        return cartMapper.insertCart(cart);
    }

    // 장바구니 삭제
    public int removeCart(CartEntity cart) {
        return cartMapper.deleteCart(cart);
    }
}
