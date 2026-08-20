#!/usr/bin/env sh

set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)

cd "$PROJECT_DIR"

exec sh mvnw -pl nnm-app -am -Dmaven.test.skip=true spring-boot:run "$@"
