package com.itbank.mall.mapper.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMemberRoleMapper {

    Boolean existsById(@Param("memberId") Long memberId);

    String findRoleById(@Param("memberId") Long memberId);

    int countAdmins();

    int updateRole(@Param("memberId") Long memberId, @Param("role") String role);
}
