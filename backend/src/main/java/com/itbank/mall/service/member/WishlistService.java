package com.itbank.mall.service.member;

import com.itbank.mall.dto.wishlist.WishlistDto;
import com.itbank.mall.entity.wishlist.WishlistEntity;
import com.itbank.mall.mapper.member.WishlistMapper;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistMapper wishlistMapper;

    public WishlistService(WishlistMapper wishlistMapper) {
        this.wishlistMapper = wishlistMapper;
    }

    // 찜 목록 조회
    public List<WishlistDto> getWishlist(Long memberId) {
        return wishlistMapper.selectWishlistByMemberId(memberId);
    }

    // 찜 추가
    public boolean addWishlist(WishlistEntity wishlist) {
        return wishlistMapper.insertWishlist(wishlist) > 0;
    }

    // 찜 삭제
    public boolean deleteWishlist(WishlistEntity wishlist) {
        return wishlistMapper.deleteWishlist(wishlist) > 0;
    }
}
