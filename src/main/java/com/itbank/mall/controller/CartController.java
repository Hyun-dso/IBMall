package com.itbank.mall.controller;

import com.itbank.mall.dto.CartDto;
import com.itbank.mall.entity.CartEntity;
import com.itbank.mall.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 장바구니 목록
    @GetMapping("/{memberId}")
    public ResponseEntity<List<CartDto>> getCart(@PathVariable("memberId") Long memberId) {
        List<CartDto> cartList = cartService.getCartList(memberId);
        return ResponseEntity.ok(cartList);
    }

    // 장바구니 추가
    @PostMapping
    public ResponseEntity<String> addCart(@RequestBody CartEntity cart) {
        int row = cartService.addCart(cart);
        return row > 0 ? ResponseEntity.ok("장바구니 추가 성공") : ResponseEntity.badRequest().body("추가 실패 (중복 or 오류)");
    }

    // 장바구니 삭제
    @DeleteMapping
    public ResponseEntity<String> deleteCart(@RequestBody CartEntity cart) {
        int row = cartService.removeCart(cart);
        return row > 0 ? ResponseEntity.ok("장바구니 삭제 성공") : ResponseEntity.badRequest().body("삭제 실패");
    }
}
