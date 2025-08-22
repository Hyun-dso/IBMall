#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   deploy.sh <image> <color>
#     color: blue | green
#
# Example:
#   deploy.sh ghcr.io/owner/ibmall-backend:abcd1234 green

IMAGE="${1:?Usage: deploy.sh <image> <color>}"
COLOR="${2:?Usage: deploy.sh <image> <color> (blue|green)}"

SERVICE_BASE="${SERVICE_BASE:-ibmall-backend}"
ENV_FILE="${ENV_FILE:-/home/ubuntu/env/backend.prod}"
DATA_ROOT="${DATA_ROOT:-/home/ubuntu/upload}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
HEALTH_PATH="${HEALTH_PATH:-/actuator/health}"

case "$COLOR" in
  blue)  PORT=8080; NAME="${SERVICE_BASE}-blue" ;;
  green) PORT=8081; NAME="${SERVICE_BASE}-green" ;;
  *) echo "color must be blue or green"; exit 2 ;;
esac

echo "[deploy-backend] image=$IMAGE color=$COLOR port=$PORT health=$HEALTH_PATH name=$NAME"

# Pull image
sudo docker pull "$IMAGE"

# Prepare data dirs (bind mount, e.g., EFS path)
sudo mkdir -p "${DATA_ROOT}/productImg" "${DATA_ROOT}/messageImg"

# Stop & remove existing same-color container if exists
if sudo docker ps -a --format '{{.Names}}' | grep -q "^${NAME}$"; then
  echo "[deploy-backend] removing old container: $NAME"
  sudo docker rm -f "$NAME" >/dev/null 2>&1 || true
fi

# Run new same-color container on its fixed port
sudo docker run -d --name "$NAME" -p ${PORT}:8080 \
  --restart=always \
  --env-file "$ENV_FILE" \
  -e SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE}" \
  -v "${DATA_ROOT}:${DATA_ROOT}:rw" \
  "$IMAGE"

# Local health check
echo -n "[deploy-backend] waiting health on :$PORT "
for i in {1..60}; do
  if curl -fsS "http://127.0.0.1:${PORT}${HEALTH_PATH}" >/dev/null 2>&1; then
    echo "OK"; OK=1; break
  fi
  echo -n "."; sleep 1
done
[ -n "${OK:-}" ] || { echo >&2 "[deploy-backend] health check FAILED"; exit 1; }

# Optional: prune dangling images
sudo docker image prune -f >/dev/null 2>&1 || true
echo "[deploy-backend] done ($COLOR @ :$PORT is UP)"
