ALTER TABLE nnmbot.sys_user ADD COLUMN IF NOT EXISTS nickname VARCHAR(100);
ALTER TABLE nnmbot.sys_user ADD COLUMN IF NOT EXISTS phone VARCHAR(30);
ALTER TABLE nnmbot.sys_user ADD COLUMN IF NOT EXISTS email VARCHAR(120);
ALTER TABLE nnmbot.sys_user ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE nnmbot.sys_user ADD COLUMN IF NOT EXISTS avatar VARCHAR(500);

ALTER TABLE nnmbot.sys_role ADD COLUMN IF NOT EXISTS data_scope VARCHAR(20) NOT NULL DEFAULT 'SELF';

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

CREATE INDEX IF NOT EXISTS idx_sys_operation_log_created_at
    ON nnmbot.sys_operation_log (created_at);
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_operator_id
    ON nnmbot.sys_operation_log (operator_id);
