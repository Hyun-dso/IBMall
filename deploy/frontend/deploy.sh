#!/usr/bin/env bash
set -euo pipefail

# Usage: deploy.sh <image>
IMAGE="${1:?Usage: deploy.sh <image>}"
BLUE_PORT="${BLUE_PORT:-3000}"
GREEN_PORT="${GREEN_PORT:-3001}"
HEALTH_PATH="${HEALTH_PATH:-/healthz}"
SNIPPET="/etc/nginx/snippets/ibmall-upstream.conf"

echo "[deploy] image=${IMAGE}"

# 현재 Nginx 업스트림이 가리키는 포트 파악
CURRENT_PORT="$(grep -o '127\.0\.0\.1:[0-9]*' "$SNIPPET" 2>/dev/null | awk -F: '{print $2}' || true)"
if [[ "${CURRENT_PORT:-$BLUE_PORT}" == "$BLUE_PORT" ]]; then
  NEW_PORT="$GREEN_PORT"
  OLD_PORT="$BLUE_PORT"
  NEW_NAME="ibmall-frontend-green"
  OLD_NAME="ibmall-frontend-blue"
else
  NEW_PORT="$BLUE_PORT"
  OLD_PORT="$GREEN_PORT"
  NEW_NAME="ibmall-frontend-blue"
  OLD_NAME="ibmall-frontend-green"
fi
echo "[deploy] current=${CURRENT_PORT:-none} -> new=$NEW_PORT (old=$OLD_PORT)"

# 새 컨테이너 실행
sudo docker pull "$IMAGE"
sudo docker rm -f "$NEW_NAME" >/dev/null 2>&1 || true
sudo docker run -d --name "$NEW_NAME" -p ${NEW_PORT}:3000 \
  --restart=always \
  -e NODE_ENV=production \
  "$IMAGE"

# 헬스체크 대기
echo -n "[deploy] waiting health on :$NEW_PORT "
for i in {1..60}; do
  if curl -fsS "http://127.0.0.1:${NEW_PORT}${HEALTH_PATH}" >/dev/null 2>&1; then
    echo "OK"
    break
  fi
  echo -n "."
  sleep 1
done
curl -fsS "http://127.0.0.1:${NEW_PORT}${HEALTH_PATH}" >/dev/null 2>&1 || { echo >&2 "[deploy] health check FAILED"; exit 1; }

# Nginx 업스트림 포인터 전환
sudo sed -i "s|127\.0\.0\.1:[0-9]*|127.0.0.1:${NEW_PORT}|g" "$SNIPPET"
sudo nginx -t
sudo nginx -s reload
echo "[deploy] nginx switched to :$NEW_PORT"

# 이전 컨테이너 정리(그레이스풀)
sudo docker stop "$OLD_NAME" >/dev/null 2>&1 || true
sudo docker rm   "$OLD_NAME" >/dev/null 2>&1 || true

# 오래된 이미지 정리(옵션)
sudo docker image prune -f >/dev/null 2>&1 || true

echo "[deploy] done"