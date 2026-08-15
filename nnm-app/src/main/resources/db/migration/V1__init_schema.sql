-- NnmBot development schema baseline.
--
-- Development policy: keep the current database shape in this single file.
-- Before production release, split changes into versioned migrations.

CREATE SCHEMA IF NOT EXISTS nnmbot;

CREATE TABLE IF NOT EXISTS nnmbot.sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(100),
    phone       VARCHAR(30),
    email       VARCHAR(120),
    gender      VARCHAR(20),
    avatar      VARCHAR(500),
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nnmbot.bot_admin (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    bot_id      BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nnmbot.mission_subscribe (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT NOT NULL,
    type        VARCHAR(50) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nnmbot.notification_history (
    id          BIGSERIAL PRIMARY KEY,
    event_type  VARCHAR(50) NOT NULL,
    event_id    VARCHAR(100) NOT NULL,
    group_id    BIGINT NOT NULL,
    sent_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_type, event_id, group_id)
);

CREATE TABLE IF NOT EXISTS nnmbot.alias (
    id          BIGSERIAL PRIMARY KEY,
    cn_name     VARCHAR(200) NOT NULL,
    en_name     VARCHAR(200) NOT NULL,
    alias_name  VARCHAR(200) NOT NULL,
    UNIQUE (alias_name)
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_role (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    data_scope  VARCHAR(20) NOT NULL DEFAULT 'SELF',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_menu (
    id          BIGSERIAL PRIMARY KEY,
    parent_id   BIGINT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    path        VARCHAR(255) NOT NULL,
    component   VARCHAR(255),
    title       VARCHAR(100) NOT NULL,
    icon        VARCHAR(100),
    sort_order  INTEGER NOT NULL DEFAULT 0,
    keep_alive  BOOLEAN NOT NULL DEFAULT FALSE,
    visible     BOOLEAN NOT NULL DEFAULT TRUE,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    fixed_tab   BOOLEAN NOT NULL DEFAULT FALSE,
    hide_tab    BOOLEAN NOT NULL DEFAULT FALSE,
    full_page   BOOLEAN NOT NULL DEFAULT FALSE,
    iframe      BOOLEAN NOT NULL DEFAULT FALSE,
    link        VARCHAR(500),
    active_path VARCHAR(255),
    text_badge  VARCHAR(50),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sys_menu_parent FOREIGN KEY (parent_id) REFERENCES nnmbot.sys_menu (id)
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_permission (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(20) NOT NULL,
    menu_id     BIGINT,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sys_permission_menu FOREIGN KEY (menu_id) REFERENCES nnmbot.sys_menu (id)
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_user_role (
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_sys_user_role_user FOREIGN KEY (user_id) REFERENCES nnmbot.sys_user (id),
    CONSTRAINT fk_sys_user_role_role FOREIGN KEY (role_id) REFERENCES nnmbot.sys_role (id)
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_sys_role_permission_role FOREIGN KEY (role_id) REFERENCES nnmbot.sys_role (id),
    CONSTRAINT fk_sys_role_permission_permission FOREIGN KEY (permission_id) REFERENCES nnmbot.sys_permission (id)
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_role_menu (
    role_id     BIGINT NOT NULL,
    menu_id     BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id),
    CONSTRAINT fk_sys_role_menu_role FOREIGN KEY (role_id) REFERENCES nnmbot.sys_role (id),
    CONSTRAINT fk_sys_role_menu_menu FOREIGN KEY (menu_id) REFERENCES nnmbot.sys_menu (id)
);

CREATE TABLE IF NOT EXISTS nnmbot.sys_operation_log (
    id            BIGSERIAL PRIMARY KEY,
    operator_id   BIGINT,
    operator_name VARCHAR(50) NOT NULL,
    action        VARCHAR(80) NOT NULL,
    target_type   VARCHAR(50) NOT NULL,
    target_id     VARCHAR(100),
    detail        VARCHAR(1000),
    ip_address    VARCHAR(64),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_menu_parent_id
    ON nnmbot.sys_menu (parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_permission_menu_id
    ON nnmbot.sys_permission (menu_id);
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_created_at
    ON nnmbot.sys_operation_log (created_at);
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_operator_id
    ON nnmbot.sys_operation_log (operator_id);
