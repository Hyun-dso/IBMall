package com.itbank.mall.controller.member;

import com.itbank.mall.dto.wishlist.WishlistDto;
import com.itbank.mall.entity.wishlist.WishlistEntity;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.member.WishlistService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // ✅ 찜 목록 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<List<WishlistDto>>> getWishlist(@PathVariable Long memberId) {
        List<WishlistDto> wishlist = wishlistService.getWishlist(memberId);
        return ResponseEntity.ok(ApiResponse.ok(wishlist));
    }

    // ✅ 찜 추가
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addWishlist(@RequestBody WishlistEntity wishlist) {
        boolean success = wishlistService.addWishlist(wishlist);

        if (success) {
            return ResponseEntity.ok(ApiResponse.ok("찜 추가 성공"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail("찜 실패 또는 이미 존재합니다."));
        }
    }

    // ✅ 찜 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteWishlist(@RequestBody WishlistEntity wishlist) {
        boolean success = wishlistService.deleteWishlist(wishlist);

        if (success) {
            return ResponseEntity.ok(ApiResponse.ok("찜 삭제 성공"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail("찜 삭제 실패"));
        }
    }
}
