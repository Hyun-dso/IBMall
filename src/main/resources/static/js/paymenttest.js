function purchaseProduct(productId, productOptionId, price, name) {
  if (typeof PortOne === 'undefined') {
    alert('SDK 로드 실패');
    return;
  }

  const merchant_uid = 'order_' + new Date().getTime();

  // 사용자 입력값 (임시 하드코딩)
  const buyerName = '비회원';
  const buyerEmail = 'guest@example.com';
  const buyerPhone = '010-9999-8888';
  const buyerAddress = '서울시 강남구 테헤란로 123';

  const recipientName = '홍수령';
  const recipientPhone = '010-1234-5678';
  const recipientAddress = '서울시 강북구 수령로 456';

  PortOne.requestPayment({
    channelKey: 'channel-key-1dfba53d-d82e-4ab9-b35d-83580951d661',
    storeId: 'store-cc5fa09c-2fa8-404f-ab18-359a100c22cb',
    paymentId: merchant_uid,
    payMethod: 'CARD',
    totalAmount: price,
    currency: 'KRW',
    orderName: name,
    customer: {
      fullName: buyerName,
      email: buyerEmail,
      phoneNumber: buyerPhone
    },
    card: {
      number: '1234123412341234',
      expirationYear: '2027',
      expirationMonth: '07',
      password: '12'
    },
    customerIdentityNumber: '880101'
  }).then(response => {
    console.log('✅ 결제 성공 응답:', response);

	if (!response.paymentId || !response.txId) {
	  alert('결제 응답이 올바르지 않습니다.');
	  return;
	}

    const body = {
      orderUid: response.paymentId,
      productName: name,
      orderPrice: price,
      paidAmount: response.totalAmount,
      paymentMethod: response.payMethod,
      status: response.status,
      transactionId: response.txId,
      pgProvider: response.pgProvider,

      productId: productId,
      productOptionId: productOptionId,
      quantity: 1,

      buyerName,
      buyerEmail,
      buyerPhone,
      buyerAddress,

      recipientName,
      recipientPhone,
      recipientAddress
    };

    // 🔁 서버에 주문 저장 요청
    fetch('/api/payments/guest', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
      .then(res => res.json())
      .then(json => {
        alert(json.message || '서버 저장 완료');
        console.log(json);
      })
      .catch(err => {
        console.error('❌ 서버 저장 실패:', err);
      });

  }).catch(err => {
    console.error('❌ 결제 실패:', err);
  });
}

// 상품 목록 불러오기
window.addEventListener('DOMContentLoaded', () => {
  fetch('/api/products')
    .then(res => res.json())
    .then(json => {
      const tbody = document.getElementById('productTableBody');
      const products = json.data;

      products.forEach(p => {
        const tr = document.createElement('tr');

        const nameTd = document.createElement('td');
        nameTd.textContent = p.name;

        const priceTd = document.createElement('td');
        priceTd.textContent = p.price + '원';

        const buttonTd = document.createElement('td');
        const btn = document.createElement('button');
        btn.textContent = '결제하기';

        const productOptionId = 1;  // 현재는 임시로 1번

        btn.onclick = () => purchaseProduct(p.productId, productOptionId, p.price, p.name);

        buttonTd.appendChild(btn);
        tr.appendChild(nameTd);
        tr.appendChild(priceTd);
        tr.appendChild(buttonTd);
        tbody.appendChild(tr);
      });
    })
    .catch(err => {
      console.error('상품 불러오기 실패:', err);
    });
});
