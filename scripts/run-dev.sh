#!/usr/bin/env sh

set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)

cd "$PROJECT_DIR"
sh mvnw -pl nnm-app -am compile

cd "$PROJECT_DIR/nnm-app"
exec sh ../mvnw spring-boot:run "$@"
