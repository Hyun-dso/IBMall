#!/usr/bin/env bash
set -euo pipefail

# Usage: deploy.sh <image> <color>   # color: blue|green
IMAGE="${1:?Usage: deploy.sh <image> <color>}"
COLOR="${2:?Usage: deploy.sh <image> <color> (blue|green)}"

SERVICE_BASE="${SERVICE_BASE:-ibmall-backend}"
ENV_FILE="${ENV_FILE:-/home/ubuntu/env/backend.prod}"
DATA_ROOT="${DATA_ROOT:-/home/ubuntu/upload}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
HEALTH_PATH="${HEALTH_PATH:-/actuator/health}"

# Health wait configs (tunable via env)
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-120}"   # seconds
HEALTH_INTERVAL="${HEALTH_INTERVAL:-2}"   # seconds

case "$COLOR" in
  blue)  PORT=8080; NAME="${SERVICE_BASE}-blue" ;;
  green) PORT=8081; NAME="${SERVICE_BASE}-green" ;;
  *) echo "color must be blue or green"; exit 2 ;;
esac

echo "[deploy-backend] image=$IMAGE color=$COLOR port=$PORT health=$HEALTH_PATH name=$NAME"

# Always pull latest of the tag
sudo docker pull "$IMAGE"

# Ensure bind-mount dirs exist
sudo mkdir -p "${DATA_ROOT}/productImg" "${DATA_ROOT}/messageImg"

# Remove same-color container if exists
if sudo docker ps -a --format '{{.Names}}' | grep -q "^${NAME}$"; then
  echo "[deploy-backend] removing old container: $NAME"
  sudo docker rm -f "$NAME" >/dev/null 2>&1 || true
fi

# Run container on fixed host port (8080 for blue, 8081 for green)
CID=$(sudo docker run -d --name "$NAME" -p ${PORT}:8080 \
  --restart=always \
  --env-file "$ENV_FILE" \
  -e SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE}" \
  -v "${DATA_ROOT}:${DATA_ROOT}:rw" \
  --label "app=ibmall-backend" --label "color=${COLOR}" \
  "$IMAGE")
echo "[deploy-backend] started container=$CID"

# Health wait (local)
echo -n "[deploy-backend] waiting health on :$PORT "
deadline=$(( $(date +%s) + HEALTH_TIMEOUT ))
OK=""
while [ $(date +%s) -lt $deadline ]; do
  if curl -fsS --max-time 3 "http://127.0.0.1:${PORT}${HEALTH_PATH}" >/dev/null 2>&1; then
    echo "OK"; OK=1; break
  fi
  echo -n "."; sleep "$HEALTH_INTERVAL"
done

if [ -z "${OK:-}" ]; then
  echo >&2 "[deploy-backend] health check FAILED on :$PORT"
  echo "---- last 200 lines of container logs ($NAME) ----"
  sudo docker logs --tail=200 "$NAME" || true
  exit 1
fi

# Optional: prune dangling images
sudo docker image prune -f >/dev/null 2>&1 || true
echo "[deploy-backend] done ($COLOR @ :$PORT is UP)"
