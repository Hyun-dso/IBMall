// src/main/resources/static/js/signup.js
let signupEmail = '';

const signupForm = document.getElementById('signup-form');
const verifySection = document.getElementById('verify-section');
const postSignupSection = document.getElementById('post-signup-section');
const verifyForm = document.getElementById('verify-form');
const sendVerificationBtn = document.getElementById('send-verification-btn');

signupForm.addEventListener('submit', async (e) => {
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

    const result = await res.json();
    if (res.ok) {
      signupEmail = data.email;
      alert(result.message || '회원가입 성공!');
      postSignupSection.style.display = 'block';  // ✅ post-signup 보이기
      signupForm.style.display = 'none';           // ✅ 폼 숨기기
    } else {
      alert(result.message || '회원가입 실패');
    }
  } catch (err) {
    alert('서버 오류');
    console.error(err);
  }
});

sendVerificationBtn.addEventListener('click', async () => {
  try {
    const res = await fetch('/api/email/send', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: signupEmail })
    });

    const result = await res.json();
    alert(result.message || '인증번호가 이메일로 전송되었습니다.');
    postSignupSection.style.display = 'none';
    verifySection.style.display = 'block';  // ✅ 인증 폼 보여주기
  } catch (err) {
    alert('인증 요청 실패');
    console.error(err);
  }
});

verifyForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const code = verifyForm.code.value;

  try {
    const res = await fetch('/api/email/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: signupEmail, code })
    });

    const result = await res.json();
    if (res.ok) {
      alert(result.message || '이메일 인증 성공!');
      location.href = '/';
    } else {
      alert(result.message || '인증 실패');
    }
  } catch (err) {
    alert('서버 오류');
    console.error(err);
  }
});

// ✨ 닉네임 중복 체크 로직 추가
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
