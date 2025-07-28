const IMP = window.IMP;
IMP.init("imp16763668");  // 아임포트 가맹점 식별코드

document.addEventListener("DOMContentLoaded", () => {
	const payBtn = document.getElementById("pay-btn");

	if (payBtn) {
		payBtn.addEventListener("click", function() {
			const pg = document.getElementById("pg-selector").value;

			IMP.request_pay({
				pg: pg,
				pay_method: "card",
				merchant_uid: "order_" + new Date().getTime(),
				name: "테스트상품",
				amount: 1,
				buyer_email: "guest@example.com",
				buyer_name: "비회원",
				buyer_tel: "010-9999-8888"
			}, function(rsp) {
				if (rsp.success) {
					alert("결제 성공: " + rsp.imp_uid);

					fetch("/api/payments/guest", {
					  method: "POST",
					  headers: {
					    "Content-Type": "application/json"
					  },
					  body: JSON.stringify({
					    orderUid: rsp.merchant_uid,
					    productName: rsp.name,
					    orderPrice: rsp.paid_amount,
					    paidAmount: rsp.paid_amount,
					    paymentMethod: "card",
					    status: "paid",
					    transactionId: rsp.imp_uid,
					    pgProvider: pg.includes("_v2") ? pg : pg.split('.')[0],

					    productId: 58,
					    productOptionId: 1,
					    quantity: 1,

					    buyerName: rsp.buyer_name,
					    buyerEmail: rsp.buyer_email,
					    buyerPhone: rsp.buyer_tel,
					    buyerAddress: "서울시 강남구 테헤란로 123",

					    recipientName: "홍수령",
					    recipientPhone: "010-1234-5678",
					    recipientAddress: "서울시 강북구 수령로 456"
					  })
					})
					.then(res => res.text())
					.then(msg => console.log("서버 응답:", msg));
				} else {
					alert("결제 실패: " + rsp.error_msg);
				}
			});
		});
	}
});

