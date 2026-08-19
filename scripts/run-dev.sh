#!/usr/bin/env sh

set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
ENV_FILE=${NNM_ENV_FILE:-"$PROJECT_DIR/.env"}

cd "$PROJECT_DIR"

if [ -f "$ENV_FILE" ]; then
    set -a
    # shellcheck：允许加载由变量指定的环境文件
    . "$ENV_FILE"
    set +a
fi

case "${ONEBOT_WS_URL:-}" in
    ""|ws://127.0.0.1:*|ws://localhost:*|wss://127.0.0.1:*|wss://localhost:*)
        ;;
    *)
        if [ -z "${ONEBOT_ACCESS_TOKEN:-}" ]; then
            printf '%s\n' \
                "WARN: ONEBOT_WS_URL points to a remote host, but ONEBOT_ACCESS_TOKEN is empty." \
                "      SnowLuma will normally reject the WebSocket handshake with HTTP 401." >&2
        fi
        ;;
esac

exec sh mvnw -pl nnm-app -am -Dmaven.test.skip=true spring-boot:run "$@"
