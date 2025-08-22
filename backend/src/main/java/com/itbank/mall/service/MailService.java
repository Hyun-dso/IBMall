package com.itbank.mall.service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.itbank.mall.dto.orders.OrderEmailLineDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // =========================
    // 인증 코드 메일 (기존 유지)
    // =========================
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

    // =========================
    // 공통 메일 전송
    // =========================
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // ===========================================================
    // (신규) 결제완료 메일 - 라인아이템 버전 (리스너가 이걸 호출)
    // ===========================================================
    public void sendPaymentCompleteEmail(String toEmail,
                                         String subject,
                                         String orderUid,
                                         List<OrderEmailLineDto> lines,
                                         int totalAmount) {
        String body = renderPaymentCompleteBody(orderUid, lines, totalAmount);
        sendEmail(toEmail, subject, body);
    }

    // ==========================================================================
    // (하위호환) 결제완료 메일 - 문자열 라인 버전 (기존 호출부 그대로 사용 가능)
    // ==========================================================================
    public void sendPaymentCompleteEmail(String toEmail,
                                         String orderUid,
                                         List<String> productLines,
                                         int totalAmount) {
        String body = renderSimpleLinesBody(orderUid, productLines, totalAmount);
        sendEmail(toEmail, "[IBMall] 결제 완료 안내", body);
    }

    // =========================
    // 본문 렌더러들
    // =========================
    private String renderPaymentCompleteBody(String orderUid,
                                             List<OrderEmailLineDto> lines,
                                             int totalAmount) {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요, IBMall입니다.\n\n")
          .append("아래와 같이 결제가 완료되었습니다.\n\n")
          .append("주문번호: ").append(orderUid).append("\n\n")
          .append("[주문 상품 목록]\n");

        if (lines == null || lines.isEmpty()) {
            sb.append("- (주문 상품 내역 없음)\n");
        } else {
            for (OrderEmailLineDto dto : lines) {
                String name = (dto.getProductName() != null) ? dto.getProductName() : "상품";
                String opt = (dto.getOptionValue() != null && !dto.getOptionValue().isBlank())
                           ? " (" + dto.getOptionValue() + ")"
                           : "";
                int qty = Math.max(1, dto.getQuantity());
                int lineTotal = Math.max(0, dto.getLineTotal());
                int unit = safeDivide(lineTotal, qty);

                sb.append(String.format("- %s%s   x%d   %s  →  %s\n",
                    name, opt, qty, won(unit), won(lineTotal)));
            }
        }

        sb.append("\n")
          .append("----------------------------------------------\n")
          .append("결제금액(실결제): ").append(won(totalAmount)).append("\n")
          .append("\n감사합니다.");

        return sb.toString();
    }

    private String renderSimpleLinesBody(String orderUid,
                                         List<String> productLines,
                                         int totalAmount) {
        StringBuilder body = new StringBuilder();
        body.append("안녕하세요, IBMall입니다.\n\n")
            .append("아래와 같이 결제가 완료되었습니다.\n\n")
            .append("주문번호: ").append(orderUid).append("\n")
            .append("결제금액: ").append(won(totalAmount)).append("\n\n")
            .append("[주문 상품 목록]\n");

        if (productLines == null || productLines.isEmpty()) {
            body.append("- (주문 상품 내역 없음)\n");
        } else {
            for (String line : productLines) {
                body.append("- ").append(line).append("\n");
            }
        }

        body.append("\n감사합니다.");
        return body.toString();
    }

    private int safeDivide(int a, int b) {
        return (b == 0) ? 0 : Math.max(0, a / b);
    }

    private String won(int amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.KOREA);
        return nf.format(Math.max(0, amount)) + "원";
    }
}
