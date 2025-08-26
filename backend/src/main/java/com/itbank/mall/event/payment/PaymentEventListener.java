package com.itbank.mall.event.payment;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.itbank.mall.dto.orders.OrderEmailLineDto;
import com.itbank.mall.mapper.member.MemberMapper;
import com.itbank.mall.mapper.orders.OrderMapper;
import com.itbank.mall.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final MailService mailService;
    private final MemberMapper memberMapper;
    private final OrderMapper orderMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedEvent e) {
        try {
            // 1) 수신 이메일 결정
            String email = e.getEmail();
            if ((email == null || email.isBlank()) && e.getMemberId() != null) {
                email = memberMapper.findEmailById(e.getMemberId());
            }
            if (email == null || email.isBlank()) {
                log.warn("[AFTER_COMMIT] 결제완료 메일 미발송: email 없음, orderUid={}", e.getOrderUid());
                return;
            }

            // 2) 메일 본문용 라인아이템 조회 (orderUid 기준)
            List<OrderEmailLineDto> lines = orderMapper.findOrderEmailLinesByOrderUid(e.getOrderUid());
            if (lines == null || lines.isEmpty()) {
                log.warn("[MAIL] 라인아이템이 비어있음: orderUid={}", e.getOrderUid());
            }

            // 3) 제목 생성: "[IBMall] 주문내역 - 첫상품 외 N개 / orderUid"
            String subject = buildSubject(e.getOrderUid(), lines);

            log.info("[AFTER_COMMIT] send payment mail: orderUid={}, email={}, subject={}", e.getOrderUid(), email, subject);

            // 4) 발송 (총액은 이벤트의 paidAmount 사용)
            mailService.sendPaymentCompleteEmail(
                email,
                subject,
                e.getOrderUid(),
                lines,
                e.getPaidAmount()
            );

        } catch (Exception ex) {
            log.error("[MAIL ERROR] 결제완료 메일 발송 실패 orderUid={}", e.getOrderUid(), ex);
        }
    }

    /** 제목: [IBMall] 주문내역 - {첫상품명} 외 {N}개 / {orderUid} */
    private String buildSubject(String orderUid, List<OrderEmailLineDto> lines) {
        final String base = "주문내역";
        if (lines == null || lines.isEmpty()) {
            return String.format("[IBMall] %s / %s", base, orderUid);
        }
        String first = truncate(summarizeFirstLine(lines.get(0)), 40);
        int extra = Math.max(0, lines.size() - 1);
        String tail = (extra > 0) ? " 외 " + extra + "개" : "";
        return String.format("[IBMall] %s - %s%s / %s", base, first, tail, orderUid);
    }

    /** 첫 라인 요약(옵션이 있으면 괄호 포함) */
    private String summarizeFirstLine(OrderEmailLineDto dto) {
        if (dto == null) return "상품";
        String name = dto.getProductName() != null ? dto.getProductName() : "상품";
        String opt  = (dto.getOptionValue() != null && !dto.getOptionValue().isBlank())
                    ? " (" + dto.getOptionValue() + ")"
                    : "";
        return name + opt + " x" + dto.getQuantity();
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return (s.length() > max) ? s.substring(0, max) + "…" : s;
    }
}
