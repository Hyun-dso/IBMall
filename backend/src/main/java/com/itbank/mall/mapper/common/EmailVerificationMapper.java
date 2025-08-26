package com.itbank.mall.mapper.common;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.email.EmailVerification;

@Mapper
public interface EmailVerificationMapper {
    void insertToken(EmailVerification verification);
    EmailVerification findValidToken(@Param("email") String email,
                                     @Param("token") String token,
                                     @Param("now") LocalDateTime now);
    
    void markAsUsed(@Param("id") Long id);
    
    void updateMemberVerified(@Param("email") String email);
    void invalidatePreviousTokens(@Param("email") String email);
    LocalDateTime findLastCreatedAtByEmail(@Param("email") String email);
}
