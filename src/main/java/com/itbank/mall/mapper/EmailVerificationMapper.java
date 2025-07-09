package com.itbank.mall.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.EmailVerification;

@Mapper
public interface EmailVerificationMapper {
    void insertToken(EmailVerification verification);
    EmailVerification findValidToken(@Param("email") String email,
                                     @Param("token") String token,
                                     @Param("now") LocalDateTime now);
    void markAsUsed(@Param("id") Long id);
}
