package com.itbank.mall.service.common;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.entity.email.EmailVerification;
import com.itbank.mall.exception.EmailAlreadyExistsException;
import com.itbank.mall.exception.EmailCooldownException;
import com.itbank.mall.mapper.common.EmailVerificationMapper;
import com.itbank.mall.mapper.member.MemberMapper;
import com.itbank.mall.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
	
	private static final int COOLDOWN_SECONDS = 60;

    private final EmailVerificationMapper emailVerificationMapper;
    private final MemberMapper memberMapper;
    private final MailService mailService;

    @Async
    public void sendVerificationCode(String email) { // 기존 메서드는 내부 재사용용으로 둠
        doSend(email);
    }
    
    // ✅ 회원가입용: 중복검사 + 쿨다운 포함한 진입 메서드
    public void sendVerificationCodeForSignup(String rawEmail) {
        String email = normalize(rawEmail);
        validateEmailFormat(email);

        // 1) 이메일 중복 검사
        if (memberMapper.countByEmail(email) > 0) {
            throw new EmailAlreadyExistsException("이미 가입된 이메일입니다.");
        }

        // 2) 재전송 쿨다운(60초) 검사
        LocalDateTime last = emailVerificationMapper.findLastCreatedAtByEmail(email);
        if (last != null && last.isAfter(LocalDateTime.now().minusSeconds(COOLDOWN_SECONDS))) {
            throw new EmailCooldownException("재전송은 60초 후에 다시 시도해 주세요.");
        }

        // 3) 실제 발송 처리
        doSend(email);
    }
    
    private void doSend(String email) {
        emailVerificationMapper.invalidatePreviousTokens(email);

        String code = mailService.generateAndSendVerificationCode(email);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        EmailVerification entity = new EmailVerification();
        entity.setEmail(email);
        entity.setToken(code);
        entity.setExpiredAt(expiredAt);
        entity.setIsUsed(false);

        emailVerificationMapper.insertToken(entity);
        log.info("[Email] code sent to={}, expiredAt={}", email, expiredAt);
    }

    @Transactional
    public boolean verifyCode(String email, String code) {
        EmailVerification found = emailVerificationMapper.findValidToken(
            normalize(email), code, LocalDateTime.now());

        if (found == null || Boolean.TRUE.equals(found.getIsUsed())) {
            return false;
        }

        emailVerificationMapper.markAsUsed(found.getId());
        emailVerificationMapper.updateMemberVerified(found.getEmail());
        return true;
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void validateEmailFormat(String email) {
        if (email == null || email.isBlank()
            || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("유효한 이메일 형식이 아닙니다.");
        }
    }
}
