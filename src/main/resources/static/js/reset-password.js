// src/main/resources/static/js/reset-password.js

document.getElementById('reset-form').addEventListener('submit', async function (e) {
  e.preventDefault();

  const params = new URLSearchParams(location.search);
  const token = params.get('token');

  const form = e.target;
  const pw1 = form.newPassword.value;
  const pw2 = form.confirmPassword.value;

  if (pw1 !== pw2) {
    return alert('비밀번호가 일치하지 않습니다');
  }

  try {
    const res = await fetch('/api/password/reset', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token, newPassword: pw1 })
    });

    const result = await res.json();

    if (res.ok) {
      alert(result.message || '비밀번호가 변경되었습니다!');
      location.href = '/signin';
    } else {
      alert(result.message || '비밀번호 변경 실패');
    }
  } catch (err) {
    console.error(err);
    alert('서버 오류');
  }
});
