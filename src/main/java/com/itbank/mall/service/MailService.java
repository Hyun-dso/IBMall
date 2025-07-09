package com.itbank.mall.service;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // 인증코드 생성 + 전송
    public String sendVerificationCode(String toEmail) {
        String code = generate6DigitCode();

        // 제목과 본문 내용은 여기서 작성
        String subject = "[IBMall] 이메일 인증코드";
        String text = "안녕하세요, IBMall입니다.\n\n"
                    + "인증코드: " + code + "\n"
                    + "유효시간: 15분\n\n";

        // 메일 작성 및 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

        return code; // 이걸 DB에 저장하거나 세션에 저장하는 구조로 확장 가능
    }

    // 코드 생성기
    private String generate6DigitCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}

