package com.itbank.mall.event.delivery;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.itbank.mall.event.delivery.DeliveryNotifyLineDto;
import com.itbank.mall.event.delivery.DeliveryNotifySummaryDto;
import com.itbank.mall.mapper.delivery.DeliveryNotifyMapper;
import com.itbank.mall.service.notify.EmailService; // ✅ 여기 중요

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

	private final EmailService emailService; // ✅ 타입/이름 통일
	private final DeliveryNotifyMapper deliveryNotifyMapper; // ✅ 배송 전용 매퍼

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onChanged(DeliveryStatusChangedEvent e) {
		final Long orderId = e.getOrderId();

		try {
			// 요약 + 수신자
			DeliveryNotifySummaryDto sum = deliveryNotifyMapper.findDeliveryNotifySummaryByOrderId(orderId); // ✅ 메서드명
																												// 일치
			if (sum == null || isBlank(sum.getMemberEmail())) {
				log.warn("[DELIVERY] 메일 미발송: 수신 이메일 없음, orderId={}", orderId);
				return;
			}

			// 상품 라인
			List<DeliveryNotifyLineDto> lines = deliveryNotifyMapper.findDeliveryNotifyLinesByOrderId(orderId); // ✅
																												// 메서드명
																												// 일치

			// 운송장 (이벤트 우선)
			String tracking = !isBlank(e.getTrackingNumber()) ? e.getTrackingNumber() : nvl(sum.getTrackingNumber());

			// 제목/본문
			String subject = buildSubject(e.getNewStatus(), sum.getOrderUid(), lines);
			String body = buildBody(nvl(sum.getMemberNickname()), orderId, nvl(sum.getOrderUid()), tracking,
					e.getNewStatus(), lines, nz(sum.getTotalAmount()));

			// ✅ EmailService#send(...) 정확히 매칭
			emailService.send(sum.getMemberEmail(), subject, body);

		} catch (Exception ex) {
			log.error("[DELIVERY][MAIL ERROR] orderId={}", orderId, ex);
		}
	}

	// ===== helpers =====
	private String buildSubject(String status, String orderUid, List<DeliveryNotifyLineDto> lines) {
		String prefix = ("DELIVERED".equalsIgnoreCase(status)) ? "배송완료" : "배송시작";
		if (lines == null || lines.isEmpty()) {
			return String.format("[IBMall] %s 안내", prefix);
		}
		String first = truncate(firstLine(lines.get(0)), 40);
		int extra = Math.max(0, lines.size() - 1);
		String tail = (extra > 0) ? " 외 " + extra + "개" : "";
		return String.format("[IBMall] %s - %s%s", prefix, first, tail);
	}

	private String firstLine(DeliveryNotifyLineDto dto) {
		if (dto == null)
			return "상품";
		return nvl(dto.getProductName(), "상품") + " x" + dto.getQuantity();
	}

	private String buildBody(String nickname, Long orderId, // 사용 안 함
			String orderUid, // 사용 안 함
			String trackingNumber, String status, List<DeliveryNotifyLineDto> lines, long totalAmount) {

		String hi = isBlank(nickname) ? "고객" : nickname;
		StringBuilder sb = new StringBuilder();

// 상단 인사 + 상태
		if ("DELIVERED".equalsIgnoreCase(status)) {
			sb.append(hi).append("님,\n\n").append("고객님의 주문 상품 배송이 완료되었습니다.\n\n");
		} else {
			sb.append(hi).append("님,\n\n").append("고객님의 주문 상품이 배송을 시작했습니다.\n\n");
		}

// [상품내역]
		sb.append("[상품내역]\n");
		if (lines == null || lines.isEmpty()) {
			sb.append("- (주문 상품 정보가 없습니다)\n\n");
		} else {
			for (DeliveryNotifyLineDto it : lines) {
				sb.append("- ").append(nvl(it.getProductName(), "상품")).append(" x ").append(it.getQuantity())
						.append(" | 단가  ").append(it.getUnitPrice()).append("원").append(" = ")
						.append(it.getLineTotal()).append("원").append("\n");
			}
			sb.append("\n");
		}

// [운송장번호]
		sb.append("[운송장번호]\n").append("- 운송장번호 : ").append(!isBlank(trackingNumber) ? trackingNumber : "--")
				.append("\n\n");

// [총결제금액]
		sb.append("[총결제금액]\n").append("결제금액 : ").append(totalAmount).append("원\n\n");

// 안내
		sb.append("안내\n").append("- 수령을 못하신 경우 배송조회 페이지 또는 고객센터(02-123-1234)/{ibmall@it.bank})로 문의해주세요.\n")
				.append("- 상품에 문제가 있다면 교환/반품 안내를 확인해주세요: {고객센터_URL}\n\n").append("IBMALL 드림");

		return sb.toString();
	}

	private String truncate(String s, int max) {
		return (s != null && s.length() > max) ? s.substring(0, max) + "…" : (s == null ? "" : s);
	}

	private String nvl(String s) {
		return s == null ? "" : s;
	}

	private String nvl(String s, String def) {
		return (s == null || s.isBlank()) ? def : s;
	}

	private boolean isBlank(String s) {
		return s == null || s.isBlank();
	}

	private long nz(Long v) {
		return v == null ? 0L : v;
	}
}
