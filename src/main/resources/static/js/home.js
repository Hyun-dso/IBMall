window.addEventListener('DOMContentLoaded', async () => {
  const token = localStorage.getItem('jwtToken');

  if (!token) return;

  try {
    const res = await fetch('/api/member/me', {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (res.ok) {
      const user = await res.json();
      document.getElementById('nickname-wrapper').style.display = 'block';
      document.getElementById('nickname-display').textContent = `${user.nickname}님 어서오세요!`;

      document.getElementById('unauthenticated-buttons').style.display = 'none';
      document.getElementById('authenticated-buttons').style.display = 'block';
    }
  } catch (err) {
    console.error('토큰 사용자 확인 실패', err);
  }
});
