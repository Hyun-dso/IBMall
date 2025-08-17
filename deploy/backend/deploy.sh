#!/usr/bin/env bash
set -euo pipefail

IMAGE="${1:?Usage: deploy.sh <image>}"
SERVICE="${SERVICE:-ibmall-backend}"      # 컨테이너 이름 prefix
PORT="${PORT:-8080}"                       # 호스트/컨테이너 내부 모두 8080
HEALTH_PATH="${HEALTH_PATH:-/actuator/health}"
ENV_FILE="${ENV_FILE:-/home/ubuntu/env/backend.prod}"

NAME="${SERVICE}"                           # 고정 이름(blue/green 안 씀)

echo "[deploy-backend] image=$IMAGE port=$PORT health=$HEALTH_PATH"

# 이미지 풀
sudo docker pull "$IMAGE"

# 기존 컨테이너 있으면 부드럽게 교체
if sudo docker ps -a --format '{{.Names}}' | grep -q "^${NAME}$"; then
  echo "[deploy-backend] stopping old container..."
  sudo docker rm -f "$NAME" >/dev/null 2>&1 || true
fi

# 새 컨테이너 실행 (고정 포트)
sudo docker run -d --name "$NAME" -p ${PORT}:${PORT} \
  --restart=always \
  --env-file "$ENV_FILE" \
  -e SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}" \
  "$IMAGE"

# 헬스체크 대기
echo -n "[deploy-backend] waiting health on :$PORT "
for i in {1..60}; do
  if curl -fsS "http://127.0.0.1:${PORT}${HEALTH_PATH}" >/dev/null 2>&1; then
    echo "OK"; break
  fi
  echo -n "."; sleep 1
done
curl -fsS "http://127.0.0.1:${PORT}${HEALTH_PATH}" >/dev/null 2>&1 || { echo >&2 "[deploy-backend] health check FAILED"; exit 1; }

# 오래된 이미지 정리(선택)
sudo docker image prune -f >/dev/null 2>&1 || true
echo "[deploy-backend] done"
