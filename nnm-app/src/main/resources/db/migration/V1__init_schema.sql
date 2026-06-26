-- Initial schema for NnmBot
-- Flyway V1: base tables

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bot_admin (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    bot_id      BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mission_subscribe (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT NOT NULL,
    type        VARCHAR(50) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_history (
    id          BIGSERIAL PRIMARY KEY,
    event_type  VARCHAR(50) NOT NULL,
    event_id    VARCHAR(100) NOT NULL,
    group_id    BIGINT NOT NULL,
    sent_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_type, event_id, group_id)
);

CREATE TABLE IF NOT EXISTS alias (
    id          BIGSERIAL PRIMARY KEY,
    cn_name     VARCHAR(200) NOT NULL,
    en_name     VARCHAR(200) NOT NULL,
    alias_name  VARCHAR(200) NOT NULL,
    UNIQUE (alias_name)
);
