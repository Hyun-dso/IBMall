// src/main/resources/static/js/signup.js
document.getElementById('signup-form').addEventListener('submit', async function (e) {
  e.preventDefault();

  const form = e.target;
  const data = {
    email: form.email.value,
    nickname: form.nickname.value,
    password: form.password.value
  };

  try {
    const res = await fetch('/api/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    if (res.ok) {
      alert('회원가입 성공! 이메일 인증을 확인하세요.');
    } else {
      const err = await res.text();
      alert('오류 발생: ' + err);
    }
  } catch (err) {
    alert('서버 연결 실패');
    console.error(err);
  }
});
