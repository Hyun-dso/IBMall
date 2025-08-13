package com.itbank.mall.event.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.itbank.mall.mapper.member.MemberMapper;
import com.itbank.mall.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final MailService mailService;
    private final MemberMapper memberMapper; // 멤버 이메일 조회용 (아래 3) 추가)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedEvent e) {
        String email = e.getEmail();
        if ((email == null || email.isBlank()) && e.getMemberId() != null) {
            email = memberMapper.findEmailById(e.getMemberId());
        }
        if (email == null || email.isBlank()) {
            log.warn("[AFTER_COMMIT] 결제완료 메일 미발송: email 없음, orderUid={}", e.getOrderUid());
            return;
        }

        log.info("[AFTER_COMMIT] send payment mail: orderUid={}, email={}", e.getOrderUid(), email);
        // MailService에 공통 메서드가 없다면 아래처럼 새로 하나 추가하거나, 기존 guest용을 재사용해도 됩니다.
        mailService.sendPaymentCompleteEmail(email, e.getOrderUid(), e.getProductLines(), e.getPaidAmount());
    }
}
