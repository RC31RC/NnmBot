-- 仅供已手工初始化的本地开发数据库补入“管理通知”能力；全新数据库直接执行 init.sql。
-- 与数据源 schema 版本无关；脚本不由应用自动执行。
CREATE TABLE nnmbot.sys_admin_notice (
    id varchar(36) PRIMARY KEY,
    recipient_id bigint NOT NULL REFERENCES nnmbot.sys_user(id) ON DELETE CASCADE,
    source_key varchar(128) UNIQUE,
    title varchar(200) NOT NULL,
    content TEXT NOT NULL,
    required_permission varchar(100),
    publisher_id bigint REFERENCES nnmbot.sys_user(id) ON DELETE SET NULL,
    task_id varchar(36),
    task_type varchar(400),
    task_status varchar(16),
    preview_id varchar(36),
    preview_dataset varchar(100),
    created_at timestamp with time zone NOT NULL,
    expires_at timestamp with time zone NOT NULL
);
COMMENT ON TABLE nnmbot.sys_admin_notice IS '管理员个人持久化通知；预览到期即失效，其他消息按每条消息的到期时间清理';
COMMENT ON COLUMN nnmbot.sys_admin_notice.id IS '每位接收人独立的通知主键';
COMMENT ON COLUMN nnmbot.sys_admin_notice.recipient_id IS '收到通知的后台用户';
COMMENT ON COLUMN nnmbot.sys_admin_notice.source_key IS '后台任务终态与接收人的幂等键；人工公告为空';
COMMENT ON COLUMN nnmbot.sys_admin_notice.title IS '通知标题';
COMMENT ON COLUMN nnmbot.sys_admin_notice.content IS '通知正文或任务结果简述';
COMMENT ON COLUMN nnmbot.sys_admin_notice.required_permission IS '查看该任务消息要求的业务权限；公告为空';
COMMENT ON COLUMN nnmbot.sys_admin_notice.publisher_id IS '人工公告的发布人；系统消息为空';
COMMENT ON COLUMN nnmbot.sys_admin_notice.task_id IS '后台任务标识；人工公告为空';
COMMENT ON COLUMN nnmbot.sys_admin_notice.task_type IS '任务所属业务场景和文件范围';
COMMENT ON COLUMN nnmbot.sys_admin_notice.task_status IS '终态 COMPLETED、FAILED、CANCELLED';
COMMENT ON COLUMN nnmbot.sys_admin_notice.preview_id IS '任务生成的待审核预览标识';
COMMENT ON COLUMN nnmbot.sys_admin_notice.preview_dataset IS '社区预览所属数据集；官方预览为空';
COMMENT ON COLUMN nnmbot.sys_admin_notice.created_at IS '通知创建时间；任务消息通常为任务终态时间';
COMMENT ON COLUMN nnmbot.sys_admin_notice.expires_at IS '当前通知独立失效时间；预览消息不晚于预览过期时间';
-- 按用户和过期时间查询消息；单独的到期索引用于定时清理。
CREATE INDEX sys_admin_notice_recipient_expiry_idx ON nnmbot.sys_admin_notice(recipient_id, expires_at, created_at DESC);
CREATE INDEX sys_admin_notice_expiry_idx ON nnmbot.sys_admin_notice(expires_at);

-- 前端发布入口仅授予超级管理员，通知列表权限授予两类管理员。
INSERT INTO nnmbot.sys_menu (parent_id, name, path, component, title, icon, sort_order,
    keep_alive, visible, enabled, fixed_tab, hide_tab, full_page, iframe, created_at, updated_at)
SELECT parent.id, 'AdminNoticePublisher', 'notifications', '/system/notifications', '发布管理通知',
    'ri:notification-3-line', 96, true, true, true, false, false, false, false,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM nnmbot.sys_menu parent WHERE parent.name = 'System';
INSERT INTO nnmbot.sys_permission (code, name, type, menu_id, enabled, created_at, updated_at)
SELECT permission.code, permission.name, 'BUTTON', menu.id, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM nnmbot.sys_menu menu
CROSS JOIN (VALUES ('system:notification:view', '查看个人管理通知'),
                   ('system:notification:publish', '发布管理通知')) AS permission(code, name)
WHERE menu.name = 'AdminNoticePublisher';
-- 超级管理员拥有页面及所有通知权限；普通管理员只能读取自己的通知。
INSERT INTO nnmbot.sys_role_menu(role_id, menu_id)
SELECT role.id, menu.id FROM nnmbot.sys_role role CROSS JOIN nnmbot.sys_menu menu
WHERE role.code = 'R_SUPER' AND menu.name = 'AdminNoticePublisher';
INSERT INTO nnmbot.sys_role_permission(role_id, permission_id)
SELECT role.id, permission.id FROM nnmbot.sys_role role CROSS JOIN nnmbot.sys_permission permission
WHERE (role.code = 'R_SUPER' AND permission.code IN ('system:notification:view', 'system:notification:publish'))
   OR (role.code = 'R_ADMIN' AND permission.code = 'system:notification:view');
