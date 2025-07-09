package com.itbank.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.Member;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    Member findByEmail(String email);
    void verifyMemberByEmail(@Param("email") String email);
}