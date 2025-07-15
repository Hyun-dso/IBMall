// src/main/resources/static/js/signin.js

document.getElementById('signin-form').addEventListener('submit', async function (e) {
  e.preventDefault();

  const form = e.target;
  const data = {
    email: form.email.value,
    password: form.password.value
  };

  try {
    const res = await fetch('/api/signin', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    const result = await res.json();

    if (res.ok) {
      alert(result.message || '로그인 성공!');
      location.href = '/';  // 홈으로 이동
    } else {
      alert(result.message || '로그인 실패');
    }
  } catch (err) {
    alert('서버 오류');
    console.error(err);
  }
});
