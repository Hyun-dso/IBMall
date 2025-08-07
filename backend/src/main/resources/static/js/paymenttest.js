document.addEventListener('DOMContentLoaded', async () => {
  const res = await fetch('/api/products');
  const result = await res.json();
  const products = result.data;

  const list = document.getElementById('product-list');
  list.innerHTML = '';

  products.forEach(p => {
    const div = document.createElement('div');
    div.innerHTML = `
      <p>
        <b>${p.name}</b> - ${p.price}원
        <button class="buy-btn" data-name="${p.name}" data-price="${p.price}">
          구매하기
        </button>
      </p>
    `;
    list.appendChild(div);
  });

  document.querySelectorAll('.buy-btn').forEach(button => {
    button.addEventListener('click', async () => {
      const name = button.dataset.name;
      const price = parseInt(button.dataset.price);
      const orderId = 'order_' + Date.now();
      const storeId = 'impXXXXXXXX'; // 👈 너의 포트원 가맹점 ID로 교체

      const payment = PortOne.requestPayment({
        storeId,
        orderId,
        orderName: name,
        totalAmount: price,
        currency: 'KRW',
        customer: {
          fullName: '테스트 유저',
          email: 'test@example.com',
          phoneNumber: '01012345678'
        },
        method: 'CARD',
        card: {
          number: '9410-9410-9410-9410',
          expiry: '2025-12',
          birth: '880101',
          pwd2Digit: '11'
        }
      });

      const result = await payment;
      console.log('✅ PortOne 결제 응답:', result);

      if (result.code === 'SUCCESS') {
        alert('✅ 결제 성공!');
      } else {
        alert('❌ 결제 실패: ' + result.message);
      }
    });
  });
});
