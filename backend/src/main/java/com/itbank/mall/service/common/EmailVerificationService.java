package com.itbank.mall.service.common;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.itbank.mall.entity.email.EmailVerification;
import com.itbank.mall.mapper.common.EmailVerificationMapper;
import com.itbank.mall.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationMapper emailVerificationMapper;
    private final MailService mailService;

    @Async
    public void sendVerificationCode(String email) {
        // 1. 기존 토큰 무효화
        emailVerificationMapper.invalidatePreviousTokens(email);
        
        String code = mailService.generateAndSendVerificationCode(email);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);
        
        EmailVerification entity = new EmailVerification();
        entity.setEmail(email);
        entity.setToken(code);
        entity.setExpiredAt(expiredAt);
        entity.setIsUsed(false);
        log.info("📨 인증메일 발송 → email: {}, code: {}", email, code);

        emailVerificationMapper.insertToken(entity);
    }

    public boolean verifyCode(String email, String code) {
        EmailVerification found = emailVerificationMapper.findValidToken(email, code, LocalDateTime.now());

        if (found == null || Boolean.TRUE.equals(found.getIsUsed())) {
            return false;
        }

        emailVerificationMapper.markAsUsed(found.getId());

        // ✅ member 테이블에 verified = true 설정
        emailVerificationMapper.updateMemberVerified(email);

        return true;
    }
}
