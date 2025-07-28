package com.itbank.mall.mapper.member;

import com.itbank.mall.dto.wishlist.WishlistDto;
import com.itbank.mall.entity.wishlist.WishlistEntity;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WishlistMapper {

    // 찜 목록 조회
    List<WishlistDto> selectWishlistByMemberId(Long memberId);

    // 찜 추가
    int insertWishlist(WishlistEntity wishlist);

    // 찜 삭제
    int deleteWishlist(WishlistEntity wishlist);
}
