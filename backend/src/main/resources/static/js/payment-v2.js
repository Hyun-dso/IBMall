console.log('📦 V2 JS 로드됨');

document.addEventListener('DOMContentLoaded', function () {
  const payBtn = document.getElementById('pay-btn');
  if (!payBtn) {
    console.warn('❌ 버튼 못 찾음');
    return;
  }

  console.log('🟢 버튼 이벤트 등록 준비됨');

  payBtn.addEventListener('click', function () {
    console.log('🟠 버튼 클릭됨');

    // SDK 테스트
    if (typeof PortOne === 'undefined') {
      console.error('❌ PortOne SDK 로드 실패');
      alert('PortOne SDK가 로드되지 않았습니다.');
      return;
    }

    alert('PortOne SDK 확인됨');

    PortOne.requestPayment({
      channelKey: 'channel-key-1dfba53d-d82e-4ab9-b35d-83580951d661',  // ✅ 이거만 주면 됨
	  storeId: 'store-cc5fa09c-2fa8-404f-ab18-359a100c22cb',
	  paymentId: 'order_' + new Date().getTime(),
      payMethod: 'CARD',
      totalAmount: 1000,
	  currency: 'KRW', // ✅ 반드시 추가
      orderName: '테스트 결제',
      customer: {
        fullName: '홍길동',
        email: 'test@example.com',
        phoneNumber: '01012345678'
      },
      card: {
        number: '1234123412341234',
        expirationYear: '2027',
        expirationMonth: '07',
        password: '12'
      },
      customerIdentityNumber: '880101'
    }).then(response => {
      alert('응답 도착');
      console.log(response);
	  }).catch(err => {
	    alert('❌ 결제 오류: ' + (err?.message || '원인 불명'));
	    console.error('🚨 결제 실패 상세:', err);
	  });
  });
});
