// src/main/resources/static/js/find-password.js

document.getElementById('reset-request-form').addEventListener('submit', async function (e) {
  e.preventDefault();

  const email = e.target.email.value;

  try {
    const res = await fetch('/api/password/send-reset-link', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });

    const result = await res.json();

    if (res.ok) {
      alert(result.message || '비밀번호 재설정 링크가 이메일로 전송되었습니다.');
      location.href = '/signin';
    } else {
      alert(result.message || '이메일 전송 실패');
    }
  } catch (err) {
    alert('서버 오류');
    console.error(err);
  }
});
