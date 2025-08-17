#!/usr/bin/env bash
set -euo pipefail

BLUE_PORT="${BLUE_PORT:-3000}"
GREEN_PORT="${GREEN_PORT:-3001}"
SNIPPET="/etc/nginx/snippets/ibmall-upstream.conf"

CURRENT_PORT="$(grep -o '127\.0\.0\.1:[0-9]*' "$SNIPPET" 2>/dev/null | awk -F: '{print $2}' || true)"
if [[ "${CURRENT_PORT:-$BLUE_PORT}" == "$BLUE_PORT" ]]; then
  TARGET="$GREEN_PORT"
else
  TARGET="$BLUE_PORT"
fi

echo "[rollback] switch upstream to :$TARGET"
sudo sed -i "s|127\.0\.0\.1:[0-9]*|127.0.0.1:${TARGET}|g" "$SNIPPET"
sudo nginx -t
sudo nginx -s reload
echo "[rollback] done (now :$TARGET)"