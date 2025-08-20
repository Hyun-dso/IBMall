#!/usr/bin/env bash
set -euo pipefail

# Usage: deploy.sh <image>
IMAGE="${1:?Usage: deploy.sh <image>}"

APP_NAME="ibmall-frontend"
BLUE_PORT="${BLUE_PORT:-3000}"
GREEN_PORT="${GREEN_PORT:-3001}"
HEALTH_PATH="${HEALTH_PATH:-/healthz}"
SNIPPET="/etc/nginx/snippets/ibmall-upstream.conf"

echo "[deploy] image=${IMAGE}"

# ---- helpers ---------------------------------------------------------------
probe_health() {
  # $1: port -> return 0 if /healthz 200
  curl -fsS "http://127.0.0.1:$1${HEALTH_PATH}" >/dev/null 2>&1
}

live_port() {
  # return first healthy port among BLUE/GREEN, else empty
  for p in "$BLUE_PORT" "$GREEN_PORT"; do
    if probe_health "$p"; then echo "$p"; return 0; fi
  done
  echo ""
  return 1
}

write_snippet() {
  local port="$1"
  echo "proxy_pass http://127.0.0.1:${port};" | sudo tee "${SNIPPET}" >/dev/null
}

nginx_reload() {
  sudo nginx -t
  sudo systemctl reload nginx || sudo nginx -s reload
}

disk_guard() {
  local used
  used=$(df -P / | awk 'NR==2 {gsub("%","",$5); print $5}')
  if [ "$used" -ge 85 ]; then
    echo "[deploy] Disk usage ${used}% >= 85%, pruning docker artifacts..."
    docker system prune -af --volumes || true
  fi
}

# rollback trap
ROLLBACK_NEW_NAME=""
ROLLBACK_SNIPPET_OLD=""
on_err() {
  local ec=$?
  echo "[deploy] ERROR (exit=$ec). Rolling back..."
  # rollback snippet if saved
  if [ -n "${ROLLBACK_SNIPPET_OLD:-}" ]; then
    echo "${ROLLBACK_SNIPPET_OLD}" | sudo tee "${SNIPPET}" >/dev/null || true
    nginx_reload || true
    echo "[deploy] snippet rolled back"
  fi
  # remove new container if exists
  if [ -n "${ROLLBACK_NEW_NAME:-}" ]; then
    docker rm -f "${ROLLBACK_NEW_NAME}" >/dev/null 2>&1 || true
    echo "[deploy] removed new container ${ROLLBACK_NEW_NAME}"
  fi
  exit $ec
}
trap on_err ERR

# ---- preflight -------------------------------------------------------------
disk_guard

# 스니펫이 없으면 현재 살아있는 포트 기준으로 생성
if [ ! -f "${SNIPPET}" ]; then
  echo "[deploy] snippet not found. creating..."
  LP="$(live_port || true)"
  if [ -z "${LP}" ]; then
    # 아무것도 안 살아있으면 BLUE를 기본으로
    write_snippet "${BLUE_PORT}"
  else
    write_snippet "${LP}"
  fi
fi

# 현재 Nginx 업스트림 포트
CURRENT_PORT="$(grep -o '127\.0\.0\.1:[0-9]*' "${SNIPPET}" 2>/dev/null | awk -F: '{print $2}' || true)"
if [ -z "${CURRENT_PORT}" ]; then
  # 스니펫이 비정상일 때 헬스로 추정
  CURRENT_PORT="$(live_port || true)"
fi
[ -z "${CURRENT_PORT}" ] && CURRENT_PORT="${BLUE_PORT}"

# NEW/OLD 포트 & 이름 결정
if [ "${CURRENT_PORT}" = "${BLUE_PORT}" ]; then
  NEW_PORT="${GREEN_PORT}"
  OLD_PORT="${BLUE_PORT}"
  NEW_NAME="${APP_NAME}-green"
  OLD_NAME="${APP_NAME}-blue"
else
  NEW_PORT="${BLUE_PORT}"
  OLD_PORT="${GREEN_PORT}"
  NEW_NAME="${APP_NAME}-blue"
  OLD_NAME="${APP_NAME}-green"
fi

echo "[deploy] current=${CURRENT_PORT} -> new=${NEW_PORT} (old=${OLD_PORT})"
echo "[deploy] new-name=${NEW_NAME} old-name=${OLD_NAME}"

# ---- deploy new ------------------------------------------------------------
echo "[deploy] pulling image..."
docker pull "${IMAGE}"

echo "[deploy] removing stale ${NEW_NAME} (if any)"
docker rm -f "${NEW_NAME}" >/dev/null 2>&1 || true

echo "[deploy] starting ${NEW_NAME} on :${NEW_PORT}"
docker run -d --name "${NEW_NAME}" \
  -p "${NEW_PORT}:3000" \
  --restart=always \
  -e NODE_ENV=production \
  "${IMAGE}"

ROLLBACK_NEW_NAME="${NEW_NAME}"

# health wait
echo -n "[deploy] waiting health ${HEALTH_PATH} on :${NEW_PORT} "
ok=0
for i in $(seq 1 60); do
  if probe_health "${NEW_PORT}"; then ok=1; break; fi
  echo -n "."
  sleep 1
done
echo
if [ "$ok" != "1" ]; then
  echo "[deploy] health check FAILED on :${NEW_PORT}"
  docker logs --tail=200 "${NEW_NAME}" || true
  exit 1
fi
echo "[deploy] health OK on :${NEW_PORT}"

# ---- switch snippet (atomic) -----------------------------------------------
# 스니펫 교체 전 기존 내용을 저장(롤백 대비)
ROLLBACK_SNIPPET_OLD="$(cat "${SNIPPET}" 2>/dev/null || echo "")"

write_snippet "${NEW_PORT}"
nginx_reload
echo "[deploy] nginx switched to :${NEW_PORT}"

# ---- retire old ------------------------------------------------------------
docker rm -f "${OLD_NAME}" >/dev/null 2>&1 || true

# ---- cleanup optional ------------------------------------------------------
docker image prune -f >/dev/null 2>&1 || true

# success: 롤백 정보 해제
ROLLBACK_NEW_NAME=""
ROLLBACK_SNIPPET_OLD=""
echo "[deploy] done"
