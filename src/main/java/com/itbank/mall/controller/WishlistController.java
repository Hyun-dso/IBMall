package com.itbank.mall.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.WishlistDto;
import com.itbank.mall.entity.WishlistEntity;
import com.itbank.mall.mapper.WishlistMapper;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistMapper wishlistMapper;

    public WishlistController(WishlistMapper wishlistMapper) {
        this.wishlistMapper = wishlistMapper;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<WishlistDto>> getWishlist(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(wishlistMapper.selectWishlistByMemberId(memberId));
    }

    @PostMapping
    public ResponseEntity<String> addWishlist(@RequestBody WishlistEntity wishlist) {
        int row = wishlistMapper.insertWishlist(wishlist);
        return row > 0 ? ResponseEntity.ok("찜 추가 성공") : ResponseEntity.badRequest().body("찜 실패 or 이미 존재");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteWishlist(@RequestBody WishlistEntity wishlist) {
        int row = wishlistMapper.deleteWishlist(wishlist);
        return row > 0 ? ResponseEntity.ok("찜 삭제 성공") : ResponseEntity.badRequest().body("찜 삭제 실패");
    }
}
