package com.itbank.mall.service;

import java.util.List;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public String generateAndSendVerificationCode(String email) {
        String code = generate6DigitCode();

        String subject = "[IBMall] 이메일 인증코드";
        String text = "안녕하세요, IBMall입니다.\n\n"
                    + "15분 내에 받은 인증 코드를 입력해 주세요.\n"
                    + "인증코드: " + code + "\n";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

        return code;
    }

    private String generate6DigitCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // ✅ 공통 결제완료 메일 (게스트/멤버 동일하게 사용)
    public void sendPaymentCompleteEmail(String toEmail, String orderUid, List<String> productLines, int totalAmount) {
        // 내부 포맷은 기존 guest 메일 포맷 재사용
        sendGuestPaymentCompleteEmail(toEmail, orderUid, productLines, totalAmount);
    }

    // (기존 게스트용 – 그대로 두고 공통 메서드가 재사용)
    public void sendGuestPaymentCompleteEmail(String toEmail, String orderUid, List<String> productLines, int totalAmount) {
        StringBuilder body = new StringBuilder();
        body.append("안녕하세요, IBMall입니다.\n\n")
            .append("아래와 같이 결제가 완료되었습니다.\n\n")
            .append("주문번호: ").append(orderUid).append("\n")
            .append("결제금액: ").append(String.format("%,d원", totalAmount)).append("\n\n")
            .append("[주문 상품 목록]\n");

        for (String line : productLines) {
            body.append("- ").append(line).append("\n");
        }

        body.append("\n감사합니다.");

        sendEmail(toEmail, "[IBMall] 결제 완료 안내", body.toString());
    }
}
