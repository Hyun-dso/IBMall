package com.itbank.mall.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.itbank.mall.entity.EmailVerification;
import com.itbank.mall.mapper.EmailVerificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final EmailVerificationMapper emailVerificationMapper;
    private final MemberService memberService;

    public void sendVerificationCode(String email) {
        String code = generateCode();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        // DB 저장
        EmailVerification entity = new EmailVerification();
        entity.setEmail(email);
        entity.setToken(code);
        entity.setExpiredAt(expiredAt);
        entity.setIsUsed(false);

        emailVerificationMapper.insertToken(entity);

        // 메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[IBMall] 이메일 인증코드");
        message.setText("인증코드: " + code + "\n\n유효시간: 15분");

        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        EmailVerification found = emailVerificationMapper.findValidToken(email, code, LocalDateTime.now());

        if (found == null || Boolean.TRUE.equals(found.getIsUsed())) {
            return false;
        }

        emailVerificationMapper.markAsUsed(found.getId());
        
        memberService.verifyMember(email);
        
        return true;
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
