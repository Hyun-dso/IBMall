const IMP = window.IMP;
IMP.init("imp16763668");  // 아임포트 가맹점 식별코드

document.addEventListener("DOMContentLoaded", () => {
  const payBtn = document.getElementById("pay-btn");

  if (payBtn) {
    payBtn.addEventListener("click", function () {
      IMP.request_pay({
        pg: "kakaopay.TC0ONETIME",
        pay_method: "card",
        merchant_uid: "order_" + new Date().getTime(),
        name: "테스트상품",
        amount: 1,
        buyer_email: "test@example.com",
        buyer_name: "홍길동",
        buyer_tel: "010-1234-5678"
      }, function (rsp) {
        if (rsp.success) {
          alert("결제 성공: " + rsp.imp_uid);
          fetch("/api/pay", {
            method: "POST",
            headers: {
              "Content-Type": "application/json"
            },
            body: JSON.stringify({
              merchant_uid: rsp.merchant_uid,
              name: rsp.name,
              amount: rsp.paid_amount,
              buyer_email: rsp.buyer_email,
              buyer_name: rsp.buyer_name,
              buyer_tel: rsp.buyer_tel
            })
          })
          .then(res => res.text())
          .then(msg => alert("서버 응답: " + msg));
        } else {
          alert("결제 실패: " + rsp.error_msg);
        }
      });
    });
  }
});
