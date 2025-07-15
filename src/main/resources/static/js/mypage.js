// src/main/resources/static/js/mypage.js

document.addEventListener('DOMContentLoaded', () => {
  const sendEmailBtn = document.getElementById('send-email-btn');
  const verifySection = document.getElementById('verify-section');
  const verifyBtn = document.getElementById('verify-btn');
  const verificationCodeInput = document.getElementById('verification-code');
  const userEmail = document.getElementById('user-email')?.textContent;

  if (!userEmail) {
    console.error('사용자 이메일을 찾을 수 없습니다');
    return;
  }

  sendEmailBtn?.addEventListener('click', async () => {
    try {
      const res = await fetch('/api/email/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: userEmail })
      });

      if (res.ok) {
        alert('인증번호가 이메일로 전송되었습니다');
        verifySection.style.display = 'block';
      } else {
        alert('인증번호 전송 실패');
      }
    } catch (err) {
      console.error('이메일 전송 중 오류:', err);
      alert('서버 오류');
    }
  });

  verifyBtn?.addEventListener('click', async () => {
    const code = verificationCodeInput.value;

    try {
      const res = await fetch('/api/email/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: userEmail, code })
      });

      const result = await res.json();

      if (res.ok) {
        alert(result.message || '이메일 인증 성공!');
        location.reload();
      } else {
        alert(result.message || '인증 실패');
      }
    } catch (err) {
      console.error('이메일 인증 중 오류:', err);
      alert('서버 오류');
    }
  });
});
