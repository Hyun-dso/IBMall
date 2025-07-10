package com.itbank.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public String generateAndSendVerificationCode(String email) {
        String code = generate6DigitCode();

        String subject = "[IBMall] 이메일 인증코드";
        String text = "안녕하세요, IBMall입니다.\n\n"
        			+ "15분 내에 받은 인증 코드를 입력 해 주세요"
                    + "인증코드: " + code + "\n";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

        return code;
    }

    private String generate6DigitCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
