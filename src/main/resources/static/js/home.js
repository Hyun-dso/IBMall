window.addEventListener('DOMContentLoaded', async () => {
  try {
    const res = await fetch('/api/member/me');
    if (res.ok) {
      document.getElementById('unauthenticated-buttons').style.display = 'none';
      document.getElementById('authenticated-buttons').style.display = 'block';
    }
  } catch (err) {
    console.error('세션 확인 실패', err);
  }

  const googleBtn = document.getElementById("google-signup-btn");
  if (googleBtn) {
    googleBtn.addEventListener("click", async function () {
      try {
        const res = await fetch("/api/oauth2/authorize/google");
        const url = await res.text();
        window.location.href = url;
      } catch (err) {
        alert("구글 인증 URL을 불러오지 못했습니다.");
        console.error(err);
      }
    });
  }
});
