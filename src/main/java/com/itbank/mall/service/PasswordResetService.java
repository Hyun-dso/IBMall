package com.itbank.mall.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.itbank.mall.entity.Member;
import com.itbank.mall.entity.PasswordResetToken;
import com.itbank.mall.mapper.MemberMapper;
import com.itbank.mall.mapper.PasswordResetTokenMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenMapper tokenMapper;
    private final MemberMapper memberMapper;  // ✅ 추가
    private final MailService mailService;
    private final BCryptPasswordEncoder passwordEncoder;  // ✅ 추가

    @Async
    public void sendResetLink(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken reset = new PasswordResetToken();
        reset.setEmail(email);
        reset.setToken(token);
        reset.setExpiredAt(expiredAt);
        reset.setIsUsed(false);

        tokenMapper.insertToken(reset);

        // ✅ 이메일 전송
        String link = "http://localhost:8080/reset-password?token=" + token;
        String subject = "[IB Mall] 비밀번호 재설정 링크";
        String body = "비밀번호를 재설정하려면 아래 링크를 클릭하세요 (30분간 유효):\n" + link;

        mailService.sendEmail(email, subject, body);
    }
    
    public boolean resetPassword(String token, String newPassword) {
        // 토큰 조회 + 유효성 검사
        PasswordResetToken found = tokenMapper.findValidToken(token, LocalDateTime.now());

        if (found == null || Boolean.TRUE.equals(found.getIsUsed())) {
            return false;
        }

        // 회원 이메일 기반으로 비밀번호 암호화 & 업데이트
        Member member = memberMapper.findByEmail(found.getEmail());
        if (member == null) {
            return false;
        }

        String encoded = passwordEncoder.encode(newPassword);
        memberMapper.updatePassword(member.getEmail(), encoded);

        // 토큰 사용 완료 표시
        tokenMapper.markAsUsed(found.getId());

        return true;
    }
}
