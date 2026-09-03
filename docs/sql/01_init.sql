-- NnmBot PostgreSQL 开发数据库初始化脚本。
--
-- 新建数据库时这是唯一需要执行的数据库脚本，包含最终表结构、固定初始化数据，
-- 以及从 pgsql-2026_08_26_21_45_51-dump.sql 整理出的可恢复开发数据。
-- 已有数据库补充 Warframe 结构时，按 docs/sql/README.md 执行尚未应用的升级脚本。
-- 表之间只使用应用层逻辑关联，不创建数据库外键。
-- 机器人凭证仍需使用生成密文时相同的 NNM_BOT_CREDENTIAL_KEY 才能解密。

\set ON_ERROR_STOP on

BEGIN;

CREATE SCHEMA IF NOT EXISTS nnmbot;

CREATE TABLE IF NOT EXISTS nnmbot.bot_channel_instance (
    id             BIGSERIAL PRIMARY KEY,
    platform       VARCHAR(32) NOT NULL,
    instance_id    VARCHAR(100) NOT NULL,
    display_name   VARCHAR(100) NOT NULL,
    bot_account_id VARCHAR(100) NOT NULL,
    enabled        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bot_channel_instance_id UNIQUE (instance_id),
    CONSTRAINT uq_bot_channel_instance_account UNIQUE (platform, bot_account_id),
    CONSTRAINT ck_bot_channel_instance_platform
        CHECK (platform = 'ONEBOT' OR platform = 'QQ_OFFICIAL')
);

COMMENT ON COLUMN nnmbot.bot_channel_instance.id IS '渠道实例主键';
COMMENT ON COLUMN nnmbot.bot_channel_instance.platform IS '渠道平台类型';
COMMENT ON COLUMN nnmbot.bot_channel_instance.instance_id IS '渠道实例业务标识';
COMMENT ON COLUMN nnmbot.bot_channel_instance.display_name IS '渠道实例显示名称';
COMMENT ON COLUMN nnmbot.bot_channel_instance.bot_account_id IS '机器人账号标识';
COMMENT ON COLUMN nnmbot.bot_channel_instance.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_channel_instance.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.bot_channel_instance.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_onebot_config (
    instance_id               BIGINT PRIMARY KEY,
    connection_url            VARCHAR(500) NOT NULL,
    access_token              VARCHAR(2000),
    reconnect_interval_seconds BIGINT NOT NULL DEFAULT 5,
    CONSTRAINT ck_bot_onebot_reconnect_interval
        CHECK (reconnect_interval_seconds > 0)
);

COMMENT ON COLUMN nnmbot.bot_onebot_config.instance_id IS '渠道实例主键，同时关联 bot_channel_instance.id';
COMMENT ON COLUMN nnmbot.bot_onebot_config.connection_url IS 'OneBot 正向 WebSocket 连接地址';
COMMENT ON COLUMN nnmbot.bot_onebot_config.access_token IS 'OneBot 访问令牌密文';
COMMENT ON COLUMN nnmbot.bot_onebot_config.reconnect_interval_seconds IS 'WebSocket 重连间隔，单位：秒';

CREATE TABLE IF NOT EXISTS nnmbot.bot_qq_official_config (
    instance_id                     BIGINT PRIMARY KEY,
    app_id                          VARCHAR(200) NOT NULL,
    client_secret                   VARCHAR(2000) NOT NULL,
    token_url                       VARCHAR(500) NOT NULL,
    api_base_url                    VARCHAR(500) NOT NULL,
    gateway_url                     VARCHAR(500),
    intents                         BIGINT NOT NULL DEFAULT 100663296,
    gateway_connect_timeout_seconds BIGINT NOT NULL DEFAULT 10,
    reconnect_delay_seconds         BIGINT NOT NULL DEFAULT 5,
    CONSTRAINT uq_bot_qq_official_app_id UNIQUE (app_id),
    CONSTRAINT ck_bot_qq_official_gateway_timeout
        CHECK (gateway_connect_timeout_seconds > 0),
    CONSTRAINT ck_bot_qq_official_reconnect_delay
        CHECK (reconnect_delay_seconds >= 0),
    CONSTRAINT ck_bot_qq_official_intents
        CHECK (intents >= 0)
);

COMMENT ON COLUMN nnmbot.bot_qq_official_config.instance_id IS '渠道实例主键，同时关联 bot_channel_instance.id';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.app_id IS 'QQ 官方机器人 AppID';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.client_secret IS 'QQ 官方机器人 Client Secret 密文';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.token_url IS 'QQ 官方机器人访问令牌接口地址';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.api_base_url IS 'QQ 官方机器人接口基础地址';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.gateway_url IS 'QQ 官方机器人 Gateway 地址，为空时自动获取';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.intents IS 'QQ 官方机器人订阅事件权限位';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.gateway_connect_timeout_seconds IS 'Gateway 连接超时时间，单位：秒';
COMMENT ON COLUMN nnmbot.bot_qq_official_config.reconnect_delay_seconds IS 'Gateway 重连延迟，单位：秒';

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

COMMENT ON COLUMN nnmbot.sys_user.id IS '用户主键';
COMMENT ON COLUMN nnmbot.sys_user.username IS '登录用户名';
COMMENT ON COLUMN nnmbot.sys_user.password IS '密码哈希';
COMMENT ON COLUMN nnmbot.sys_user.nickname IS '用户昵称';
COMMENT ON COLUMN nnmbot.sys_user.phone IS '手机号码';
COMMENT ON COLUMN nnmbot.sys_user.email IS '电子邮箱';
COMMENT ON COLUMN nnmbot.sys_user.gender IS '性别';
COMMENT ON COLUMN nnmbot.sys_user.avatar IS '头像地址';
COMMENT ON COLUMN nnmbot.sys_user.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.sys_user.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.sys_user.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_admin (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    bot_id      BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN nnmbot.bot_admin.id IS '管理员关联主键';
COMMENT ON COLUMN nnmbot.bot_admin.user_id IS '用户主键';
COMMENT ON COLUMN nnmbot.bot_admin.bot_id IS '机器人主键';
COMMENT ON COLUMN nnmbot.bot_admin.created_at IS '创建时间';

CREATE TABLE IF NOT EXISTS nnmbot.mission_subscribe (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT NOT NULL,
    type        VARCHAR(50) NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN nnmbot.mission_subscribe.id IS '订阅主键';
COMMENT ON COLUMN nnmbot.mission_subscribe.group_id IS '群聊标识';
COMMENT ON COLUMN nnmbot.mission_subscribe.type IS '订阅类型';
COMMENT ON COLUMN nnmbot.mission_subscribe.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.mission_subscribe.created_at IS '创建时间';

CREATE TABLE IF NOT EXISTS nnmbot.notification_history (
    id          BIGSERIAL PRIMARY KEY,
    event_type  VARCHAR(50) NOT NULL,
    event_id    VARCHAR(100) NOT NULL,
    group_id    BIGINT NOT NULL,
    sent_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_type, event_id, group_id)
);

COMMENT ON COLUMN nnmbot.notification_history.id IS '通知历史主键';
COMMENT ON COLUMN nnmbot.notification_history.event_type IS '事件类型';
COMMENT ON COLUMN nnmbot.notification_history.event_id IS '外部事件标识';
COMMENT ON COLUMN nnmbot.notification_history.group_id IS '群聊标识';
COMMENT ON COLUMN nnmbot.notification_history.sent_at IS '发送时间';

CREATE TABLE IF NOT EXISTS nnmbot.alias (
    id          BIGSERIAL PRIMARY KEY,
    cn_name     VARCHAR(200) NOT NULL,
    en_name     VARCHAR(200) NOT NULL,
    alias_name  VARCHAR(200) NOT NULL,
    UNIQUE (alias_name)
);

COMMENT ON COLUMN nnmbot.alias.id IS '别名主键';
COMMENT ON COLUMN nnmbot.alias.cn_name IS '中文名称';
COMMENT ON COLUMN nnmbot.alias.en_name IS '英文名称';
COMMENT ON COLUMN nnmbot.alias.alias_name IS '别名';

CREATE TABLE IF NOT EXISTS nnmbot.sys_role (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    authority_level INTEGER NOT NULL DEFAULT 0,
    data_scope  VARCHAR(20) NOT NULL DEFAULT 'SELF',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN nnmbot.sys_role.id IS '角色主键';
COMMENT ON COLUMN nnmbot.sys_role.code IS '角色编码';
COMMENT ON COLUMN nnmbot.sys_role.name IS '角色名称';
COMMENT ON COLUMN nnmbot.sys_role.description IS '角色描述';
COMMENT ON COLUMN nnmbot.sys_role.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.sys_role.authority_level IS '角色权限等级，数值越大权限越高';
COMMENT ON COLUMN nnmbot.sys_role.data_scope IS '数据范围';
COMMENT ON COLUMN nnmbot.sys_role.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.sys_role.updated_at IS '最后更新时间';

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
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN nnmbot.sys_menu.id IS '菜单主键';
COMMENT ON COLUMN nnmbot.sys_menu.parent_id IS '父菜单主键';
COMMENT ON COLUMN nnmbot.sys_menu.name IS '菜单唯一名称';
COMMENT ON COLUMN nnmbot.sys_menu.path IS '前端路由路径';
COMMENT ON COLUMN nnmbot.sys_menu.component IS '前端路由组件';
COMMENT ON COLUMN nnmbot.sys_menu.title IS '菜单标题';
COMMENT ON COLUMN nnmbot.sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN nnmbot.sys_menu.sort_order IS '排序值';
COMMENT ON COLUMN nnmbot.sys_menu.keep_alive IS '是否缓存页面';
COMMENT ON COLUMN nnmbot.sys_menu.visible IS '是否在菜单中可见';
COMMENT ON COLUMN nnmbot.sys_menu.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.sys_menu.fixed_tab IS '是否固定标签页';
COMMENT ON COLUMN nnmbot.sys_menu.hide_tab IS '是否隐藏标签页';
COMMENT ON COLUMN nnmbot.sys_menu.full_page IS '是否使用整页布局';
COMMENT ON COLUMN nnmbot.sys_menu.iframe IS '是否以内嵌页面打开';
COMMENT ON COLUMN nnmbot.sys_menu.link IS '外部链接地址';
COMMENT ON COLUMN nnmbot.sys_menu.active_path IS '激活菜单路径';
COMMENT ON COLUMN nnmbot.sys_menu.text_badge IS '菜单文字徽标';
COMMENT ON COLUMN nnmbot.sys_menu.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.sys_menu.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.sys_permission (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(20) NOT NULL,
    menu_id     BIGINT,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN nnmbot.sys_permission.id IS '权限主键';
COMMENT ON COLUMN nnmbot.sys_permission.code IS '权限编码';
COMMENT ON COLUMN nnmbot.sys_permission.name IS '权限名称';
COMMENT ON COLUMN nnmbot.sys_permission.type IS '权限类型';
COMMENT ON COLUMN nnmbot.sys_permission.menu_id IS '所属菜单主键';
COMMENT ON COLUMN nnmbot.sys_permission.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.sys_permission.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.sys_permission.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.sys_user_role (
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON COLUMN nnmbot.sys_user_role.user_id IS '用户主键';
COMMENT ON COLUMN nnmbot.sys_user_role.role_id IS '角色主键';

CREATE TABLE IF NOT EXISTS nnmbot.sys_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

COMMENT ON COLUMN nnmbot.sys_role_permission.role_id IS '角色主键';
COMMENT ON COLUMN nnmbot.sys_role_permission.permission_id IS '权限主键';

CREATE TABLE IF NOT EXISTS nnmbot.sys_role_menu (
    role_id     BIGINT NOT NULL,
    menu_id     BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

COMMENT ON COLUMN nnmbot.sys_role_menu.role_id IS '角色主键';
COMMENT ON COLUMN nnmbot.sys_role_menu.menu_id IS '菜单主键';

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

COMMENT ON COLUMN nnmbot.sys_operation_log.id IS '操作日志主键';
COMMENT ON COLUMN nnmbot.sys_operation_log.operator_id IS '操作人用户主键';
COMMENT ON COLUMN nnmbot.sys_operation_log.operator_name IS '操作人名称';
COMMENT ON COLUMN nnmbot.sys_operation_log.action IS '操作动作编码';
COMMENT ON COLUMN nnmbot.sys_operation_log.target_type IS '目标对象类型';
COMMENT ON COLUMN nnmbot.sys_operation_log.target_id IS '目标对象标识';
COMMENT ON COLUMN nnmbot.sys_operation_log.detail IS '操作详情';
COMMENT ON COLUMN nnmbot.sys_operation_log.ip_address IS '操作人 IP 地址';
COMMENT ON COLUMN nnmbot.sys_operation_log.created_at IS '操作时间';

CREATE INDEX IF NOT EXISTS idx_sys_menu_parent_id
    ON nnmbot.sys_menu (parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_permission_menu_id
    ON nnmbot.sys_permission (menu_id);
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_created_at
    ON nnmbot.sys_operation_log (created_at);
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_operator_id
    ON nnmbot.sys_operation_log (operator_id);

-- Command, output, identity and scope schema.
-- Command configuration, channel capability and cross-platform scope model.

CREATE TABLE IF NOT EXISTS nnmbot.bot_command (
    id                   BIGSERIAL PRIMARY KEY,
    code                 VARCHAR(80) NOT NULL UNIQUE,
    name                 VARCHAR(100) NOT NULL,
    game_code            VARCHAR(40) NOT NULL DEFAULT 'system',
    description          VARCHAR(255),
    enabled              BOOLEAN NOT NULL DEFAULT TRUE,
    show_in_help         BOOLEAN NOT NULL DEFAULT TRUE,
    output_mode          VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    permission_override  VARCHAR(100),
    group_role_required  VARCHAR(20),
    cooldown_seconds     BIGINT NOT NULL DEFAULT 0,
    mention_invoker      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_bot_command_output_mode
        CHECK (output_mode IN ('NONE', 'TEXT', 'IMAGE', 'TEXT_IMAGE', 'FILE', 'TEXT_FILE')),
    CONSTRAINT ck_bot_command_group_role
        CHECK (group_role_required IS NULL OR group_role_required IN ('OWNER', 'ADMIN')),
    CONSTRAINT ck_bot_command_cooldown
        CHECK (cooldown_seconds >= 0)
);

COMMENT ON COLUMN nnmbot.bot_command.id IS '命令配置主键';
COMMENT ON COLUMN nnmbot.bot_command.code IS '与 Java 插件稳定对应的命令编码';
COMMENT ON COLUMN nnmbot.bot_command.name IS '命令显示名称';
COMMENT ON COLUMN nnmbot.bot_command.game_code IS '命令所属游戏或业务域';
COMMENT ON COLUMN nnmbot.bot_command.description IS '命令说明';
COMMENT ON COLUMN nnmbot.bot_command.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_command.show_in_help IS '是否显示在帮助菜单';
COMMENT ON COLUMN nnmbot.bot_command.output_mode IS '默认输出模式：NONE、TEXT、IMAGE、TEXT_IMAGE';
COMMENT ON COLUMN nnmbot.bot_command.permission_override IS '覆盖插件默认权限码';
COMMENT ON COLUMN nnmbot.bot_command.group_role_required IS '群组角色要求：OWNER、ADMIN；为空表示不限制群组角色';
COMMENT ON COLUMN nnmbot.bot_command.cooldown_seconds IS '冷却时间，单位：秒';
COMMENT ON COLUMN nnmbot.bot_command.mention_invoker IS '是否在支持提及能力的渠道回复中提及命令触发者';
COMMENT ON COLUMN nnmbot.bot_command.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.bot_command.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_command_alias (
    id               BIGSERIAL PRIMARY KEY,
    command_id       BIGINT NOT NULL,
    alias_name       VARCHAR(100) NOT NULL,
    boundary_policy  VARCHAR(30) NOT NULL DEFAULT 'WHITESPACE_REQUIRED',
    -- 限制运行时只读取受支持的参数边界策略。
    CONSTRAINT ck_bot_command_alias_boundary_policy
        CHECK (boundary_policy IN ('WHITESPACE_REQUIRED', 'CJK_OR_WHITESPACE', 'NONE')),
    CONSTRAINT uq_bot_command_alias UNIQUE (command_id, alias_name)
);

COMMENT ON COLUMN nnmbot.bot_command_alias.command_id IS '命令配置主键';
COMMENT ON COLUMN nnmbot.bot_command_alias.alias_name IS '命令自定义别名';
COMMENT ON COLUMN nnmbot.bot_command_alias.boundary_policy IS '前缀命令参数边界策略：必须空格、CJK或空格、无限制';
COMMENT ON COLUMN nnmbot.bot_command_alias.id IS '命令别名主键';

CREATE TABLE IF NOT EXISTS nnmbot.bot_command_template (
    id            BIGSERIAL PRIMARY KEY,
    template_code VARCHAR(80) NOT NULL UNIQUE,
    name          VARCHAR(100) NOT NULL,
    command_id    BIGINT,
    sort_order    INTEGER NOT NULL DEFAULT 0,
    html_content  TEXT NOT NULL,
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN nnmbot.bot_command_template.id IS 'HTML 模板主键';
COMMENT ON COLUMN nnmbot.bot_command_template.template_code IS '模板业务编码';
COMMENT ON COLUMN nnmbot.bot_command_template.name IS '模板名称';
COMMENT ON COLUMN nnmbot.bot_command_template.command_id IS '所属命令主键，使用应用层逻辑关联，不建立数据库外键';
COMMENT ON COLUMN nnmbot.bot_command_template.sort_order IS '同一命令下模板的默认渲染和展示顺序';
COMMENT ON COLUMN nnmbot.bot_command_template.html_content IS '模板 HTML 内容，变量由服务端白名单控制';
COMMENT ON COLUMN nnmbot.bot_command_template.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_command_template.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.bot_command_template.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_command_channel (
    id          BIGSERIAL PRIMARY KEY,
    command_id  BIGINT NOT NULL,
    platform    VARCHAR(32) NOT NULL,
    output_mode VARCHAR(20) NOT NULL,
    CONSTRAINT uq_bot_command_channel UNIQUE (command_id, platform),
    CONSTRAINT ck_bot_command_channel_output_mode
        CHECK (output_mode IN ('NONE', 'TEXT', 'IMAGE', 'TEXT_IMAGE', 'FILE', 'TEXT_FILE', 'MARKDOWN'))
);

COMMENT ON COLUMN nnmbot.bot_command_channel.command_id IS '命令配置主键';
COMMENT ON COLUMN nnmbot.bot_command_channel.platform IS '渠道编码，如 ONEBOT、QQ_OFFICIAL、KOOK';
COMMENT ON COLUMN nnmbot.bot_command_channel.output_mode IS '该渠道的输出模式';
COMMENT ON COLUMN nnmbot.bot_command_channel.id IS '命令渠道输出配置主键';

CREATE TABLE IF NOT EXISTS nnmbot.bot_command_role (
    id          BIGSERIAL PRIMARY KEY,
    command_id  BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    CONSTRAINT uq_bot_command_role UNIQUE (command_id, role_id)
);

COMMENT ON COLUMN nnmbot.bot_command_role.command_id IS '命令配置主键';
COMMENT ON COLUMN nnmbot.bot_command_role.role_id IS '系统角色主键';
COMMENT ON COLUMN nnmbot.bot_command_role.id IS '命令与系统角色关联主键';

CREATE TABLE IF NOT EXISTS nnmbot.sys_user_identity (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    platform          VARCHAR(32) NOT NULL,
    external_user_id  VARCHAR(160) NOT NULL,
    CONSTRAINT uq_sys_user_identity UNIQUE (platform, external_user_id)
);

COMMENT ON COLUMN nnmbot.sys_user_identity.user_id IS '系统用户主键';
COMMENT ON COLUMN nnmbot.sys_user_identity.platform IS '身份所属渠道，如 QQ、KOOK';
COMMENT ON COLUMN nnmbot.sys_user_identity.external_user_id IS '渠道侧用户标识';
COMMENT ON COLUMN nnmbot.sys_user_identity.id IS '系统用户外部身份主键';

CREATE TABLE IF NOT EXISTS nnmbot.bot_scope (
    id              BIGSERIAL PRIMARY KEY,
    platform        VARCHAR(32) NOT NULL,
    scope_type      VARCHAR(20) NOT NULL,
    scope_key       VARCHAR(160) NOT NULL,
    bot_instance_id BIGINT NOT NULL,
    display_name    VARCHAR(160),
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_bot_scope UNIQUE (platform, scope_type, scope_key, bot_instance_id)
);

COMMENT ON COLUMN nnmbot.bot_scope.platform IS '作用域所属渠道';
COMMENT ON COLUMN nnmbot.bot_scope.scope_type IS '作用域类型：GROUP、CHANNEL 等';
COMMENT ON COLUMN nnmbot.bot_scope.scope_key IS '渠道侧作用域标识，使用字符串兼容不同平台';
COMMENT ON COLUMN nnmbot.bot_scope.bot_instance_id IS '所属机器人实例';
COMMENT ON COLUMN nnmbot.bot_scope.display_name IS '作用域显示名称';
COMMENT ON COLUMN nnmbot.bot_scope.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_scope.id IS '机器人作用域主键';

CREATE TABLE IF NOT EXISTS nnmbot.bot_scope_member (
    id                BIGSERIAL PRIMARY KEY,
    scope_id          BIGINT NOT NULL,
    user_id           BIGINT NOT NULL,
    platform_user_id  VARCHAR(160) NOT NULL,
    member_role       VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    enabled           BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_bot_scope_member UNIQUE (scope_id, platform_user_id),
    CONSTRAINT ck_bot_scope_member_role
        CHECK (member_role IN ('OWNER', 'ADMIN', 'MEMBER'))
);

COMMENT ON COLUMN nnmbot.bot_scope_member.scope_id IS '机器人作用域主键';
COMMENT ON COLUMN nnmbot.bot_scope_member.user_id IS '绑定的系统用户主键';
COMMENT ON COLUMN nnmbot.bot_scope_member.platform_user_id IS '渠道侧成员标识';
COMMENT ON COLUMN nnmbot.bot_scope_member.member_role IS '作用域角色：OWNER、ADMIN、MEMBER';
COMMENT ON COLUMN nnmbot.bot_scope_member.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_scope_member.id IS '作用域成员主键';

CREATE INDEX IF NOT EXISTS idx_bot_command_game
    ON nnmbot.bot_command (game_code);
CREATE INDEX IF NOT EXISTS idx_bot_command_template_command_order
    ON nnmbot.bot_command_template (command_id, sort_order, id);
CREATE INDEX IF NOT EXISTS idx_bot_scope_instance
    ON nnmbot.bot_scope (bot_instance_id);
CREATE INDEX IF NOT EXISTS idx_bot_scope_member_user
    ON nnmbot.bot_scope_member (user_id);

-- Warframe 官方静态数据按领域保存。基础表保留公共身份和完整原始 JSON，
-- 领域详情表只保存高频查询字段，表之间仅通过 official_record_id 逻辑关联。
CREATE TABLE IF NOT EXISTS nnmbot.wf_official_export_record (
    id                 BIGSERIAL PRIMARY KEY,
    unique_name        VARCHAR(500) NOT NULL,
    export_file        VARCHAR(260) NOT NULL,
    export_type        VARCHAR(120) NOT NULL,
    record_type        VARCHAR(160),
    official_name      VARCHAR(500),
    localized_name     VARCHAR(500),
    parent_name        VARCHAR(500),
    product_category   VARCHAR(160),
    exclude_from_codex BOOLEAN,
    description        TEXT,
    long_description   TEXT,
    wiki_slug          VARCHAR(300),
    market_slug        VARCHAR(300),
    raw_json           TEXT NOT NULL,
    localized_raw_json TEXT,
    enabled            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 相同 uniqueName 允许出现在不同 Export 类型中。
    CONSTRAINT uq_wf_official_export_identity
        UNIQUE (export_type, unique_name)
);

COMMENT ON TABLE nnmbot.wf_official_export_record IS 'Warframe 官方 PublicExport 公共身份、名称及完整双语原始 JSON';
COMMENT ON COLUMN nnmbot.wf_official_export_record.id IS '官方记录主键';
COMMENT ON COLUMN nnmbot.wf_official_export_record.unique_name IS '官方 PublicExport uniqueName，与 export_type 共同构成业务身份';
COMMENT ON COLUMN nnmbot.wf_official_export_record.export_file IS '记录所属 PublicExport 文件名';
COMMENT ON COLUMN nnmbot.wf_official_export_record.export_type IS '官方导出类型，与 unique_name 共同构成业务身份';
COMMENT ON COLUMN nnmbot.wf_official_export_record.record_type IS 'JSON 记录的具体类型';
COMMENT ON COLUMN nnmbot.wf_official_export_record.official_name IS '官方英文名称';
COMMENT ON COLUMN nnmbot.wf_official_export_record.localized_name IS '官方本地化名称';
COMMENT ON COLUMN nnmbot.wf_official_export_record.parent_name IS '官方父级名称';
COMMENT ON COLUMN nnmbot.wf_official_export_record.product_category IS '官方产品分类';
COMMENT ON COLUMN nnmbot.wf_official_export_record.exclude_from_codex IS '是否排除在 Codex 之外';
COMMENT ON COLUMN nnmbot.wf_official_export_record.description IS '官方短描述';
COMMENT ON COLUMN nnmbot.wf_official_export_record.long_description IS '官方长描述';
COMMENT ON COLUMN nnmbot.wf_official_export_record.wiki_slug IS '灰机 Wiki 页面路径';
COMMENT ON COLUMN nnmbot.wf_official_export_record.market_slug IS 'Warframe Market slug';
COMMENT ON COLUMN nnmbot.wf_official_export_record.raw_json IS '官方英文原始 JSON';
COMMENT ON COLUMN nnmbot.wf_official_export_record.localized_raw_json IS '官方本地化原始 JSON';
COMMENT ON COLUMN nnmbot.wf_official_export_record.enabled IS '是否参与运行时解析';
COMMENT ON COLUMN nnmbot.wf_official_export_record.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_official_export_record.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_official_weapon (
    official_record_id   BIGINT PRIMARY KEY,
    mastery_req          INTEGER,
    total_damage         DOUBLE PRECISION,
    damage_per_shot_json TEXT,
    critical_chance      DOUBLE PRECISION,
    critical_multiplier  DOUBLE PRECISION,
    proc_chance          DOUBLE PRECISION,
    fire_rate            DOUBLE PRECISION,
    accuracy             DOUBLE PRECISION,
    magazine_size        INTEGER,
    reload_time          DOUBLE PRECISION,
    multishot            DOUBLE PRECISION,
    slot                 INTEGER,
    noise                VARCHAR(120),
    trigger              VARCHAR(120),
    omega_attenuation    DOUBLE PRECISION,
    range_json           TEXT,
    slam_attack          DOUBLE PRECISION,
    slide_attack         DOUBLE PRECISION,
    wind_up              DOUBLE PRECISION,
    level_stats_json     TEXT,
    ingredients_json     TEXT,
    tags_json            TEXT
);

COMMENT ON TABLE nnmbot.wf_official_weapon IS 'Warframe 官方武器高频查询字段';
COMMENT ON COLUMN nnmbot.wf_official_weapon.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_official_weapon.mastery_req IS '武器段位要求';
COMMENT ON COLUMN nnmbot.wf_official_weapon.total_damage IS '武器总伤害';
COMMENT ON COLUMN nnmbot.wf_official_weapon.damage_per_shot_json IS '武器分段伤害 JSON';
COMMENT ON COLUMN nnmbot.wf_official_weapon.critical_chance IS '武器暴击几率';
COMMENT ON COLUMN nnmbot.wf_official_weapon.critical_multiplier IS '武器暴击倍率';
COMMENT ON COLUMN nnmbot.wf_official_weapon.proc_chance IS '武器触发几率';
COMMENT ON COLUMN nnmbot.wf_official_weapon.fire_rate IS '武器射速';
COMMENT ON COLUMN nnmbot.wf_official_weapon.accuracy IS '武器精准度';
COMMENT ON COLUMN nnmbot.wf_official_weapon.magazine_size IS '武器弹匣容量';
COMMENT ON COLUMN nnmbot.wf_official_weapon.reload_time IS '武器换弹时间';
COMMENT ON COLUMN nnmbot.wf_official_weapon.multishot IS '武器多重射击';
COMMENT ON COLUMN nnmbot.wf_official_weapon.slot IS '武器装备槽位';
COMMENT ON COLUMN nnmbot.wf_official_weapon.noise IS '武器噪声类型';
COMMENT ON COLUMN nnmbot.wf_official_weapon.trigger IS '武器扳机类型';
COMMENT ON COLUMN nnmbot.wf_official_weapon.omega_attenuation IS '武器 Omega 衰减系数';
COMMENT ON COLUMN nnmbot.wf_official_weapon.range_json IS '武器射程结构 JSON';
COMMENT ON COLUMN nnmbot.wf_official_weapon.slam_attack IS '武器震地攻击数值';
COMMENT ON COLUMN nnmbot.wf_official_weapon.slide_attack IS '武器滑行攻击数值';
COMMENT ON COLUMN nnmbot.wf_official_weapon.wind_up IS '武器蓄力时间';
COMMENT ON COLUMN nnmbot.wf_official_weapon.level_stats_json IS '武器等级属性 JSON';
COMMENT ON COLUMN nnmbot.wf_official_weapon.ingredients_json IS '武器制造材料 JSON';
COMMENT ON COLUMN nnmbot.wf_official_weapon.tags_json IS '武器标签 JSON';

CREATE TABLE IF NOT EXISTS nnmbot.wf_official_warframe (
    official_record_id BIGINT PRIMARY KEY,
    health             INTEGER,
    shield             INTEGER,
    armor              INTEGER,
    stamina            INTEGER,
    power              INTEGER,
    mastery_req        INTEGER,
    sprint_speed       DOUBLE PRECISION,
    codex_secret       BOOLEAN,
    abilities_json     TEXT,
    exalted_json       TEXT,
    stats_json         TEXT,
    tags_json          TEXT
);

COMMENT ON TABLE nnmbot.wf_official_warframe IS 'Warframe 官方战甲高频查询字段';
COMMENT ON COLUMN nnmbot.wf_official_warframe.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_official_warframe.health IS '战甲生命值';
COMMENT ON COLUMN nnmbot.wf_official_warframe.shield IS '战甲护盾值';
COMMENT ON COLUMN nnmbot.wf_official_warframe.armor IS '战甲护甲值';
COMMENT ON COLUMN nnmbot.wf_official_warframe.stamina IS '战甲体力值';
COMMENT ON COLUMN nnmbot.wf_official_warframe.power IS '战甲能量值';
COMMENT ON COLUMN nnmbot.wf_official_warframe.mastery_req IS '战甲段位要求';
COMMENT ON COLUMN nnmbot.wf_official_warframe.sprint_speed IS '战甲冲刺速度';
COMMENT ON COLUMN nnmbot.wf_official_warframe.codex_secret IS '是否为 Codex 隐藏记录';
COMMENT ON COLUMN nnmbot.wf_official_warframe.abilities_json IS '战甲技能数组 JSON';
COMMENT ON COLUMN nnmbot.wf_official_warframe.exalted_json IS '战甲显赫武器数据 JSON';
COMMENT ON COLUMN nnmbot.wf_official_warframe.stats_json IS '战甲扩展属性 JSON';
COMMENT ON COLUMN nnmbot.wf_official_warframe.tags_json IS '战甲标签 JSON';

CREATE TABLE IF NOT EXISTS nnmbot.wf_official_upgrade (
    official_record_id        BIGINT PRIMARY KEY,
    base_drain                INTEGER,
    fusion_limit              INTEGER,
    compat_name               VARCHAR(300),
    type                      VARCHAR(160),
    subtype                   VARCHAR(160),
    polarity                  VARCHAR(120),
    rarity                    VARCHAR(120),
    is_utility                BOOLEAN,
    is_augment                BOOLEAN,
    is_exilus                 BOOLEAN,
    num_upgrades_in_set       INTEGER,
    mod_set                   VARCHAR(300),
    mod_set_values_json       TEXT,
    available_challenges_json TEXT,
    level_stats_json          TEXT,
    upgrade_entries_json      TEXT,
    tags_json                 TEXT
);

COMMENT ON TABLE nnmbot.wf_official_upgrade IS 'Warframe 官方 MOD 和升级项高频查询字段';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.base_drain IS 'MOD 基础容量消耗';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.fusion_limit IS 'MOD 融合等级上限';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.compat_name IS 'MOD 兼容对象名称';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.type IS '升级项类型';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.subtype IS '升级项子类型';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.polarity IS 'MOD 极性';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.rarity IS 'MOD 稀有度';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.is_utility IS '是否为功能槽 MOD';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.is_augment IS '是否为强化 MOD';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.is_exilus IS '是否为特殊槽 MOD';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.num_upgrades_in_set IS '升级套装包含数量';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.mod_set IS 'MOD 套装名称';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.mod_set_values_json IS 'MOD 套装数值 JSON';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.available_challenges_json IS '可用挑战条件 JSON';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.level_stats_json IS '分级属性 JSON';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.upgrade_entries_json IS '升级条目 JSON';
COMMENT ON COLUMN nnmbot.wf_official_upgrade.tags_json IS '升级项标签 JSON';

CREATE TABLE IF NOT EXISTS nnmbot.wf_official_relic_arcane (
    official_record_id BIGINT PRIMARY KEY,
    type               VARCHAR(160),
    rarity             VARCHAR(120),
    level_stats_json   TEXT,
    relic_rewards_json TEXT
);

COMMENT ON TABLE nnmbot.wf_official_relic_arcane IS 'Warframe 官方遗物和奥术高频查询字段';
COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.type IS '遗物或奥术类型';
COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.rarity IS '遗物或奥术稀有度';
COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.level_stats_json IS '奥术分级属性 JSON';
COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.relic_rewards_json IS '遗物奖励池 JSON';

CREATE TABLE IF NOT EXISTS nnmbot.wf_official_region (
    official_record_id BIGINT PRIMARY KEY,
    system_name        VARCHAR(160),
    system_index       INTEGER,
    node_type          INTEGER,
    mastery_req        INTEGER,
    mission_index      INTEGER,
    faction_index      INTEGER,
    min_enemy_level    INTEGER,
    max_enemy_level    INTEGER
);

COMMENT ON TABLE nnmbot.wf_official_region IS 'Warframe 官方星图节点高频查询字段';
COMMENT ON COLUMN nnmbot.wf_official_region.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_official_region.system_name IS '节点所属星球或星系名称';
COMMENT ON COLUMN nnmbot.wf_official_region.system_index IS '节点所属星系索引';
COMMENT ON COLUMN nnmbot.wf_official_region.node_type IS '节点类型';
COMMENT ON COLUMN nnmbot.wf_official_region.mastery_req IS '节点段位要求';
COMMENT ON COLUMN nnmbot.wf_official_region.mission_index IS '节点任务类型索引';
COMMENT ON COLUMN nnmbot.wf_official_region.faction_index IS '节点敌对派系索引';
COMMENT ON COLUMN nnmbot.wf_official_region.min_enemy_level IS '节点最小敌人等级';
COMMENT ON COLUMN nnmbot.wf_official_region.max_enemy_level IS '节点最大敌人等级';

CREATE TABLE IF NOT EXISTS nnmbot.wf_official_recipe (
    official_record_id      BIGINT PRIMARY KEY,
    result_type             VARCHAR(500),
    build_price             INTEGER,
    build_time              INTEGER,
    consume_on_use          BOOLEAN,
    skip_build_time_price   INTEGER,
    num                     INTEGER,
    prime_selling_price     INTEGER,
    always_available        BOOLEAN,
    ingredients_json        TEXT,
    secret_ingredients_json TEXT
);

COMMENT ON TABLE nnmbot.wf_official_recipe IS 'Warframe 官方蓝图和配方高频查询字段';
COMMENT ON COLUMN nnmbot.wf_official_recipe.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_official_recipe.result_type IS '配方产物 uniqueName';
COMMENT ON COLUMN nnmbot.wf_official_recipe.build_price IS '配方建造价格';
COMMENT ON COLUMN nnmbot.wf_official_recipe.build_time IS '配方建造时间';
COMMENT ON COLUMN nnmbot.wf_official_recipe.consume_on_use IS '配方是否使用后消耗';
COMMENT ON COLUMN nnmbot.wf_official_recipe.skip_build_time_price IS '配方立即完成价格';
COMMENT ON COLUMN nnmbot.wf_official_recipe.num IS '配方产出数量';
COMMENT ON COLUMN nnmbot.wf_official_recipe.prime_selling_price IS 'Prime 物品出售价格';
COMMENT ON COLUMN nnmbot.wf_official_recipe.always_available IS '配方是否始终可用';
COMMENT ON COLUMN nnmbot.wf_official_recipe.ingredients_json IS '配方材料 JSON';
COMMENT ON COLUMN nnmbot.wf_official_recipe.secret_ingredients_json IS '配方隐藏材料 JSON';

CREATE TABLE IF NOT EXISTS nnmbot.wf_manual_item (
    id              BIGSERIAL PRIMARY KEY,
    source_key      VARCHAR(500) NOT NULL UNIQUE,
    official_name   VARCHAR(500) NOT NULL,
    localized_name  VARCHAR(500) NOT NULL,
    normalized_name VARCHAR(500) NOT NULL,
    wiki_slug       VARCHAR(300),
    market_slug     VARCHAR(300),
    remark          VARCHAR(1000),
    raw_json        TEXT,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nnmbot.wf_manual_item IS '管理员维护的 Warframe 人工物品数据';
COMMENT ON COLUMN nnmbot.wf_manual_item.id IS '人工物品主键';
COMMENT ON COLUMN nnmbot.wf_manual_item.source_key IS '人工物品稳定业务标识';
COMMENT ON COLUMN nnmbot.wf_manual_item.official_name IS '人工维护的英文名称';
COMMENT ON COLUMN nnmbot.wf_manual_item.localized_name IS '人工维护的本地化名称';
COMMENT ON COLUMN nnmbot.wf_manual_item.normalized_name IS '人工物品规范化名称';
COMMENT ON COLUMN nnmbot.wf_manual_item.wiki_slug IS '灰机 Wiki 页面路径';
COMMENT ON COLUMN nnmbot.wf_manual_item.market_slug IS 'Warframe Market slug';
COMMENT ON COLUMN nnmbot.wf_manual_item.remark IS '管理员备注';
COMMENT ON COLUMN nnmbot.wf_manual_item.raw_json IS '人工扩展 JSON';
COMMENT ON COLUMN nnmbot.wf_manual_item.enabled IS '是否参与运行时解析';
COMMENT ON COLUMN nnmbot.wf_manual_item.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_manual_item.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_manual_alias (
    id              BIGSERIAL PRIMARY KEY,
    manual_item_id  BIGINT NOT NULL,
    alias_name      VARCHAR(300) NOT NULL,
    normalized_alias VARCHAR(300) NOT NULL,
    remark          VARCHAR(1000),
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_wf_manual_alias UNIQUE (manual_item_id, normalized_alias)
);

COMMENT ON TABLE nnmbot.wf_manual_alias IS '管理员维护的 Warframe 人工物品别名';
COMMENT ON COLUMN nnmbot.wf_manual_alias.id IS '人工别名主键';
COMMENT ON COLUMN nnmbot.wf_manual_alias.manual_item_id IS '目标人工物品业务标识，不声明外键';
COMMENT ON COLUMN nnmbot.wf_manual_alias.alias_name IS '别名原始文本';
COMMENT ON COLUMN nnmbot.wf_manual_alias.normalized_alias IS '规范化检索值';
COMMENT ON COLUMN nnmbot.wf_manual_alias.remark IS '管理员备注';
COMMENT ON COLUMN nnmbot.wf_manual_alias.enabled IS '是否参与运行时解析';
COMMENT ON COLUMN nnmbot.wf_manual_alias.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_manual_alias.updated_at IS '最后更新时间';

-- 论坛文章是低频维护、可被多个命令查询的 Warframe 社区内容。
-- 文章正文和展示地址落库，命令运行时只查询 enabled 文章并返回 page_url。
CREATE TABLE IF NOT EXISTS nnmbot.wf_forum_article (
    id               BIGINT PRIMARY KEY,
    slug             VARCHAR(160) NOT NULL UNIQUE,
    title            VARCHAR(300) NOT NULL,
    summary          VARCHAR(1000),
    tags             VARCHAR(1000),
    category         VARCHAR(120),
    cover_url        VARCHAR(1000),
    content_html     TEXT NOT NULL,
    content_markdown TEXT,
    page_url         VARCHAR(1000) NOT NULL,
    enabled          BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order       INTEGER NOT NULL DEFAULT 0,
    created_by_user_id BIGINT NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_wf_forum_article_sort_order CHECK (sort_order >= 0)
);

COMMENT ON TABLE nnmbot.wf_forum_article IS 'Warframe 论坛文章及配装内容，供管理端维护和 wf配装 命令查询';
COMMENT ON COLUMN nnmbot.wf_forum_article.id IS '论坛文章雪花 ID';
COMMENT ON COLUMN nnmbot.wf_forum_article.slug IS '文章稳定业务标识和 URL 短标识';
COMMENT ON COLUMN nnmbot.wf_forum_article.title IS '文章标题';
COMMENT ON COLUMN nnmbot.wf_forum_article.summary IS '文章摘要';
COMMENT ON COLUMN nnmbot.wf_forum_article.tags IS '文章标签，按管理端约定保存';
COMMENT ON COLUMN nnmbot.wf_forum_article.category IS '文章分类';
COMMENT ON COLUMN nnmbot.wf_forum_article.cover_url IS '文章封面地址';
COMMENT ON COLUMN nnmbot.wf_forum_article.content_html IS '文章 HTML 正文';
COMMENT ON COLUMN nnmbot.wf_forum_article.content_markdown IS '文章 Markdown 原文，可为空';
COMMENT ON COLUMN nnmbot.wf_forum_article.page_url IS '文章对外查看 URI，仅保存前端 hash 路径，完整地址由命令运行时拼接';
COMMENT ON COLUMN nnmbot.wf_forum_article.enabled IS '是否允许在管理端和机器人查询结果中使用';
COMMENT ON COLUMN nnmbot.wf_forum_article.sort_order IS '文章展示和查询结果排序值，数值越小越靠前';
COMMENT ON COLUMN nnmbot.wf_forum_article.created_by_user_id IS '文章创建人的系统用户主键，仅通过应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_forum_article.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.wf_forum_article.updated_at IS '最后更新时间';

-- 蹲任务规则属于群组成员的长期配置；世界状态本身保持在运行时缓存中。
-- group_id 和 member_id 仅通过应用层逻辑关联，不声明数据库外键。
CREATE TABLE IF NOT EXISTS nnmbot.wf_group_watch_rule (
    id             BIGSERIAL PRIMARY KEY,
    group_id       BIGINT NOT NULL,
    member_id      BIGINT NOT NULL,
    watch_type     VARCHAR(80) NOT NULL,
    watch_key      VARCHAR(160) NOT NULL,
    watch_value    VARCHAR(500) NOT NULL,
    enabled        BOOLEAN NOT NULL DEFAULT TRUE,
    notify_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_match_key VARCHAR(500),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_wf_group_watch_rule
        UNIQUE (group_id, member_id, watch_type, watch_key, watch_value)
);

COMMENT ON TABLE nnmbot.wf_group_watch_rule IS 'Warframe 群组成员蹲任务规则和世界状态通知配置';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.id IS '蹲任务规则主键';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.group_id IS '逻辑群组主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.member_id IS '群组成员主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.watch_type IS '规则类型，如任务、节点、派系、奖励或裂隙';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.watch_key IS '规则匹配字段';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.watch_value IS '规则匹配值';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.enabled IS '是否启用该成员的蹲任务规则';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.notify_enabled IS '命中规则后是否发送群消息，属于蹲任务插件配置';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.last_match_key IS '上次已处理的稳定命中标识，用于避免重复通知';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.wf_group_watch_rule.updated_at IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_wf_official_enabled_name
    ON nnmbot.wf_official_export_record (enabled, localized_name);
CREATE INDEX IF NOT EXISTS idx_wf_official_market_slug
    ON nnmbot.wf_official_export_record (market_slug);
CREATE INDEX IF NOT EXISTS idx_wf_manual_item_enabled_name
    ON nnmbot.wf_manual_item (enabled, localized_name);
CREATE INDEX IF NOT EXISTS idx_wf_manual_alias_normalized
    ON nnmbot.wf_manual_alias (enabled, normalized_alias);
CREATE INDEX IF NOT EXISTS idx_wf_forum_article_enabled_order
    ON nnmbot.wf_forum_article (enabled, sort_order, id);
CREATE INDEX IF NOT EXISTS idx_wf_forum_article_category
    ON nnmbot.wf_forum_article (enabled, category, sort_order, id);
CREATE INDEX IF NOT EXISTS idx_wf_group_watch_rule_group
    ON nnmbot.wf_group_watch_rule (group_id, enabled, updated_at DESC);
CREATE INDEX IF NOT EXISTS idx_wf_group_watch_rule_member
    ON nnmbot.wf_group_watch_rule (member_id, enabled);

-- RBAC、菜单和命令初始化数据。
-- 默认开发管理员为 admin / admin123。

-- Root menus.
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
VALUES
    (NULL, 'Dashboard', '/dashboard', '/index/index', 'menus.dashboard.title', 'ri:pie-chart-line', 10,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'System', '/system', '/index/index', 'menus.system.title', 'ri:user-3-line', 20,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'SnowLumaConsole', '/outside/iframe/snowluma', NULL, 'SnowLuma', 'ri:snowflake-line', 26,
        FALSE, FALSE, FALSE, FALSE, TRUE, 'http://localhost:5098/', NULL, NULL, TRUE, TRUE),
    (NULL, 'QqWebLogin', '/outside/iframe/qq-web-login', NULL, 'QQ 网页登录', 'ri:qq-line', 27,
        FALSE, FALSE, FALSE, FALSE, TRUE, 'http://localhost:5899/', NULL, NULL, TRUE, TRUE),
    (NULL, 'BotManagement', '/bot-management', '/index/index', 'menus.bot.title', 'ri:robot-2-line', 28,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Template', '/template', '/index/index', 'menus.template.title', 'ri:apps-2-line', 30,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Widgets', '/widgets', '/index/index', 'menus.widgets.title', 'ri:apps-2-add-line', 40,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Examples', '/examples', '/index/index', 'menus.examples.title', 'ri:sparkling-line', 60,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Article', '/article', '/index/index', 'menus.article.title', 'ri:book-2-line', 70,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Result', '/result', '/index/index', 'menus.result.title', 'ri:checkbox-circle-line', 80,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Exception', '/exception', '/index/index', 'menus.exception.title', 'ri:error-warning-line', 90,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Safeguard', '/safeguard', '/index/index', 'menus.safeguard.title', 'ri:shield-check-line', 100,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
ON CONFLICT (name) DO NOTHING;

-- First-level menus.
WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('Dashboard', 'Console', 'console', '/dashboard/console', 'menus.dashboard.console', 'ri:home-smile-2-line', 11,
            TRUE, TRUE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Dashboard', 'Analysis', 'analysis', '/dashboard/analysis', 'menus.dashboard.analysis', 'ri:align-item-bottom-line', 12,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Dashboard', 'Ecommerce', 'ecommerce', '/dashboard/ecommerce', 'menus.dashboard.ecommerce', 'ri:bar-chart-box-line', 13,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('System', 'User', 'user', '/system/user', 'menus.system.user', 'ri:user-line', 21,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('System', 'Role', 'role', '/system/role', 'menus.system.role', 'ri:user-settings-line', 22,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('System', 'UserCenter', 'user-center', '/system/user-center', 'menus.system.userCenter', 'ri:user-line', 23,
            TRUE, FALSE, TRUE, FALSE, FALSE, NULL, NULL, NULL, FALSE, TRUE),
        ('System', 'Menus', 'menu', '/system/menu', 'menus.system.menu', 'ri:menu-line', 24,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('System', 'OperationLog', 'audit', '/system/audit', 'menus.system.audit', 'ri:file-list-3-line', 25,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('System', 'OnlineSession', 'session', '/system/session', 'menus.system.session', 'ri:computer-line', 26,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('System', 'Nested', 'nested', NULL, 'menus.system.nested', 'ri:menu-unfold-3-line', 27,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotManagement', 'BotInstanceManagement', 'instance', '/bot/instance', 'menus.bot.instance', 'ri:robot-2-line', 281,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotManagement', 'BotFunctionManagement', 'function', '/bot/function', 'menus.bot.function', 'ri:settings-3-line', 282,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotManagement', 'BotClusterManagement', 'cluster', '/bot/cluster', 'menus.bot.cluster', 'ri:server-line', 283,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Cards', 'cards', '/template/cards', 'menus.template.cards', 'ri:wallet-line', 31,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Banners', 'banners', '/template/banners', 'menus.template.banners', 'ri:rectangle-line', 32,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Charts', 'charts', '/template/charts', 'menus.template.charts', 'ri:bar-chart-box-line', 33,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Map', 'map', '/template/map', 'menus.template.map', 'ri:map-pin-line', 34,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Chat', 'chat', '/template/chat', 'menus.template.chat', 'ri:message-3-line', 35,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Calendar', 'calendar', '/template/calendar', 'menus.template.calendar', 'ri:calendar-2-line', 36,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Template', 'Pricing', 'pricing', '/template/pricing', 'menus.template.pricing', 'ri:money-cny-box-line', 37,
            TRUE, FALSE, FALSE, TRUE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Icon', 'icon', '/widgets/icon', 'menus.widgets.icon', 'ri:palette-line', 41,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'ImageCrop', 'image-crop', '/widgets/image-crop', 'menus.widgets.imageCrop', 'ri:screenshot-line', 42,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Excel', 'excel', '/widgets/excel', 'menus.widgets.excel', 'ri:download-2-line', 43,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Video', 'video', '/widgets/video', 'menus.widgets.video', 'ri:vidicon-line', 44,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'CountTo', 'count-to', '/widgets/count-to', 'menus.widgets.countTo', 'ri:anthropic-line', 45,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'WangEditor', 'wang-editor', '/widgets/wang-editor', 'menus.widgets.wangEditor', 'ri:t-box-line', 46,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Watermark', 'watermark', '/widgets/watermark', 'menus.widgets.watermark', 'ri:water-flash-line', 47,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'ContextMenu', 'context-menu', '/widgets/context-menu', 'menus.widgets.contextMenu', 'ri:menu-2-line', 48,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Qrcode', 'qrcode', '/widgets/qrcode', 'menus.widgets.qrcode', 'ri:qr-code-line', 49,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Drag', 'drag', '/widgets/drag', 'menus.widgets.drag', 'ri:drag-move-fill', 50,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'TextScroll', 'text-scroll', '/widgets/text-scroll', 'menus.widgets.textScroll', 'ri:input-method-line', 51,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Widgets', 'Fireworks', 'fireworks', '/widgets/fireworks', 'menus.widgets.fireworks', 'ri:magic-line', 52,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, 'Hot', TRUE, TRUE),
        ('Widgets', 'ElementUI', '/outside/iframe/elementui', NULL, 'menus.widgets.elementUI', 'ri:apps-2-line', 53,
            FALSE, FALSE, FALSE, FALSE, TRUE, 'https://element-plus.org/zh-CN/component/overview.html', NULL, NULL, TRUE, TRUE),
        ('Examples', 'Permission', 'permission', NULL, 'menus.examples.permission.title', 'ri:fingerprint-line', 61,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'Tabs', 'tabs', '/examples/tabs', 'menus.examples.tabs', 'ri:price-tag-line', 62,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'TablesBasic', 'tables/basic', '/examples/tables/basic', 'menus.examples.tablesBasic', 'ri:layout-grid-line', 63,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'Tables', 'tables', '/examples/tables', 'menus.examples.tables', 'ri:table-3', 64,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'Forms', 'forms', '/examples/forms', 'menus.examples.forms', 'ri:table-view', 65,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'SearchBar', 'form/search-bar', '/examples/forms/search-bar', 'menus.examples.searchBar', 'ri:table-line', 66,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'TablesTree', 'tables/tree', '/examples/tables/tree', 'menus.examples.tablesTree', 'ri:layout-2-line', 67,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Examples', 'SocketChat', 'socket-chat', '/examples/socket-chat', 'menus.examples.socketChat', 'ri:shake-hands-line', 68,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Article', 'ArticleList', 'article-list', '/article/list', 'menus.article.articleList', 'ri:article-line', 71,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Article', 'ArticleDetail', 'detail/:id', '/article/detail', 'menus.article.articleDetail', NULL, 72,
            TRUE, FALSE, TRUE, FALSE, FALSE, NULL, '/article/article-list', NULL, FALSE, TRUE),
        ('Article', 'ArticleComment', 'comment', '/article/comment', 'menus.article.comment', 'ri:mail-line', 73,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Article', 'ArticlePublish', 'publish', '/article/publish', 'menus.article.articlePublish', 'ri:telegram-2-line', 74,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Result', 'ResultSuccess', 'success', '/result/success', 'menus.result.success', 'ri:checkbox-circle-line', 81,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Result', 'ResultFail', 'fail', '/result/fail', 'menus.result.fail', 'ri:close-circle-line', 82,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Exception', 'Exception403', '403', '/exception/403', 'menus.exception.forbidden', NULL, 91,
            TRUE, FALSE, TRUE, TRUE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Exception', 'Exception404', '404', '/exception/404', 'menus.exception.notFound', NULL, 92,
            TRUE, FALSE, TRUE, TRUE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Exception', 'Exception500', '500', '/exception/500', 'menus.exception.serverError', NULL, 93,
            TRUE, FALSE, TRUE, TRUE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Safeguard', 'SafeguardServer', 'server', '/safeguard/server', 'menus.safeguard.server', 'ri:hard-drive-3-line', 101,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO NOTHING;

-- Second-level menus.
WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('Nested', 'NestedMenu1', 'menu1', '/system/nested/menu1', 'menus.system.menu1', 'ri:align-justify', 271,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Nested', 'NestedMenu2', 'menu2', NULL, 'menus.system.menu2', 'ri:align-justify', 272,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Nested', 'NestedMenu3', 'menu3', NULL, 'menus.system.menu3', 'ri:align-justify', 273,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Permission', 'PermissionSwitchRole', 'switch-role', '/examples/permission/switch-role',
            'menus.examples.permission.switchRole', 'ri:contacts-line', 611,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Permission', 'PermissionButtonAuth', 'button-auth', '/examples/permission/button-auth',
            'menus.examples.permission.buttonAuth', 'ri:mouse-line', 612,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Permission', 'PermissionPageVisibility', 'page-visibility', '/examples/permission/page-visibility',
            'menus.examples.permission.pageVisibility', 'ri:user-3-line', 613,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO NOTHING;

-- Third-level menus.
WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('NestedMenu2', 'NestedMenu2-1', 'menu2-1', '/system/nested/menu2',
            'menus.system.menu21', 'ri:align-justify', 2721,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('NestedMenu3', 'NestedMenu3-1', 'menu3-1', '/system/nested/menu3',
            'menus.system.menu31', NULL, 2731,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('NestedMenu3', 'NestedMenu3-2', 'menu3-2', NULL, 'menus.system.menu32', NULL, 2732,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO NOTHING;

-- Fourth-level menu.
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, 'NestedMenu3-2-1', 'menu3-2-1', '/system/nested/menu3/menu3-2',
       'menus.system.menu321', NULL, 27321,
       TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE
FROM nnmbot.sys_menu parent
WHERE parent.name = 'NestedMenu3-2'
ON CONFLICT (name) DO NOTHING;

-- Roles.
INSERT INTO nnmbot.sys_role (
    code, name, description, enabled, authority_level, data_scope
)
VALUES
    ('R_SUPER', '超级管理员', '拥有全部管理权限', TRUE, 100, 'ALL'),
    ('R_ADMIN', '管理员', '负责日常用户管理', TRUE, 50, 'ALL'),
    ('R_USER', '只读用户', '仅访问基础功能', TRUE, 0, 'SELF')
ON CONFLICT (code) DO NOTHING;

-- Default development administrator. The password value is a BCrypt hash for
-- admin123 and is inserted only when the account does not already exist.
INSERT INTO nnmbot.sys_user (
    username, password, nickname, enabled
)
VALUES (
    'admin',
    '$2a$10$3Wc65toh6h.Em9HofpJXhOthgQyRXvmrSlC3mIKC41Dyr6pUpyczS',
    '超级管理员',
    TRUE
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO nnmbot.sys_user_role (user_id, role_id)
SELECT user_account.id, role.id
FROM nnmbot.sys_user user_account
JOIN nnmbot.sys_role role ON role.code = 'R_SUPER'
WHERE user_account.username = 'admin'
ON CONFLICT DO NOTHING;

-- Button and API permissions.
WITH seed(code, name, menu_name) AS (
    VALUES
        ('system:user:view', '查看用户', 'User'),
        ('system:user:create', '创建用户', 'User'),
        ('system:user:update', '修改用户', 'User'),
        ('system:user:disable', '禁用用户', 'User'),
        ('system:role:view', '查看角色', 'Role'),
        ('system:role:create', '创建角色', 'Role'),
        ('system:role:update', '修改角色', 'Role'),
        ('system:role:delete', '删除角色', 'Role'),
        ('system:role:assign', '分配权限', 'Role'),
        ('system:menu:view', '查看菜单', 'Menus'),
        ('system:menu:create', '创建菜单', 'Menus'),
        ('system:menu:update', '修改菜单', 'Menus'),
        ('system:menu:delete', '删除菜单', 'Menus'),
        ('system:audit:view', '查看操作日志', 'OperationLog'),
        ('system:session:view', '查看在线会话', 'OnlineSession'),
        ('system:session:kickout', '强制下线', 'OnlineSession'),
        ('bot:instance:view', '查看机器人实例', 'BotInstanceManagement'),
        ('bot:instance:create', '创建机器人实例', 'BotInstanceManagement'),
        ('bot:instance:update', '修改机器人实例', 'BotInstanceManagement'),
        ('bot:instance:delete', '删除机器人实例', 'BotInstanceManagement'),
        ('bot:instance:operate', '启动或停止机器人实例', 'BotInstanceManagement'),
        ('bot:instance:probe', '获取 OneBot 登录账号', 'BotInstanceManagement'),
        ('add', '新增', 'PermissionButtonAuth'),
        ('edit', '编辑', 'PermissionButtonAuth'),
        ('delete', '删除', 'PermissionButtonAuth'),
        ('export', '导出', 'PermissionButtonAuth'),
        ('view', '查看', 'PermissionButtonAuth'),
        ('publish', '发布', 'PermissionButtonAuth'),
        ('config', '配置', 'PermissionButtonAuth'),
        ('manage', '管理', 'PermissionButtonAuth')
)
INSERT INTO nnmbot.sys_permission (code, name, type, menu_id, enabled)
SELECT seed.code, seed.name, 'BUTTON', menu.id, TRUE
FROM seed
JOIN nnmbot.sys_menu menu ON menu.name = seed.menu_name
ON CONFLICT (code) DO NOTHING;

-- Super administrator receives every current menu and permission.
INSERT INTO nnmbot.sys_role_menu (role_id, menu_id)
SELECT role.id, menu.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_menu menu
WHERE role.code = 'R_SUPER'
ON CONFLICT DO NOTHING;

INSERT INTO nnmbot.sys_role_permission (role_id, permission_id)
SELECT role.id, permission.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_permission permission
WHERE role.code = 'R_SUPER'
ON CONFLICT DO NOTHING;

-- Regular administrators cannot manage peers or higher roles.
INSERT INTO nnmbot.sys_role_menu (role_id, menu_id)
SELECT role.id, menu.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_menu menu
WHERE role.code = 'R_ADMIN'
  AND menu.name NOT IN (
      'Role',
      'Menus',
      'OperationLog',
      'OnlineSession',
      'PermissionPageVisibility'
  )
ON CONFLICT DO NOTHING;

INSERT INTO nnmbot.sys_role_permission (role_id, permission_id)
SELECT role.id, permission.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_permission permission
WHERE role.code = 'R_ADMIN'
  AND (
      permission.code = 'system:role:view'
      OR (
          permission.code NOT LIKE 'system:role:%'
          AND permission.code NOT LIKE 'system:menu:%'
          AND permission.code NOT LIKE 'system:audit:%'
          AND permission.code NOT LIKE 'system:session:%'
      )
  )
ON CONFLICT DO NOTHING;

-- NnmBot management, command and Warframe menus.
-- Legacy template pages remain available as development references.

-- 新增机器人命令、群组菜单。
WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('BotManagement', 'BotCommandManagement', 'command', NULL,
            'menus.bot.command.title', 'ri:terminal-box-line', 282,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotManagement', 'BotCapabilityManagement', 'capability', '/bot/robot-role',
            'menus.bot.capability', 'ri:shield-user-line', 283,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotManagement', 'BotGroupManagement', 'group', '/bot/group',
            'menus.bot.group', 'ri:group-line', 284,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO UPDATE SET
    parent_id = EXCLUDED.parent_id,
    path = EXCLUDED.path,
    component = EXCLUDED.component,
    title = EXCLUDED.title,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    keep_alive = EXCLUDED.keep_alive,
    fixed_tab = EXCLUDED.fixed_tab,
    hide_tab = EXCLUDED.hide_tab,
    full_page = EXCLUDED.full_page,
    iframe = EXCLUDED.iframe,
    link = EXCLUDED.link,
    active_path = EXCLUDED.active_path,
    text_badge = EXCLUDED.text_badge,
    visible = EXCLUDED.visible,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('BotCommandManagement', 'BotSystemCommands', 'system', '/bot/command',
            'menus.bot.command.system', 'ri:settings-3-line', 2821,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotCommandManagement', 'BotWarframeCommands', 'warframe', '/bot/command',
            'menus.bot.command.warframe', 'ri:game-line', 2822,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('BotCommandManagement', 'BotTemplateManagement', 'template', '/bot/template',
            'menus.bot.command.template', 'ri:html5-line', 2823,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO UPDATE SET
    parent_id = EXCLUDED.parent_id,
    path = EXCLUDED.path,
    component = EXCLUDED.component,
    title = EXCLUDED.title,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    keep_alive = EXCLUDED.keep_alive,
    fixed_tab = EXCLUDED.fixed_tab,
    hide_tab = EXCLUDED.hide_tab,
    full_page = EXCLUDED.full_page,
    iframe = EXCLUDED.iframe,
    link = EXCLUDED.link,
    active_path = EXCLUDED.active_path,
    text_badge = EXCLUDED.text_badge,
    visible = EXCLUDED.visible,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

-- Warframe 管理菜单。
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
VALUES (
    NULL, 'Warframe', '/warframe', '/index/index', 'menus.warframe.title',
    'ri:game-line', 29, FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE
)
ON CONFLICT (name) DO UPDATE SET
    path = EXCLUDED.path,
    component = EXCLUDED.component,
    title = EXCLUDED.title,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    visible = EXCLUDED.visible,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('Warframe', 'WarframeAlias', 'alias', '/warframe/alias',
            'menus.warframe.alias', 'ri:translate-2', 291,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Warframe', 'WarframeData', 'data', '/warframe/data',
            'menus.warframe.data', 'ri:database-2-line', 292,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Warframe', 'WarframeWorldState', 'world-state', '/warframe/world-state',
            'menus.warframe.worldState', 'ri:earth-line', 290,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Warframe', 'WarframeCommunityData', 'community', '/warframe/community-datasets',
            'menus.warframe.community', 'ri:git-branch-line', 293,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Warframe', 'WarframeForum', 'forum', '/warframe/forum',
            'menus.warframe.forum', 'ri:book-open-line', 294,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO UPDATE SET
    parent_id = EXCLUDED.parent_id,
    path = EXCLUDED.path,
    component = EXCLUDED.component,
    title = EXCLUDED.title,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    keep_alive = EXCLUDED.keep_alive,
    fixed_tab = EXCLUDED.fixed_tab,
    hide_tab = EXCLUDED.hide_tab,
    full_page = EXCLUDED.full_page,
    iframe = EXCLUDED.iframe,
    link = EXCLUDED.link,
    active_path = EXCLUDED.active_path,
    text_badge = EXCLUDED.text_badge,
    visible = EXCLUDED.visible,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

-- 论坛管理页使用 Warframe 下的独立隐藏同级路由；论坛列表组件不承载子路由。
WITH seed(parent_name, name, path, component, title, icon, sort_order,
          keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
          text_badge, visible, enabled) AS (
    VALUES
        ('Warframe', 'WarframeForumManageDetail', 'forum/manage-detail/:id',
            '/warframe/forum/detail', 'menus.warframe.forumDetail', NULL, 2941,
            FALSE, FALSE, TRUE, FALSE, FALSE, NULL, '/warframe/forum', NULL, FALSE, TRUE),
        ('Warframe', 'WarframeForumPublish', 'forum/publish',
            '/warframe/forum/publish', 'menus.warframe.forumPublish', NULL, 2942,
            FALSE, FALSE, TRUE, FALSE, FALSE, NULL, '/warframe/forum', NULL, FALSE, TRUE)
)
INSERT INTO nnmbot.sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM seed
JOIN nnmbot.sys_menu parent ON parent.name = seed.parent_name
ON CONFLICT (name) DO UPDATE SET
    parent_id = EXCLUDED.parent_id,
    path = EXCLUDED.path,
    component = EXCLUDED.component,
    title = EXCLUDED.title,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order,
    keep_alive = EXCLUDED.keep_alive,
    fixed_tab = EXCLUDED.fixed_tab,
    hide_tab = EXCLUDED.hide_tab,
    full_page = EXCLUDED.full_page,
    iframe = EXCLUDED.iframe,
    link = EXCLUDED.link,
    active_path = EXCLUDED.active_path,
    text_badge = EXCLUDED.text_badge,
    visible = EXCLUDED.visible,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

-- 命令管理的按钮权限。
INSERT INTO nnmbot.sys_permission (code, name, type, menu_id, enabled)
SELECT seed.code, seed.name, 'BUTTON', menu.id, TRUE
FROM (
    VALUES
        ('bot:command:view', '查看命令配置'),
        ('bot:command:update', '修改命令配置'),
        ('bot:group:view', '查看群组管理'),
        ('bot:group:update', '修改群组配置'),
        ('bot:group:delete', '删除群组作用域')
) AS seed(code, name)
CROSS JOIN LATERAL (
    SELECT menu.id
    FROM nnmbot.sys_menu menu
    WHERE menu.name = CASE
        WHEN seed.code LIKE 'bot:command:%' THEN 'BotSystemCommands'
        ELSE 'BotGroupManagement'
    END
) menu
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    menu_id = EXCLUDED.menu_id,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

-- Warframe 三类数据的查看、刷新、维护和审批权限。
INSERT INTO nnmbot.sys_permission (code, name, type, menu_id, enabled)
SELECT seed.code, seed.name, 'BUTTON', menu.id, TRUE
FROM (
    VALUES
        ('warframe:world:view', '查看 Warframe 世界数据', 'WarframeWorldState'),
        ('warframe:world:refresh', '刷新 Warframe 世界数据', 'WarframeWorldState'),
        ('warframe:game:view', '查看 Warframe 官方游戏数据', 'WarframeData'),
        ('warframe:game:refresh', '刷新 Warframe 官方游戏数据', 'WarframeData'),
        ('warframe:community:view', '查看 Warframe 社区数据', 'WarframeCommunityData'),
        ('warframe:community:update', '维护 Warframe 社区数据', 'WarframeCommunityData'),
        ('warframe:community:sync', '同步 Warframe 社区数据', 'WarframeCommunityData'),
        ('warframe:community:apply', '审批 Warframe 社区数据', 'WarframeCommunityData'),
        ('warframe:data:view', '兼容查看 Warframe 本地数据', 'WarframeData'),
        ('warframe:data:update', '兼容维护 Warframe 本地数据', 'WarframeCommunityData'),
        ('warframe:forum:view', '查看 Warframe 论坛文章', 'WarframeForum'),
        ('warframe:forum:create', '创建 Warframe 论坛文章', 'WarframeForum'),
        ('warframe:forum:update', '修改 Warframe 论坛文章', 'WarframeForum'),
        ('warframe:forum:delete', '删除 Warframe 论坛文章', 'WarframeForum')
) AS seed(code, name, menu_name)
CROSS JOIN LATERAL (
    SELECT menu.id
    FROM nnmbot.sys_menu menu
    WHERE menu.name = seed.menu_name
) menu
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    menu_id = EXCLUDED.menu_id,
    enabled = EXCLUDED.enabled,
    updated_at = CURRENT_TIMESTAMP;

-- 将新增菜单授予超级管理员和普通管理员。
INSERT INTO nnmbot.sys_role_menu (role_id, menu_id)
SELECT role.id, menu.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_menu menu
WHERE role.code IN ('R_SUPER', 'R_ADMIN')
  AND menu.name IN (
      'BotCommandManagement',
      'BotSystemCommands',
      'BotWarframeCommands',
      'BotTemplateManagement',
      'BotCapabilityManagement',
      'BotGroupManagement',
      'Warframe',
      'WarframeAlias',
      'WarframeData',
      'WarframeWorldState',
      'WarframeCommunityData',
      'WarframeForum',
      'WarframeForumManageDetail',
      'WarframeForumPublish'
  )
ON CONFLICT DO NOTHING;

INSERT INTO nnmbot.sys_role_permission (role_id, permission_id)
SELECT role.id, permission.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_permission permission
WHERE role.code IN ('R_SUPER', 'R_ADMIN')
  AND permission.code IN (
      'bot:command:view',
      'bot:command:update',
      'bot:group:view',
      'bot:group:update',
      'bot:group:delete',
      'warframe:world:view',
      'warframe:world:refresh',
      'warframe:game:view',
      'warframe:game:refresh',
      'warframe:community:view',
      'warframe:community:update',
      'warframe:community:sync',
      'warframe:community:apply',
      'warframe:data:view',
      'warframe:data:update',
      'warframe:forum:view',
      'warframe:forum:create',
      'warframe:forum:update',
      'warframe:forum:delete'
  )
ON CONFLICT DO NOTHING;

-- Reapply the super administrator invariant after every menu and permission
-- seed has been created.
INSERT INTO nnmbot.sys_role_menu (role_id, menu_id)
SELECT role.id, menu.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_menu menu
WHERE role.code = 'R_SUPER'
  AND role.enabled
ON CONFLICT DO NOTHING;

INSERT INTO nnmbot.sys_role_permission (role_id, permission_id)
SELECT role.id, permission.id
FROM nnmbot.sys_role role
CROSS JOIN nnmbot.sys_permission permission
WHERE role.code = 'R_SUPER'
  AND role.enabled
  AND permission.enabled
ON CONFLICT DO NOTHING;

-- Robot capability roles, logical groups and group points.
-- These tables are deliberately separate from the system RBAC tables above.

CREATE TABLE IF NOT EXISTS nnmbot.bot_robot_role (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(64) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bot_robot_role_code UNIQUE (code)
);

COMMENT ON TABLE nnmbot.bot_robot_role IS '机器人能力角色，不属于后台用户系统 RBAC';
COMMENT ON COLUMN nnmbot.bot_robot_role.id IS '机器人能力角色主键';
COMMENT ON COLUMN nnmbot.bot_robot_role.code IS '机器人能力角色业务编码';
COMMENT ON COLUMN nnmbot.bot_robot_role.name IS '机器人能力角色名称';
COMMENT ON COLUMN nnmbot.bot_robot_role.description IS '机器人能力角色说明';
COMMENT ON COLUMN nnmbot.bot_robot_role.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_robot_role.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.bot_robot_role.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_command_robot_role (
    id            BIGSERIAL PRIMARY KEY,
    command_id    BIGINT NOT NULL,
    robot_role_id BIGINT NOT NULL,
    CONSTRAINT uq_bot_command_robot_role UNIQUE (command_id, robot_role_id)
);

COMMENT ON TABLE nnmbot.bot_command_robot_role IS '命令与机器人能力角色关联';
COMMENT ON COLUMN nnmbot.bot_command_robot_role.id IS '命令与机器人角色关联主键';
COMMENT ON COLUMN nnmbot.bot_command_robot_role.command_id IS '命令配置主键';
COMMENT ON COLUMN nnmbot.bot_command_robot_role.robot_role_id IS '机器人能力角色主键';

CREATE TABLE IF NOT EXISTS nnmbot.bot_robot_role_instance (
    id              BIGSERIAL PRIMARY KEY,
    robot_role_id   BIGINT NOT NULL,
    bot_instance_id BIGINT NOT NULL,
    CONSTRAINT uq_bot_robot_role_instance UNIQUE (robot_role_id, bot_instance_id)
);

COMMENT ON TABLE nnmbot.bot_robot_role_instance IS '机器人能力角色与机器人实例关联';
COMMENT ON COLUMN nnmbot.bot_robot_role_instance.id IS '机器人角色与实例关联主键';
COMMENT ON COLUMN nnmbot.bot_robot_role_instance.robot_role_id IS '机器人能力角色主键';
COMMENT ON COLUMN nnmbot.bot_robot_role_instance.bot_instance_id IS '机器人渠道实例主键';

CREATE TABLE IF NOT EXISTS nnmbot.bot_group (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(64) NOT NULL,
    name        VARCHAR(160) NOT NULL,
    description VARCHAR(255),
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bot_group_code UNIQUE (code)
);

COMMENT ON TABLE nnmbot.bot_group IS '逻辑群组，作为跨渠道消息和业务状态的隔离主体';
COMMENT ON COLUMN nnmbot.bot_group.id IS '逻辑群组主键';
COMMENT ON COLUMN nnmbot.bot_group.code IS '逻辑群组业务编码';
COMMENT ON COLUMN nnmbot.bot_group.name IS '逻辑群组名称';
COMMENT ON COLUMN nnmbot.bot_group.description IS '逻辑群组说明';
COMMENT ON COLUMN nnmbot.bot_group.enabled IS '是否启用';
COMMENT ON COLUMN nnmbot.bot_group.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.bot_group.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_group_bot (
    id              BIGSERIAL PRIMARY KEY,
    group_id        BIGINT NOT NULL,
    bot_instance_id BIGINT NOT NULL,
    platform        VARCHAR(32) NOT NULL,
    scope_type      VARCHAR(20) NOT NULL DEFAULT 'GROUP',
    scope_key       VARCHAR(160) NOT NULL,
    display_name    VARCHAR(160),
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_bot_group_bot
        UNIQUE (group_id, bot_instance_id, platform, scope_type, scope_key)
);

COMMENT ON TABLE nnmbot.bot_group_bot IS '逻辑群组与渠道机器人实例及平台侧群组标识的关联';
COMMENT ON COLUMN nnmbot.bot_group_bot.id IS '群组机器人绑定主键';
COMMENT ON COLUMN nnmbot.bot_group_bot.group_id IS '逻辑群组主键';
COMMENT ON COLUMN nnmbot.bot_group_bot.bot_instance_id IS '机器人实例主键';
COMMENT ON COLUMN nnmbot.bot_group_bot.platform IS '渠道平台，如 ONEBOT、QQ_OFFICIAL';
COMMENT ON COLUMN nnmbot.bot_group_bot.scope_type IS '平台作用域类型，如 GROUP、CHANNEL';
COMMENT ON COLUMN nnmbot.bot_group_bot.scope_key IS '平台侧群号、群 OpenID 或频道标识';
COMMENT ON COLUMN nnmbot.bot_group_bot.display_name IS '平台侧群组显示名称';
COMMENT ON COLUMN nnmbot.bot_group_bot.enabled IS '是否启用该机器人在当前逻辑群组中的服务';

CREATE TABLE IF NOT EXISTS nnmbot.bot_group_member (
    id                   BIGSERIAL PRIMARY KEY,
    group_id             BIGINT NOT NULL,
    qq_id                VARCHAR(32),
    member_openid        VARCHAR(160),
    kook_id              VARCHAR(160),
    qq_id_verified       BOOLEAN NOT NULL DEFAULT FALSE,
    qq_bound_at          TIMESTAMP,
    display_name         VARCHAR(160),
    member_role          VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    enabled              BOOLEAN NOT NULL DEFAULT TRUE,
    points               BIGINT NOT NULL DEFAULT 0,
    total_earned         BIGINT NOT NULL DEFAULT 0,
    total_spent         BIGINT NOT NULL DEFAULT 0,
    version              INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT ck_bot_group_member_role
        CHECK (member_role IN ('OWNER', 'ADMIN', 'MEMBER')),
    CONSTRAINT ck_bot_group_member_points
        CHECK (points >= 0 AND total_earned >= 0 AND total_spent >= 0)
);

COMMENT ON TABLE nnmbot.bot_group_member IS '逻辑群组成员，按渠道身份列关联不同平台，不要求绑定后台用户';
COMMENT ON COLUMN nnmbot.bot_group_member.id IS '群组成员主键';
COMMENT ON COLUMN nnmbot.bot_group_member.group_id IS '逻辑群组主键';
COMMENT ON COLUMN nnmbot.bot_group_member.qq_id IS 'OneBot 或绑定命令提供的 QQ 号';
COMMENT ON COLUMN nnmbot.bot_group_member.member_openid IS 'QQ 官方机器人提供的群成员 OpenID';
COMMENT ON COLUMN nnmbot.bot_group_member.kook_id IS 'KOOK 渠道提供的用户 ID';
COMMENT ON COLUMN nnmbot.bot_group_member.qq_id_verified IS 'QQ 号是否通过 OneBot 等可信渠道核验';
COMMENT ON COLUMN nnmbot.bot_group_member.qq_bound_at IS '最近一次绑定 QQ 号的时间';
COMMENT ON COLUMN nnmbot.bot_group_member.display_name IS '渠道侧显示名称';
COMMENT ON COLUMN nnmbot.bot_group_member.member_role IS '平台群组角色：OWNER、ADMIN、MEMBER';
COMMENT ON COLUMN nnmbot.bot_group_member.enabled IS '是否启用该成员在逻辑群组中的身份';
COMMENT ON COLUMN nnmbot.bot_group_member.points IS '当前可用积分余额，数据库是最终权威值';
COMMENT ON COLUMN nnmbot.bot_group_member.total_earned IS '累计获得积分';
COMMENT ON COLUMN nnmbot.bot_group_member.total_spent IS '累计消耗积分';
COMMENT ON COLUMN nnmbot.bot_group_member.version IS '乐观锁版本号，避免并发签到或扣费覆盖余额';

CREATE TABLE IF NOT EXISTS nnmbot.bot_command_cost (
    id            BIGSERIAL PRIMARY KEY,
    command_id    BIGINT NOT NULL,
    cost_points   BIGINT NOT NULL DEFAULT 0,
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    reward_points BIGINT NOT NULL DEFAULT 0,
    reward_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    description   VARCHAR(255),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bot_command_cost_command UNIQUE (command_id),
    CONSTRAINT ck_bot_command_cost_points CHECK (cost_points >= 0),
    CONSTRAINT ck_bot_command_reward_points CHECK (reward_points >= 0)
);

COMMENT ON TABLE nnmbot.bot_command_cost IS '命令在逻辑群组中执行时的积分扣除与成功奖励规则';
COMMENT ON COLUMN nnmbot.bot_command_cost.id IS '命令积分规则主键';
COMMENT ON COLUMN nnmbot.bot_command_cost.command_id IS '命令配置主键，一个命令最多一条积分规则';
COMMENT ON COLUMN nnmbot.bot_command_cost.cost_points IS '单次执行消耗的积分数量，零表示不消耗';
COMMENT ON COLUMN nnmbot.bot_command_cost.enabled IS '是否启用该命令的积分检查';
COMMENT ON COLUMN nnmbot.bot_command_cost.reward_points IS '命令业务和响应均成功后增加的积分数量';
COMMENT ON COLUMN nnmbot.bot_command_cost.reward_enabled IS '是否启用命令成功奖励';
COMMENT ON COLUMN nnmbot.bot_command_cost.description IS '积分扣除或奖励规则说明';
COMMENT ON COLUMN nnmbot.bot_command_cost.created_at IS '创建时间';
COMMENT ON COLUMN nnmbot.bot_command_cost.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.bot_group_point_transaction (
    id                BIGSERIAL PRIMARY KEY,
    group_id          BIGINT NOT NULL,
    member_id         BIGINT NOT NULL,
    command_code      VARCHAR(80),
    transaction_type  VARCHAR(32) NOT NULL,
    amount            BIGINT NOT NULL,
    balance_after     BIGINT NOT NULL,
    idempotency_key   VARCHAR(160) NOT NULL,
    description       VARCHAR(255),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bot_group_point_transaction_key UNIQUE (idempotency_key),
    CONSTRAINT ck_bot_group_point_transaction_type
        CHECK (transaction_type IN (
            'SIGN_IN', 'COMMAND_COST', 'COMMAND_REWARD', 'ADMIN_ADJUST', 'REFUND'
        )),
    CONSTRAINT ck_bot_group_point_transaction_balance CHECK (balance_after >= 0)
);

COMMENT ON TABLE nnmbot.bot_group_point_transaction IS '逻辑群组积分流水，用于审计、幂等和余额追溯';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.id IS '积分流水主键';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.group_id IS '逻辑群组主键';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.member_id IS '群组成员主键';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.command_code IS '产生流水的命令编码，管理员调整时可以为空';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.transaction_type IS '流水类型：签到、命令消耗、命令成功奖励、管理员调整、失败退款';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.amount IS '积分变化量，增加为正数，消耗为负数';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.balance_after IS '写入流水后的成员余额快照';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.idempotency_key IS '消息或管理操作幂等键，防止重复处理';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.description IS '流水说明';
COMMENT ON COLUMN nnmbot.bot_group_point_transaction.created_at IS '流水创建时间';

CREATE INDEX IF NOT EXISTS idx_bot_command_robot_role_command
    ON nnmbot.bot_command_robot_role (command_id);
CREATE INDEX IF NOT EXISTS idx_bot_robot_role_instance_role
    ON nnmbot.bot_robot_role_instance (robot_role_id);
CREATE INDEX IF NOT EXISTS idx_bot_robot_role_instance_bot
    ON nnmbot.bot_robot_role_instance (bot_instance_id);
CREATE INDEX IF NOT EXISTS idx_bot_group_bot_group
    ON nnmbot.bot_group_bot (group_id);
CREATE INDEX IF NOT EXISTS idx_bot_group_bot_instance
    ON nnmbot.bot_group_bot (bot_instance_id);
CREATE INDEX IF NOT EXISTS idx_bot_group_member_group
    ON nnmbot.bot_group_member (group_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_bot_group_member_qq
    ON nnmbot.bot_group_member (group_id, qq_id)
    WHERE qq_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_bot_group_member_openid
    ON nnmbot.bot_group_member (group_id, member_openid)
    WHERE member_openid IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_bot_group_member_kook
    ON nnmbot.bot_group_member (group_id, kook_id)
    WHERE kook_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_bot_command_cost_enabled
    ON nnmbot.bot_command_cost (enabled, reward_enabled, command_id);
CREATE INDEX IF NOT EXISTS idx_bot_group_point_transaction_member
    ON nnmbot.bot_group_point_transaction (member_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_bot_group_point_transaction_group
    ON nnmbot.bot_group_point_transaction (group_id, created_at DESC);

-- Table comments.
COMMENT ON TABLE nnmbot.bot_channel_instance IS '机器人渠道实例及其运行开关';
COMMENT ON TABLE nnmbot.bot_onebot_config IS 'OneBot 渠道实例连接配置';
COMMENT ON TABLE nnmbot.bot_qq_official_config IS 'QQ 官方机器人渠道连接配置';
COMMENT ON TABLE nnmbot.sys_user IS '管理端系统用户';
COMMENT ON TABLE nnmbot.bot_admin IS '用户与机器人实例的管理员关联';
COMMENT ON TABLE nnmbot.mission_subscribe IS '作用域任务订阅配置';
COMMENT ON TABLE nnmbot.notification_history IS '外部事件通知发送历史';
COMMENT ON TABLE nnmbot.alias IS 'Warframe 数据别名';
COMMENT ON TABLE nnmbot.sys_role IS '系统角色及其权限等级';
COMMENT ON TABLE nnmbot.sys_menu IS '管理端菜单与动态路由定义';
COMMENT ON TABLE nnmbot.sys_permission IS '管理端按钮和接口权限';
COMMENT ON TABLE nnmbot.sys_user_role IS '用户与系统角色关联';
COMMENT ON TABLE nnmbot.sys_role_permission IS '角色与权限关联';
COMMENT ON TABLE nnmbot.sys_role_menu IS '角色与菜单关联';
COMMENT ON TABLE nnmbot.sys_operation_log IS '管理端操作审计日志';
COMMENT ON TABLE nnmbot.bot_command IS '机器人命令的可变管理配置';
COMMENT ON TABLE nnmbot.bot_command_alias IS '机器人命令自定义别名';
COMMENT ON TABLE nnmbot.bot_command_template IS '机器人图片回复 HTML 模板';
COMMENT ON TABLE nnmbot.bot_command_channel IS '命令按渠道定义的输出配置';
COMMENT ON TABLE nnmbot.bot_command_role IS '命令与系统角色的使用范围关联';
COMMENT ON TABLE nnmbot.sys_user_identity IS '系统用户与外部平台身份绑定';
COMMENT ON TABLE nnmbot.bot_scope IS '机器人在外部平台中的群组或频道作用域';
COMMENT ON TABLE nnmbot.bot_scope_member IS '作用域成员及其作用域角色';
COMMENT ON TABLE nnmbot.bot_robot_role IS '机器人能力角色，不属于后台用户系统 RBAC';
COMMENT ON TABLE nnmbot.bot_command_robot_role IS '命令与机器人能力角色关联';
COMMENT ON TABLE nnmbot.bot_robot_role_instance IS '机器人能力角色与机器人实例关联';
COMMENT ON TABLE nnmbot.bot_group IS '逻辑群组，作为跨渠道消息和业务状态的隔离主体';
COMMENT ON TABLE nnmbot.bot_group_bot IS '逻辑群组与渠道机器人实例及平台侧群组标识的关联';
COMMENT ON TABLE nnmbot.bot_group_member IS '逻辑群组成员及其积分和平台身份';
COMMENT ON TABLE nnmbot.bot_command_cost IS '命令在逻辑群组中的积分扣除与成功奖励规则';
COMMENT ON TABLE nnmbot.bot_group_point_transaction IS '逻辑群组积分流水';
COMMENT ON TABLE nnmbot.wf_forum_article IS 'Warframe 论坛文章及配装内容';
COMMENT ON TABLE nnmbot.wf_group_watch_rule IS 'Warframe 群组成员蹲任务规则';

-- Warframe command metadata.
-- Java plugins remain the executable implementation; these rows only expose
-- command settings, aliases, templates and channel output to the admin UI.
INSERT INTO nnmbot.bot_command
    (code, name, game_code, description, output_mode)
VALUES
    ('status', '状态', 'system', '查看机器人运行状态', 'TEXT'),
    ('help', '帮助', 'system', '查看命令菜单', 'TEXT'),
    ('sign-in', '签到', 'system', '每日签到并返回签到后的群组积分', 'TEXT'),
    ('bind', '绑定', 'system', '绑定当前群组成员的 QQ 号', 'TEXT'),
    ('wiki', 'Wiki', 'warframe', '返回 Warframe 物品对应的灰机 Wiki 链接', 'TEXT'),
    ('forum-loadout', 'wf配装', 'warframe', '模糊查询 Warframe 论坛配装文章并返回文章地址', 'TEXT'),
    ('market', '市场', 'warframe', '查询 Warframe Market 实时订单', 'TEXT_IMAGE'),
    ('arbitration', '仲裁', 'warframe', '查看当前仲裁任务', 'TEXT_IMAGE'),
    ('alerts', '警报', 'warframe', '查看当前警报', 'TEXT'),
    ('sorties', '突击', 'warframe', '查看当前突击', 'TEXT'),
    ('archon-hunts', '执刑官猎杀', 'warframe', '查看当前执刑官猎杀', 'TEXT'),
    ('void-trader', '奸商', 'warframe', '查看奸商行踪和商品', 'TEXT'),
    ('arbitration-table', '仲裁表', 'warframe', '查看仲裁轮换表', 'TEXT'),
    ('daily-deals', '每日特惠', 'warframe', '查看每日特惠', 'TEXT'),
    ('invasions', '入侵', 'warframe', '查看当前入侵', 'TEXT'),
    ('fissures', '裂隙', 'warframe', '查看普通裂隙', 'TEXT'),
    ('void-storms', '九重天裂隙', 'warframe', '查看九重天裂隙', 'TEXT'),
    ('steel-fissures', '钢铁裂隙', 'warframe', '查看钢铁路径裂隙', 'TEXT'),
    ('steel-path', '钢铁奖励', 'warframe', '查看钢铁路径奖励', 'TEXT'),
    ('cycles', '平原', 'warframe', '查看各个开放世界周期', 'TEXT'),
    ('ostrons', '希图斯', 'warframe', '查看希图斯赏金任务', 'TEXT'),
    ('entrati', '英择谛', 'warframe', '查看英择谛赏金任务', 'TEXT'),
    ('solaris', '索拉里斯', 'warframe', '查看索拉里斯联合赏金任务', 'TEXT'),
    ('duviri-cycle', '双衍王境', 'warframe', '查看双衍王境轮换', 'TEXT'),
    ('nightwave', '电波', 'warframe', '查看电波任务', 'TEXT'),
    ('riven-disposition', '紫卡倾向变动', 'warframe', '查看紫卡倾向变动', 'TEXT'),
    ('translation', '翻译', 'warframe', '查询 Warframe 翻译', 'TEXT'),
    ('market-riven', '紫卡市场', 'warframe', '查询紫卡市场数据', 'TEXT'),
    ('riven-market', '紫卡市场查询', 'warframe', '查询紫卡市场订单', 'TEXT'),
    ('liches', '赤毒', 'warframe', '查询赤毒相关数据', 'TEXT'),
    ('sisters', '信条', 'warframe', '查询信条相关数据', 'TEXT'),
    ('perrin-sequence', '佩兰数列', 'warframe', '查询佩兰数列和信条武器', 'TEXT'),
    ('gold-trash', '金垃圾', 'warframe', '查询金垃圾推荐', 'TEXT'),
    ('silver-trash', '银垃圾', 'warframe', '查询银垃圾推荐', 'TEXT'),
    ('relics', '遗物', 'warframe', '查询遗物信息', 'TEXT'),
    ('open-relics', '开核桃', 'warframe', '查询开核桃组队信息', 'TEXT'),
    ('riven-analyse', '紫卡分析', 'warframe', '分析紫卡截图或文本', 'TEXT'),
    ('calendar-1999', '1999日历', 'warframe', '查看 1999 日历', 'TEXT'),
    ('subscribe', '订阅', 'warframe', '订阅 Warframe 事件通知', 'TEXT'),
    ('unsubscribe', '取消订阅', 'warframe', '取消 Warframe 事件通知订阅', 'TEXT'),
    ('update-market-items', '更新WM物品', 'warframe', '更新 Warframe Market 物品数据', 'TEXT'),
    ('update-market-riven', '更新WM紫卡', 'warframe', '更新 Warframe Market 紫卡数据', 'TEXT'),
    ('update-sister', '更新信条', 'warframe', '更新信条和赤毒武器数据', 'TEXT'),
    ('update-translation', '更新翻译', 'warframe', '更新 Warframe 翻译数据', 'TEXT')
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    game_code = EXCLUDED.game_code,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO nnmbot.bot_command_alias (command_id, alias_name, boundary_policy)
SELECT command.id,
       seed.alias_name,
       CASE
           WHEN command.code = 'market'
               AND lower(seed.alias_name) IN ('wm', '/wm', 'market', '/market')
               THEN 'CJK_OR_WHITESPACE'
           WHEN seed.alias_name ~ '^[A-Za-z0-9/_-]+$'
               THEN 'WHITESPACE_REQUIRED'
           ELSE 'NONE'
       END
FROM (VALUES
    ('status', 'status'),
    ('status', '运行状态'),
    ('status', '版本'),
    ('status', '检查版本'),
    ('help', 'HELP'),
    ('help', 'help'),
    ('help', '指令'),
    ('help', '命令'),
    ('help', '菜单'),
    ('sign-in', 'sign-in'),
    ('sign-in', '签到'),
    ('bind', 'bind'),
    ('bind', '绑定'),
    ('wiki', 'wk'),
    ('wiki', '/wk'),
    ('wiki', 'wiki'),
    ('wiki', '/wiki'),
    ('forum-loadout', 'wf配装'),
    ('forum-loadout', '/wf配装'),
    ('market', 'wm'),
    ('market', '/wm'),
    ('market', 'market'),
    ('market', '/market'),
    ('market', '订单'),
    ('market', '/订单'),
    ('market', '报价'),
    ('market', '/报价'),
    ('market', '/市场'),
    ('arbitration', 'arbitration'),
    ('arbitration', '仲裁任务'),
    ('arbitration', '高价值'),
    ('alerts', '警报'),
    ('sorties', '突击'),
    ('archon-hunts', '猎杀'),
    ('archon-hunts', '执行官'),
    ('archon-hunts', '执政官'),
    ('archon-hunts', '执刑官'),
    ('arbitration-table', '仲裁表'),
    ('daily-deals', '特惠'),
    ('fissures', '裂缝'),
    ('void-storms', '九重天'),
    ('void-storms', '九重天裂缝'),
    ('steel-fissures', '钢铁裂缝'),
    ('cycles', '夜灵平原'),
    ('cycles', '夜灵平野'),
    ('cycles', '福尔图娜'),
    ('cycles', '魔胎之境'),
    ('cycles', '扎里曼'),
    ('ostrons', '地球赏金'),
    ('entrati', '火卫二赏金'),
    ('solaris', '金星赏金'),
    ('duviri-cycle', '轮换'),
    ('riven-disposition', '倾向变动'),
    ('translation', '翻译'),
    ('market-riven', 'wr'),
    ('market-riven', 'wmr'),
    ('market-riven', '/wr'),
    ('market-riven', '/wmr'),
    ('riven-market', 'rm'),
    ('riven-market', '/rm'),
    ('liches', 'cd'),
    ('liches', '/cd'),
    ('liches', '/赤毒'),
    ('sisters', 'xt'),
    ('sisters', '/xt'),
    ('sisters', '/信条'),
    ('perrin-sequence', '佩兰'),
    ('perrin-sequence', '信条武器'),
    ('relics', '核桃'),
    ('relics', '查核桃'),
    ('open-relics', '砸核桃'),
    ('riven-analyse', '分析紫卡'),
    ('calendar-1999', '1999'),
    ('calendar-1999', '日历'),
    ('update-market-items', '更新WM物品'),
    ('update-market-riven', '更新WM紫卡'),
    ('update-sister', '更新信条'),
    ('update-translation', '更新翻译')
) AS seed(code, alias_name)
JOIN nnmbot.bot_command command ON command.code = seed.code
WHERE NOT EXISTS (
    SELECT 1
    FROM nnmbot.bot_command_alias existing
    WHERE existing.command_id = command.id
      AND lower(existing.alias_name) = lower(seed.alias_name)
);

INSERT INTO nnmbot.bot_command_template
    (template_code, name, html_content)
VALUES
    ('status', '运行状态卡片', $$
<!doctype html>
<html lang="zh-CN">
<head><meta charset="UTF-8"><style>
body{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}
.panel{width:680px;background:#fff;border:1px solid #dfe5ef;border-radius:10px;padding:24px;box-sizing:border-box}
h1{font-size:28px;margin:0 0 20px}.grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}
.item{background:#f7f9fc;padding:14px;border-radius:8px}.label{color:#667085;font-size:13px}
.value{font-size:20px;font-weight:700;margin-top:6px}
</style></head>
<body><section class="panel"><h1>NnmBot 运行状态</h1>
<div class="grid"><div class="item"><div class="label">机器人账号</div><div class="value" th:text="${accountId}">-</div></div>
<div class="item"><div class="label">会话类型</div><div class="value" th:text="${scope}">-</div></div>
<div class="item"><div class="label">运行时长</div><div class="value" th:text="${uptime}">-</div></div>
<div class="item"><div class="label">内存</div><div class="value" th:text="${memory}">-</div></div>
<div class="item"><div class="label">插件数量</div><div class="value" th:text="${pluginCount}">0</div></div>
</div></section></body></html>
$$),
    ('warframe-world-state', 'Warframe 世界状态卡片', $$
<!doctype html>
<html lang="zh-CN">
<head><meta charset="UTF-8"><style>
body{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}
.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}
h1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}
.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}
.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}
.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}
</style></head>
<body><section class="panel">
<h1 th:text="${title}">Warframe</h1>
<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>
<div th:if="${available}">
  <div class="item" th:each="item : ${items}">
    <div class="entry" th:each="entry : ${item}">
      <span class="key" th:text="${entry.key}">字段</span>
      <span class="value" th:text="${entry.value}">值</span>
    </div>
  </div>
</div>
<div class="empty" th:unless="${available}">当前没有可用数据</div>
<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>
</section></body></html>
$$),
    ('warframe-market-orders', 'Warframe Market 订单卡片', $$
<!doctype html>
<html lang="zh-CN">
<head><meta charset="UTF-8"><style>
body{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}
.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}
h1{font-size:30px;line-height:1.25;margin:0}.sub{color:#657386;font-size:15px;margin:8px 0 18px}
.summary{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:18px}.price{background:#f4f8f6;border-radius:8px;padding:13px}
.label{color:#657386;font-size:14px}.value{font-size:24px;font-weight:700;color:#13795b;margin-top:4px}
.section{border-top:1px solid #e6ebf0;padding-top:14px;margin-top:14px}.section h2{font-size:19px;margin:0 0 10px}
.order{display:flex;justify-content:space-between;border-bottom:1px solid #edf0f3;padding:9px 0;font-size:16px}
.user{font-weight:600}.detail{color:#657386}.empty{color:#657386;font-size:16px}
</style></head>
<body><section class="panel">
<h1 th:text="${itemName}">Warframe Market</h1>
<div class="sub" th:text="${itemDescription}">实时订单</div>
<div class="summary"><div class="price"><div class="label">最低卖价</div><div class="value" th:text="${lowestSellPrice}">暂无</div></div>
<div class="price"><div class="label">最高买价</div><div class="value" th:text="${highestBuyPrice}">暂无</div></div></div>
<div class="section"><h2>卖家（<span th:text="${sellerCount}">0</span>）</h2>
  <div class="order" th:each="seller : ${sellers}"><span class="user" th:text="${seller.userName}">-</span><span class="detail"><span th:text="${seller.platinum}">0</span> 白金 × <span th:text="${seller.quantity}">0</span></span></div>
  <div class="empty" th:if="${sellerCount == 0}">暂无卖家</div>
</div>
<div class="section"><h2>买家（<span th:text="${buyerCount}">0</span>）</h2>
  <div class="order" th:each="buyer : ${buyers}"><span class="user" th:text="${buyer.userName}">-</span><span class="detail"><span th:text="${buyer.platinum}">0</span> 白金 × <span th:text="${buyer.quantity}">0</span></span></div>
  <div class="empty" th:if="${buyerCount == 0}">暂无买家</div>
</div>
</section></body></html>
$$)
ON CONFLICT (template_code) DO NOTHING;

-- JM 下载涉及较高网络、磁盘和 CPU 成本，开发基线默认关闭，确认上游
-- 配置和渠道文件大小限制后再从管理端启用。
INSERT INTO nnmbot.bot_command (
    code, name, game_code, description, enabled, show_in_help, output_mode,
    cooldown_seconds
)
VALUES (
    'jm', 'JM 下载', 'system', '下载指定 JM 漫画并返回压缩 PDF',
    FALSE, FALSE, 'TEXT_FILE', 30
)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    game_code = EXCLUDED.game_code,
    description = EXCLUDED.description,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO nnmbot.bot_command_alias (command_id, alias_name, boundary_policy)
SELECT command.id, seed.alias_name, 'WHITESPACE_REQUIRED'
FROM (VALUES
    ('jm'),
    ('/jm')
) AS seed(alias_name)
CROSS JOIN nnmbot.bot_command command
WHERE command.code = 'jm'
  AND NOT EXISTS (
      SELECT 1
      FROM nnmbot.bot_command_alias existing
      WHERE existing.command_id = command.id
        AND lower(existing.alias_name) = lower(seed.alias_name)
  );

UPDATE nnmbot.bot_command_template template
SET command_id = command.id,
    sort_order = 0,
    updated_at = CURRENT_TIMESTAMP
FROM nnmbot.bot_command command
WHERE template.template_code = 'status'
  AND command.code = 'status';

-- Each command owns its world-state template. The source row remains available
-- as an unassigned template for future manual variants.
INSERT INTO nnmbot.bot_command_template
    (template_code, name, command_id, sort_order, html_content, enabled)
SELECT
    'warframe-world-state-' || command.code,
    template.name || ' - ' || command.name,
    command.id,
    0,
    template.html_content,
    template.enabled
FROM nnmbot.bot_command_template template
CROSS JOIN nnmbot.bot_command command
WHERE template.template_code = 'warframe-world-state'
  AND command.game_code = 'warframe'
  AND command.code IN (
      'alerts', 'sorties', 'archon-hunts', 'void-trader', 'arbitration',
      'arbitration-table', 'daily-deals', 'invasions', 'fissures',
      'void-storms', 'steel-fissures', 'steel-path', 'cycles', 'ostrons',
      'entrati', 'solaris', 'duviri-cycle', 'nightwave'
  )
ON CONFLICT (template_code) DO UPDATE SET
    command_id = EXCLUDED.command_id,
    sort_order = EXCLUDED.sort_order,
    updated_at = CURRENT_TIMESTAMP;

UPDATE nnmbot.bot_command_template template
SET command_id = command.id,
    sort_order = 0,
    updated_at = CURRENT_TIMESTAMP
FROM nnmbot.bot_command command
WHERE template.template_code = 'warframe-market-orders'
  AND command.code = 'market';

INSERT INTO nnmbot.bot_command_channel (command_id, platform, output_mode)
SELECT command.id, platform.platform, 'TEXT_IMAGE'
FROM nnmbot.bot_command command
CROSS JOIN (VALUES ('ONEBOT'), ('QQ_OFFICIAL')) AS platform(platform)
WHERE command.code IN (
    'alerts', 'sorties', 'archon-hunts', 'void-trader', 'arbitration',
    'arbitration-table', 'daily-deals', 'invasions', 'fissures',
    'void-storms', 'steel-fissures', 'steel-path', 'cycles', 'ostrons',
    'entrati', 'solaris', 'duviri-cycle', 'nightwave', 'market'
)
ON CONFLICT (command_id, platform) DO NOTHING;

INSERT INTO nnmbot.bot_command_channel (command_id, platform, output_mode)
SELECT command.id, platform.platform, 'TEXT'
FROM nnmbot.bot_command command
CROSS JOIN (VALUES ('ONEBOT'), ('QQ_OFFICIAL')) AS platform(platform)
WHERE command.code IN (
    'riven-disposition', 'translation', 'market-riven', 'riven-market',
    'liches', 'sisters', 'perrin-sequence', 'gold-trash', 'silver-trash',
    'relics', 'open-relics', 'riven-analyse', 'calendar-1999',
    'subscribe', 'unsubscribe', 'update-market-items',
    'update-market-riven', 'update-sister', 'update-translation', 'wiki',
    'forum-loadout'
)
ON CONFLICT (command_id, platform) DO NOTHING;

INSERT INTO nnmbot.bot_command_channel (command_id, platform, output_mode)
SELECT command.id, platform.platform, 'TEXT_FILE'
FROM nnmbot.bot_command command
CROSS JOIN (VALUES ('ONEBOT'), ('QQ_OFFICIAL')) AS platform(platform)
WHERE command.code = 'jm'
ON CONFLICT (command_id, platform) DO NOTHING;

-- Recoverable runtime data from the PostgreSQL backup captured on 2026-08-26.
-- Relationships are rebuilt through stable business keys instead of old
-- sequence IDs. The obsolete bot_command_bot table is intentionally excluded;
-- robot capabilities now use bot_command_robot_role and
-- bot_robot_role_instance.

-- Keep the documented admin/admin123 development password while restoring the
-- administrator profile that existed in the backup.
UPDATE nnmbot.sys_user
SET nickname = 'admin',
    phone = '12345678',
    email = '3341780787qq@gmail.com',
    gender = '男',
    avatar = NULL,
    enabled = TRUE,
    created_at = TIMESTAMP '2026-08-15 12:35:05.092459',
    updated_at = TIMESTAMP '2026-08-21 16:03:43.155427'
WHERE username = 'admin';

INSERT INTO nnmbot.sys_user (
    username, password, nickname, phone, email, gender, avatar, enabled,
    created_at, updated_at
)
VALUES (
    '管理员',
    '$2a$10$xI4k8.PLZ2i61WxZCgyOAuqe3XsYBGZS68MHoKYExSe6aZXXvbpBS',
    '管理员',
    NULL,
    NULL,
    '男',
    NULL,
    TRUE,
    TIMESTAMP '2026-08-21 16:02:07.424269',
    TIMESTAMP '2026-08-21 16:02:07.424269'
)
ON CONFLICT (username) DO UPDATE SET
    nickname = EXCLUDED.nickname,
    phone = EXCLUDED.phone,
    email = EXCLUDED.email,
    gender = EXCLUDED.gender,
    avatar = EXCLUDED.avatar,
    enabled = EXCLUDED.enabled,
    updated_at = EXCLUDED.updated_at;

INSERT INTO nnmbot.sys_user_role (user_id, role_id)
SELECT user_account.id, role.id
FROM nnmbot.sys_user user_account
JOIN nnmbot.sys_role role ON role.code = 'R_ADMIN'
WHERE user_account.username = '管理员'
ON CONFLICT DO NOTHING;

INSERT INTO nnmbot.bot_channel_instance (
    platform, instance_id, display_name, bot_account_id, enabled,
    created_at, updated_at
)
VALUES
    (
        'ONEBOT',
        'nnmbot',
        '小号机器人',
        '3831958077',
        TRUE,
        TIMESTAMP '2026-08-21 11:51:34.101542',
        TIMESTAMP '2026-08-21 11:52:48.214780'
    ),
    (
        'QQ_OFFICIAL',
        'qq官方机器人',
        'qq官方机器人',
        '1904080813',
        TRUE,
        TIMESTAMP '2026-08-21 13:57:39.439424',
        TIMESTAMP '2026-08-21 13:57:39.439424'
    )
ON CONFLICT (instance_id) DO UPDATE SET
    platform = EXCLUDED.platform,
    display_name = EXCLUDED.display_name,
    bot_account_id = EXCLUDED.bot_account_id,
    enabled = EXCLUDED.enabled,
    updated_at = EXCLUDED.updated_at;

-- These values are encrypted application credentials, not plaintext secrets.
INSERT INTO nnmbot.bot_onebot_config (
    instance_id, connection_url, access_token, reconnect_interval_seconds
)
SELECT
    instance.id,
    'ws://123.206.232.72:3001',
    'enc:v1:1MmConxo1C9U85taJb40+WSDrOL0+IoXm1TwgpSL52wvsHvQAfa7JNYi88XL8gv4scBX6wewGMonxtP6O8ywMIO5qN5V044=',
    5
FROM nnmbot.bot_channel_instance instance
WHERE instance.instance_id = 'nnmbot'
ON CONFLICT (instance_id) DO UPDATE SET
    connection_url = EXCLUDED.connection_url,
    access_token = EXCLUDED.access_token,
    reconnect_interval_seconds = EXCLUDED.reconnect_interval_seconds;

INSERT INTO nnmbot.bot_qq_official_config (
    instance_id, app_id, client_secret, token_url, api_base_url, gateway_url,
    intents, gateway_connect_timeout_seconds, reconnect_delay_seconds
)
SELECT
    instance.id,
    '1904080813',
    'enc:v1:79OeS5am6OA2yBgwzYHOXggKvvgQIP/zs/c8tVrjUEsseP4ywBL3o5k6xlk9yY/IErKCER2gmZlR6eVy',
    'https://bots.qq.com/app/getAppAccessToken',
    'https://api.sgroup.qq.com',
    NULL,
    100663296,
    10,
    5
FROM nnmbot.bot_channel_instance instance
WHERE instance.instance_id = 'qq官方机器人'
ON CONFLICT (instance_id) DO UPDATE SET
    app_id = EXCLUDED.app_id,
    client_secret = EXCLUDED.client_secret,
    token_url = EXCLUDED.token_url,
    api_base_url = EXCLUDED.api_base_url,
    gateway_url = EXCLUDED.gateway_url,
    intents = EXCLUDED.intents,
    gateway_connect_timeout_seconds = EXCLUDED.gateway_connect_timeout_seconds,
    reconnect_delay_seconds = EXCLUDED.reconnect_delay_seconds;

INSERT INTO nnmbot.bot_scope (
    platform, scope_type, scope_key, bot_instance_id, display_name, enabled
)
SELECT
    'ONEBOT',
    'GROUP',
    '1019641911',
    instance.id,
    'nnm测试',
    TRUE
FROM nnmbot.bot_channel_instance instance
WHERE instance.instance_id = 'nnmbot'
ON CONFLICT (platform, scope_type, scope_key, bot_instance_id) DO UPDATE SET
    display_name = EXCLUDED.display_name,
    enabled = EXCLUDED.enabled;

INSERT INTO nnmbot.bot_group (
    code, name, description, enabled, created_at, updated_at
)
VALUES (
    'onebot_1019641911',
    'nnm测试',
    '由 nnmbot 发现',
    TRUE,
    TIMESTAMP '2026-08-23 01:52:20.496431',
    TIMESTAMP '2026-08-23 01:52:20.496431'
)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    enabled = EXCLUDED.enabled,
    updated_at = EXCLUDED.updated_at;

INSERT INTO nnmbot.bot_group_bot (
    group_id, bot_instance_id, platform, scope_type, scope_key, display_name,
    enabled
)
SELECT
    bot_group.id,
    instance.id,
    seed.platform,
    'GROUP',
    '1019641911',
    seed.display_name,
    TRUE
FROM (
    VALUES
        ('nnmbot', 'ONEBOT', 'nnm测试'),
        ('qq官方机器人', 'QQ_OFFICIAL', 'qq官方机器人')
) AS seed(instance_id, platform, display_name)
JOIN nnmbot.bot_group bot_group ON bot_group.code = 'onebot_1019641911'
JOIN nnmbot.bot_channel_instance instance
    ON instance.instance_id = seed.instance_id
ON CONFLICT (group_id, bot_instance_id, platform, scope_type, scope_key)
DO UPDATE SET
    display_name = EXCLUDED.display_name,
    enabled = EXCLUDED.enabled;

INSERT INTO nnmbot.bot_group_member (
    group_id, qq_id, member_openid, kook_id, qq_id_verified, qq_bound_at,
    display_name, member_role, enabled, points, total_earned, total_spent,
    version
)
SELECT
    bot_group.id,
    seed.qq_id,
    NULL,
    NULL,
    TRUE,
    NULL,
    seed.display_name,
    seed.member_role,
    TRUE,
    seed.points,
    seed.total_earned,
    seed.total_spent,
    seed.version
FROM (
    VALUES
        ('3831958077', '夢-', 'MEMBER', 0::BIGINT, 0::BIGINT, 0::BIGINT, 0),
        ('4018297298', 'nnm氏族机器人', 'MEMBER', 0::BIGINT, 0::BIGINT, 0::BIGINT, 0),
        ('3341780787', '[空格]', 'OWNER', 1::BIGINT, 10::BIGINT, 2::BIGINT, 3)
) AS seed(
    qq_id, display_name, member_role, points, total_earned, total_spent, version
)
JOIN nnmbot.bot_group bot_group ON bot_group.code = 'onebot_1019641911'
ON CONFLICT (group_id, qq_id) WHERE qq_id IS NOT NULL
DO UPDATE SET
    member_openid = EXCLUDED.member_openid,
    kook_id = EXCLUDED.kook_id,
    qq_id_verified = EXCLUDED.qq_id_verified,
    qq_bound_at = EXCLUDED.qq_bound_at,
    display_name = EXCLUDED.display_name,
    member_role = EXCLUDED.member_role,
    enabled = EXCLUDED.enabled,
    points = EXCLUDED.points,
    total_earned = EXCLUDED.total_earned,
    total_spent = EXCLUDED.total_spent,
    version = EXCLUDED.version;

INSERT INTO nnmbot.bot_group_point_transaction (
    group_id, member_id, command_code, transaction_type, amount,
    balance_after, idempotency_key, description, created_at
)
SELECT
    bot_group.id,
    member.id,
    seed.command_code,
    seed.transaction_type,
    seed.amount,
    seed.balance_after,
    seed.idempotency_key,
    seed.description,
    seed.created_at
FROM (
    VALUES
        (
            'sign-in',
            'SIGN_IN',
            10::BIGINT,
            10::BIGINT,
            'SIGN_IN:1:1:2026-08-26',
            '群组签到',
            TIMESTAMP '2026-08-26 11:57:40.136653'
        ),
        (
            'arbitration',
            'COMMAND_COST',
            -2::BIGINT,
            8::BIGINT,
            'COMMAND_COST:ONEBOT:arbitration:-391647784',
            '命令积分消耗',
            TIMESTAMP '2026-08-26 11:57:47.292111'
        ),
        (
            NULL,
            'ADMIN_ADJUST',
            -7::BIGINT,
            1::BIGINT,
            'ADMIN_ADJUSTMENT:f7c553af-2c47-4f47-8cf3-8bc0dff0c604',
            '后台管理员调整成员积分',
            TIMESTAMP '2026-08-26 13:07:17.663559'
        )
) AS seed(
    command_code, transaction_type, amount, balance_after, idempotency_key,
    description, created_at
)
JOIN nnmbot.bot_group bot_group ON bot_group.code = 'onebot_1019641911'
JOIN nnmbot.bot_group_member member
    ON member.group_id = bot_group.id
   AND member.qq_id = '3341780787'
ON CONFLICT (idempotency_key) DO NOTHING;

INSERT INTO nnmbot.bot_robot_role (
    code, name, description, enabled, created_at, updated_at
)
VALUES (
    '1',
    '1',
    '1',
    TRUE,
    TIMESTAMP '2026-08-23 10:24:09.191370',
    TIMESTAMP '2026-08-23 10:24:09.191370'
)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    enabled = EXCLUDED.enabled,
    updated_at = EXCLUDED.updated_at;

INSERT INTO nnmbot.bot_command_robot_role (command_id, robot_role_id)
SELECT command.id, robot_role.id
FROM nnmbot.bot_command command
CROSS JOIN nnmbot.bot_robot_role robot_role
WHERE command.code IN (
    'help', 'status', 'sign-in', 'bind', 'jm', 'wiki', 'arbitration',
    'market', 'forum-loadout'
)
  AND robot_role.code = '1'
ON CONFLICT (command_id, robot_role_id) DO NOTHING;

INSERT INTO nnmbot.bot_robot_role_instance (robot_role_id, bot_instance_id)
SELECT robot_role.id, instance.id
FROM nnmbot.bot_robot_role robot_role
CROSS JOIN nnmbot.bot_channel_instance instance
WHERE robot_role.code = '1'
  AND instance.instance_id = 'nnmbot'
ON CONFLICT (robot_role_id, bot_instance_id) DO NOTHING;

INSERT INTO nnmbot.bot_command_cost (
    command_id, cost_points, enabled, reward_points, reward_enabled,
    description, created_at, updated_at
)
SELECT
    command.id,
    rule.cost_points,
    rule.cost_enabled,
    rule.reward_points,
    rule.reward_enabled,
    NULL,
    TIMESTAMP '2026-08-26 11:10:25.180261',
    TIMESTAMP '2026-08-26 11:10:25.180261'
FROM nnmbot.bot_command command
JOIN (
    VALUES
        ('arbitration', 2::BIGINT, TRUE, 0::BIGINT, FALSE),
        -- 签到奖励由通用命令积分链结算，默认按业务日期只发放一次。
        ('sign-in', 0::BIGINT, FALSE, 10::BIGINT, TRUE)
) AS rule(command_code, cost_points, cost_enabled, reward_points, reward_enabled)
    ON rule.command_code = command.code
ON CONFLICT (command_id) DO UPDATE SET
    cost_points = EXCLUDED.cost_points,
    enabled = EXCLUDED.enabled,
    reward_points = EXCLUDED.reward_points,
    reward_enabled = EXCLUDED.reward_enabled,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;

-- Template contents are restored from the database backup. The newer baseline
-- templates remain available under their existing template codes.
INSERT INTO nnmbot.bot_command_template (
    template_code, name, html_content, enabled, created_at, updated_at
)
VALUES
    (
        'status',
        '运行状态卡片',
        $status$<!doctype html><html><head><meta charset="UTF-8"><style>
body{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}
.panel{background:#fff;border:1px solid #dfe5ef;border-radius:12px;padding:24px}
h1{font-size:28px;margin:0 0 20px}.grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}
.item{background:#f7f9fc;padding:14px;border-radius:8px}.label{color:#667085;font-size:13px}
.value{font-size:20px;font-weight:700;margin-top:6px}
</style></head><body><div class="panel"><h1>NnmBot 运行状态</h1>
<div class="grid"><div class="item"><div class="label">机器人账号</div><div class="value" th:text="${accountId}"></div></div>
<div class="item"><div class="label">会话类型</div><div class="value" th:text="${scope}"></div></div>
<div class="item"><div class="label">运行时长</div><div class="value" th:text="${uptime}"></div></div>
<div class="item"><div class="label">内存</div><div class="value" th:text="${memory}"></div></div></div>
</div></body></html>$status$,
        TRUE,
        TIMESTAMP '2026-08-22 02:36:12.539810',
        TIMESTAMP '2026-08-23 10:42:44.482464'
    ),
    (
        'market-orders',
        '市场报价卡片',
        $market$<!doctype html><html><head><meta charset="UTF-8"><style>
body{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}
.panel{background:#fff;border:1px solid #dfe5ef;border-radius:12px;padding:24px}
h1{font-size:28px;margin:0}.sub{color:#667085;margin:8px 0 20px}.price{font-size:24px;font-weight:700;color:#13795b}
</style></head><body><div class="panel"><h1 th:text="${itemName}"></h1>
<div class="sub" th:text="${itemDescription}"></div><div class="price" th:text="${lowestSellPrice}"></div>
<div class="sub">参考中位价：<span th:text="${medianPrice}"></span></div></div></body></html>$market$,
        TRUE,
        TIMESTAMP '2026-08-22 02:36:12.539810',
        TIMESTAMP '2026-08-22 02:36:12.539810'
    ),
    (
        'arbitrations',
        '仲裁轮换卡片',
        $arbitrations$<!doctype html><html><head><meta charset="UTF-8"><style>
body{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}
.panel{background:#fff;border:1px solid #dfe5ef;border-radius:12px;padding:24px}
h1{font-size:28px;margin:0 0 18px}.item{padding:12px 0;border-bottom:1px solid #e7ebf2}
.node{font-size:18px;font-weight:700}.meta{color:#667085;margin-top:4px}
</style></head><body><div class="panel"><h1>仲裁轮换</h1>
<div class="item" th:each="event : ${events}"><div class="node" th:text="${event.node}"></div>
<div class="meta"><span th:text="${event.type}"></span> · <span th:text="${event.remaining}"></span></div></div>
</div></body></html>$arbitrations$,
        TRUE,
        TIMESTAMP '2026-08-22 02:36:12.539810',
        TIMESTAMP '2026-08-22 02:36:12.539810'
    )
ON CONFLICT (template_code) DO UPDATE SET
    name = EXCLUDED.name,
    html_content = EXCLUDED.html_content,
    enabled = EXCLUDED.enabled,
    updated_at = EXCLUDED.updated_at;

-- Restore mutable command settings without replacing the newer command
-- metadata and aliases maintained by the current baseline.
UPDATE nnmbot.bot_command command
SET enabled = seed.enabled,
    show_in_help = seed.show_in_help,
    output_mode = seed.output_mode,
    permission_override = seed.permission_override,
    group_role_required = seed.group_role_required,
    cooldown_seconds = seed.cooldown_seconds,
    updated_at = seed.updated_at
FROM (
    VALUES
        ('status', TRUE, TRUE, 'TEXT_IMAGE', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-23 10:38:54.042272'),
        ('help', TRUE, TRUE, 'TEXT', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-22 02:36:11.812122'),
        ('market', TRUE, TRUE, 'TEXT_IMAGE', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-22 02:36:11.812122'),
        ('arbitration', TRUE, TRUE, 'IMAGE', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-26 11:10:09.495834'),
        ('bind', TRUE, TRUE, 'TEXT', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-23 16:07:54.491956'),
        ('sign-in', TRUE, TRUE, 'TEXT', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-23 16:07:54.491956'),
        ('duviri-cycle', TRUE, TRUE, 'TEXT', NULL::VARCHAR, NULL::VARCHAR, 0::BIGINT,
            TIMESTAMP '2026-08-26 16:18:27.212129')
) AS seed(
    code, enabled, show_in_help, output_mode, permission_override,
    group_role_required, cooldown_seconds, updated_at
)
WHERE command.code = seed.code;

UPDATE nnmbot.bot_command_template template
SET command_id = command.id,
    sort_order = 0,
    updated_at = CURRENT_TIMESTAMP
FROM nnmbot.bot_command command
WHERE command.code = 'status'
  AND template.template_code = 'status';

UPDATE nnmbot.bot_command_template template
SET command_id = command.id,
    sort_order = 0,
    updated_at = CURRENT_TIMESTAMP
FROM nnmbot.bot_command command
WHERE command.code = 'arbitration'
  AND template.template_code = 'arbitrations';

INSERT INTO nnmbot.bot_command_channel (command_id, platform, output_mode)
SELECT command.id, 'ONEBOT', 'IMAGE'
FROM nnmbot.bot_command command
WHERE command.code = 'arbitration'
ON CONFLICT (command_id, platform) DO UPDATE SET
    output_mode = EXCLUDED.output_mode;

-- Operation logs are historical audit records. Target IDs retain their
-- original display values, while operator IDs are resolved by username.
INSERT INTO nnmbot.sys_operation_log (
    operator_id, operator_name, action, target_type, target_id, detail,
    ip_address, created_at
)
SELECT
    user_account.id,
    seed.operator_name,
    seed.action,
    seed.target_type,
    seed.target_id,
    seed.detail,
    seed.ip_address,
    seed.created_at
FROM (
    VALUES
        ('admin', 'BOT_INSTANCE_CREATE', 'BOT_INSTANCE', '1', '创建 OneBot 实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 11:51:34.246589'),
        ('admin', 'BOT_INSTANCE_UPDATE', 'BOT_INSTANCE', '1', '修改 OneBot 实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 11:52:32.762956'),
        ('admin', 'BOT_INSTANCE_UPDATE', 'BOT_INSTANCE', '1', '修改 OneBot 实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 11:52:48.183034'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '1', '停止机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 13:11:17.664483'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 13:11:47.846243'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '1', '停止机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 13:12:23.835322'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 13:13:30.578495'),
        ('admin', 'BOT_INSTANCE_CREATE', 'BOT_INSTANCE', '2', '创建 QQ 官方机器人实例 qq官方机器人', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 13:57:39.514831'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '2', '启动机器人实例 qq官方机器人', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 13:57:51.820029'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '2', '停止机器人实例 qq官方机器人', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 14:58:34.697090'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '2', '启动机器人实例 qq官方机器人', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 15:35:43.496720'),
        ('admin', 'USER_UPDATE', 'USER', '1', '修改用户 admin', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 15:56:14.506428'),
        ('admin', 'ROLE_GRANT_ASSIGN', 'ROLE', '2', '分配菜单 [1, 2, 3, 4, 5, 6, 8, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75]，权限 [1, 2, 3, 4, 17, 18, 19, 20, 21, 22, 23, 24, 25]', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 16:01:08.877312'),
        ('admin', 'USER_CREATE', 'USER', '2', '创建用户 管理员', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 16:02:07.605198'),
        ('管理员', 'USER_DISABLE', 'USER', '1', '禁用用户', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 16:03:24.990847'),
        ('管理员', 'USER_ENABLE', 'USER', '1', '启用用户', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 16:03:43.110395'),
        ('admin', 'MENU_DELETE', 'MENU', '71', '删除菜单 ChangeLog', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 18:55:11.094625'),
        ('admin', 'MENU_DELETE', 'MENU', '70', '删除菜单 OldVersion', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 18:55:22.638874'),
        ('admin', 'MENU_DELETE', 'MENU', '69', '删除菜单 LiteVersion', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 18:55:31.655732'),
        ('admin', 'MENU_DELETE', 'MENU', '68', '删除菜单 Document', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 18:55:44.507577'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '1', '停止机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 19:09:46.555833'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '1', '停止机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-21 23:06:04.513599'),
        ('admin', 'MENU_DELETE', 'MENU', '74', '删除菜单 BotFunctionManagement', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-22 10:51:34.922718'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '1', '停止机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-22 10:52:05.059967'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-25 10:16:59.058629'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-25 10:38:50.451554'),
        ('admin', 'BOT_INSTANCE_STOP', 'BOT_INSTANCE', '1', '停止机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-25 23:41:19.362204'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-25 23:41:37.721002'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-26 00:08:10.840809'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-26 10:07:17.146208'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-26 12:47:08.033225'),
        ('admin', 'BOT_INSTANCE_START', 'BOT_INSTANCE', '1', '启动机器人实例 nnmbot', '0:0:0:0:0:0:0:1', TIMESTAMP '2026-08-26 15:44:36.012531')
) AS seed(
    operator_name, action, target_type, target_id, detail, ip_address, created_at
)
LEFT JOIN nnmbot.sys_user user_account
    ON user_account.username = seed.operator_name
WHERE NOT EXISTS (
    SELECT 1
    FROM nnmbot.sys_operation_log existing
    WHERE existing.operator_name = seed.operator_name
      AND existing.action = seed.action
      AND existing.created_at = seed.created_at
);

-- 社区仓库每个 JSON 独立存储，保留当前值、最新上游值和首次导入值。
-- 刷新时只有命中人工字段的差异需要管理员逐字段审批。
CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_translation (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    chinese_name VARCHAR(500),
    english_name VARCHAR(500),
    prime_item BOOLEAN,
    set_item BOOLEAN,
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_translation_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_translation IS '社区 translation.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.chinese_name IS '社区中文名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.english_name IS '社区英文名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.prime_item IS '是否为 Prime 物品';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.set_item IS '是否为套装物品';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_alias (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    alias_name VARCHAR(500),
    target_name VARCHAR(500),
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_alias_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_alias IS '社区 alias.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.alias_name IS '社区中文别名';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.target_name IS '别名对应的英文目标名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_nodes (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    unique_name VARCHAR(500),
    display_name VARCHAR(500),
    system_index INTEGER,
    system_name VARCHAR(200),
    node_type INTEGER,
    mastery_req INTEGER,
    mission_index INTEGER,
    faction_index INTEGER,
    min_enemy_level INTEGER,
    max_enemy_level INTEGER,
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_nodes_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_nodes IS '社区 nodes.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.unique_name IS '游戏内部节点 uniqueName';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.display_name IS '社区节点显示名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.system_index IS '节点所属星系索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.system_name IS '节点所属星系名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.node_type IS '节点类型索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.mastery_req IS '节点段位要求';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.mission_index IS '节点任务类型索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.faction_index IS '节点敌对派系索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.min_enemy_level IS '节点最低敌人等级';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.max_enemy_level IS '节点最高敌人等级';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_reward_pool (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    unique_name VARCHAR(500),
    reward_count INTEGER,
    rewards_json TEXT,
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_reward_pool_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_reward_pool IS '社区 reward_pool.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.unique_name IS '游戏内部奖励池 uniqueName';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.reward_count IS '奖励数组元素数量';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.rewards_json IS '完整奖励数组 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_state_translation (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    unique_name VARCHAR(500),
    display_name VARCHAR(500),
    description TEXT,
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_state_translation_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_state_translation IS '社区 state_translation.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.unique_name IS '动态世界状态内部 uniqueName';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.display_name IS '动态世界状态显示名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.description IS '动态世界状态说明';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_market_element (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    element_code VARCHAR(200),
    display_name VARCHAR(200),
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_market_element_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_market_element IS '社区 market_element.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.element_code IS 'Warframe Market 元素代码';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.display_name IS '社区中文显示名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_market_riven_tion (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    effect VARCHAR(500),
    attribute_group VARCHAR(100),
    negative_only BOOLEAN,
    positive_is_negative BOOLEAN,
    prefix VARCHAR(100),
    search_only BOOLEAN,
    suffix VARCHAR(100),
    units VARCHAR(100),
    url_name VARCHAR(200),
    exclusive_to_json TEXT,
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_market_riven_tion_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_market_riven_tion IS '社区 market_riven_tion.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.source_id IS '社区源记录 ids';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.effect IS 'Riven 词条效果说明';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.attribute_group IS 'Riven 属性分组';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.negative_only IS '是否仅用于负面词条';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.positive_is_negative IS '正值是否按负面效果解释';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.prefix IS 'Riven 词条前缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.search_only IS '是否仅用于搜索匹配';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.suffix IS 'Riven 词条后缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.units IS 'Riven 词条数值单位';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.url_name IS 'Warframe Market URL 属性名';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.exclusive_to_json IS '词条适用武器类型 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_market_riven_tion_alias (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    attribute_code VARCHAR(200),
    alias_name VARCHAR(200),
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_market_riven_tion_alias_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_market_riven_tion_alias IS '社区 market_riven_tion_alias.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.attribute_code IS 'Riven 属性代码';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.alias_name IS 'Riven 属性中文别名';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_riven_analyse_trend (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    display_name VARCHAR(500),
    prefix VARCHAR(100),
    suffix VARCHAR(100),
    archwing DOUBLE PRECISION,
    melee_value DOUBLE PRECISION,
    pistol DOUBLE PRECISION,
    rifle DOUBLE PRECISION,
    shotgun DOUBLE PRECISION,
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_riven_analyse_trend_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_riven_analyse_trend IS '社区 riven_analyse_trend.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.display_name IS 'Riven 分析趋势名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.prefix IS 'Riven 分析词条前缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.suffix IS 'Riven 分析词条后缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.archwing IS 'Archwing 武器趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.melee_value IS '近战武器趋势系数，对应上游 melle 字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.pistol IS '手枪趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.rifle IS '步枪趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.shotgun IS '霰弹枪趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.updated_at IS '最后更新时间';

CREATE TABLE IF NOT EXISTS nnmbot.wf_community_dataset_riven_trend (
    id BIGSERIAL PRIMARY KEY,
    record_key VARCHAR(500) NOT NULL,
    record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY',
    source_id BIGINT,
    trend_time VARCHAR(32),
    new_dot VARCHAR(32),
    new_num DOUBLE PRECISION,
    old_dot VARCHAR(32),
    old_num DOUBLE PRECISION,
    trend_name VARCHAR(500),
    weapon_type VARCHAR(100),
    current_json TEXT NOT NULL,
    upstream_json TEXT NOT NULL,
    original_json TEXT NOT NULL,
    manual_fields_json TEXT NOT NULL DEFAULT '{}',
    upstream_hash VARCHAR(64) NOT NULL,
    source_hash VARCHAR(64) NOT NULL,
    manual_modified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_community_dataset_riven_trend_key UNIQUE (record_key)
);
COMMENT ON TABLE nnmbot.wf_community_dataset_riven_trend IS '社区 riven_trend.json 数据集记录';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.id IS '数据集记录主键';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.record_key IS '从 JSON 推断的稳定记录标识';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.trend_time IS 'Riven 趋势时间，对应上游 isDate 字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.new_dot IS '变化后的倾向点位';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.new_num IS '变化后的倾向数值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.old_dot IS '变化前的倾向点位';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.old_num IS '变化前的倾向数值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.trend_name IS 'Riven 趋势名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.weapon_type IS 'Riven 趋势武器类型';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.current_json IS '当前运行时生效 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.upstream_json IS '最近一次上游 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.original_json IS '首次导入时的原始 JSON';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.manual_fields_json IS '人工修改的顶层字段及其值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.upstream_hash IS '上游记录 JSON SHA-256';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.source_hash IS '数据源记录摘要，供刷新幂等判断';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.manual_modified IS '是否存在人工修改字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.enabled IS '记录是否参与运行时查询';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.updated_at IS '最后更新时间';

-- 人工翻译独立于社区 translation.json，社区刷新不会覆盖该表。
CREATE TABLE IF NOT EXISTS nnmbot.wf_manual_translation (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    locale VARCHAR(20) NOT NULL,
    translated_name VARCHAR(500) NOT NULL,
    remark TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_wf_manual_translation_item_locale UNIQUE (item_id, locale)
);
COMMENT ON TABLE nnmbot.wf_manual_translation IS '管理员维护的物品人工翻译';
COMMENT ON COLUMN nnmbot.wf_manual_translation.id IS '人工翻译主键';
COMMENT ON COLUMN nnmbot.wf_manual_translation.item_id IS '对应人工物品主键，使用应用层逻辑关联';
COMMENT ON COLUMN nnmbot.wf_manual_translation.locale IS '语言区域编码';
COMMENT ON COLUMN nnmbot.wf_manual_translation.translated_name IS '人工翻译名称';
COMMENT ON COLUMN nnmbot.wf_manual_translation.remark IS '管理员备注';
COMMENT ON COLUMN nnmbot.wf_manual_translation.enabled IS '是否参与运行时解析';
COMMENT ON COLUMN nnmbot.wf_manual_translation.created_at IS '首次写入时间';
COMMENT ON COLUMN nnmbot.wf_manual_translation.updated_at IS '最后更新时间';

-- record_key 用于单个数据集内的稳定定位，enabled 用于快速筛选运行中记录。
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_translation_enabled
    ON nnmbot.wf_community_dataset_translation (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_alias_enabled
    ON nnmbot.wf_community_dataset_alias (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_nodes_enabled
    ON nnmbot.wf_community_dataset_nodes (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_reward_pool_enabled
    ON nnmbot.wf_community_dataset_reward_pool (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_state_translation_enabled
    ON nnmbot.wf_community_dataset_state_translation (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_market_element_enabled
    ON nnmbot.wf_community_dataset_market_element (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_market_riven_tion_enabled
    ON nnmbot.wf_community_dataset_market_riven_tion (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_market_riven_tion_alias_enabled
    ON nnmbot.wf_community_dataset_market_riven_tion_alias (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_riven_analyse_trend_enabled
    ON nnmbot.wf_community_dataset_riven_analyse_trend (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_community_dataset_riven_trend_enabled
    ON nnmbot.wf_community_dataset_riven_trend (enabled, record_key);
CREATE INDEX IF NOT EXISTS idx_wf_manual_translation_enabled_item
    ON nnmbot.wf_manual_translation (enabled, item_id, locale);

COMMIT;
