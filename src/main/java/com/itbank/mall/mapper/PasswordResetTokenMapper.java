package com.itbank.mall.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.PasswordResetToken;

public interface PasswordResetTokenMapper {
    void insertToken(PasswordResetToken token);
    
    PasswordResetToken findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    void markAsUsed(@Param("id") int id);
}
