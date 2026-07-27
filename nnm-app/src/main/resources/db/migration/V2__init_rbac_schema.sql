-- RBAC schema for NnmBot admin

CREATE TABLE IF NOT EXISTS nnmbot.sys_role (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
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

CREATE INDEX IF NOT EXISTS idx_sys_menu_parent_id ON nnmbot.sys_menu (parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_permission_menu_id ON nnmbot.sys_permission (menu_id);
