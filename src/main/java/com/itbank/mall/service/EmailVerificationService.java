package com.itbank.mall.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.itbank.mall.entity.EmailVerification;
import com.itbank.mall.mapper.EmailVerificationMapper;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationMapper emailVerificationMapper;
    private final MailService mailService;

    @Async
    public void sendVerificationCode(String email) {
        String code = mailService.generateAndSendVerificationCode(email);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        
        EmailVerification entity = new EmailVerification();
        entity.setEmail(email);
        entity.setToken(code);
        entity.setExpiredAt(expiredAt);
        entity.setIsUsed(false);
        System.out.println(">>> 이메일 전송 시도: " + email + " / 코드: " + code);

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
