package com.itbank.mall.mapper.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.member.Member;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    Member findByEmail(String email);
    int countByNickname(String nickname);
    void verifyMemberByEmail(@Param("email") String email);
    void updatePassword(@Param("email") String email, @Param("password") String encodedPassword);
    int countByEmail(String email);
    int insertByGoogle(Member member);
    boolean existsByNickname(String nickname);
    Member findById(Long id);
    void updateMember(Member member);
    boolean existsByPhone(String phone);
    String findEmailById(@Param("memberId") Long memberId);
}