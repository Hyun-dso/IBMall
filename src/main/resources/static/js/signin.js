// src/main/resources/static/js/signin.js

document.getElementById('signin-form').addEventListener('submit', async function (e) {
  e.preventDefault();

  const form = e.target;
  const data = {
    email: form.email.value,
    password: form.password.value
  };

  try {
    const res = await fetch('/api/auth/signin', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    const result = await res.json();

	if (res.ok) {
	  // ⬇️ 토큰 저장 추가!
	  if (result.token) {
	    localStorage.setItem('jwtToken', result.token);
	  }

	  alert(result.message || '로그인 성공!');
	  console.log('📦 저장한 토큰:', localStorage.getItem('jwtToken'));
	  console.log('📥 로드된 토큰:', token);
	  location.href = '/';
    } else {
      alert(result.message || '로그인 실패');
    }
  } catch (err) {
    alert('서버 오류');
    console.error(err);
  }
});
