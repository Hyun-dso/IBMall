const googleSignupForm = document.getElementById('google-signup-form');

googleSignupForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const form = e.target;
  const data = {
    email: form.email.value,
    nickname: form.nickname.value,
    password: form.password.value
  };

  try {
    const res = await fetch('/api/oauth2/signup-by-google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    const result = await res.text(); // 텍스트 반환 예상
    if (res.ok) {
      alert('회원가입 완료! 로그인되었습니다');
      location.href = '/';
    } else {
      alert(result || '가입 실패');
    }
  } catch (err) {
    alert('서버 오류');
    console.error(err);
  }
});

// ✅ 닉네임 중복 검사 추가
const nicknameInput = document.querySelector('input[name="nickname"]');

nicknameInput.addEventListener('blur', async (e) => {
  const nickname = e.target.value.trim();
  if (!nickname) return;

  try {
    const res = await fetch(`/api/nickname/check?nickname=${encodeURIComponent(nickname)}`);
    const result = await res.json();

    if (result.exists) {
      alert('이미 사용 중인 닉네임입니다.');
      nicknameInput.focus();
    }
  } catch (err) {
    console.error('닉네임 중복 검사 실패', err);
  }
});