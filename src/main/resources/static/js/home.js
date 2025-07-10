
window.addEventListener('DOMContentLoaded', async () => {
  try {
    const res = await fetch('/api/member/me');
    if (res.ok) {
      // 로그인 상태
      document.getElementById('unauthenticated-buttons').style.display = 'none';
      document.getElementById('authenticated-buttons').style.display = 'block';
    }
  } catch (err) {
    console.error('세션 확인 실패', err);
  }
});

function logout() {
  fetch('/api/logout', { method: 'POST' })
    .then(() => location.href = '/');
}