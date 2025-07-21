package com.itbank.mall.service;

import com.itbank.mall.dto.WishlistDto;
import com.itbank.mall.entity.WishlistEntity;
import com.itbank.mall.mapper.WishlistMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistMapper wishlistMapper;

    public WishlistService(WishlistMapper wishlistMapper) {
        this.wishlistMapper = wishlistMapper;
    }

    // 찜 목록 조회
    public List<WishlistDto> getWishlistByMemberId(Long memberId) {
        return wishlistMapper.selectWishlistByMemberId(memberId);
    }

    // 찜 추가
    public int addWishlist(WishlistEntity wishlist) {
        return wishlistMapper.insertWishlist(wishlist);
    }

    // 찜 삭제
    public int removeWishlist(WishlistEntity wishlist) {
        return wishlistMapper.deleteWishlist(wishlist);
    }
}
