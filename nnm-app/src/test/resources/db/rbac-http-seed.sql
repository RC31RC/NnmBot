DELETE FROM sys_admin_notice;
DELETE FROM sys_operation_log;
DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM sys_role_menu;
DELETE FROM sys_user;
DELETE FROM sys_permission;
DELETE FROM sys_role;
DELETE FROM sys_menu;

ALTER TABLE sys_menu ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_menu ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_role ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_role ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_permission ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_permission ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

INSERT INTO sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
VALUES
    (NULL, 'Dashboard', '/dashboard', '/index/index', 'menus.dashboard.title', 'ri:pie-chart-line', 10,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'System', '/system', '/index/index', 'menus.system.title', 'ri:user-3-line', 20,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
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
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
    (NULL, 'Document', '', NULL, 'menus.help.document', 'ri:bill-line', 110,
        FALSE, FALSE, FALSE, FALSE, FALSE, 'https://www.artd.pro/docs/zh/', NULL, NULL, TRUE, TRUE),
    (NULL, 'LiteVersion', '', NULL, 'menus.help.liteVersion', 'ri:bus-2-line', 111,
        FALSE, FALSE, FALSE, FALSE, FALSE, 'https://www.artd.pro/docs/zh/guide/lite-version.html', NULL, NULL, TRUE, TRUE),
    (NULL, 'OldVersion', '', NULL, 'menus.help.oldVersion', 'ri:subway-line', 112,
        FALSE, FALSE, FALSE, FALSE, FALSE, 'https://www.artd.pro/v2/', NULL, NULL, TRUE, TRUE),
    (NULL, 'ChangeLog', '/change/log', '/change/log', 'menus.plan.log', 'ri:gamepad-line', 113,
        FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE);

INSERT INTO sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM (
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
        ('Widgets', 'ElementUI', '/outside/iframe/elementui', NULL, 'menus.widgets.elementUI', 'ri:apps-2-line', 53,
            FALSE, FALSE, FALSE, FALSE, TRUE, 'https://element-plus.org/zh-CN/component/overview.html', NULL, NULL, TRUE, TRUE),
        ('Examples', 'Permission', 'permission', NULL, 'menus.examples.permission.title', 'ri:fingerprint-line', 61,
            FALSE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Article', 'ArticleList', 'article-list', '/article/list', 'menus.article.articleList', 'ri:article-line', 71,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Article', 'ArticleDetail', 'detail/:id', '/article/detail', 'menus.article.articleDetail', NULL, 72,
            TRUE, FALSE, TRUE, FALSE, FALSE, NULL, '/article/article-list', NULL, FALSE, TRUE),
        ('Safeguard', 'SafeguardServer', 'server', '/safeguard/server', 'menus.safeguard.server', 'ri:hard-drive-3-line', 101,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
) AS seed(
    parent_name, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
JOIN sys_menu parent ON parent.name = seed.parent_name;

INSERT INTO sys_menu (
    parent_id, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
SELECT parent.id, seed.name, seed.path, seed.component, seed.title, seed.icon, seed.sort_order,
       seed.keep_alive, seed.fixed_tab, seed.hide_tab, seed.full_page, seed.iframe,
       seed.link, seed.active_path, seed.text_badge, seed.visible, seed.enabled
FROM (
    VALUES
        ('Permission', 'PermissionSwitchRole', 'switch-role', '/examples/permission/switch-role',
            'menus.examples.permission.switchRole', 'ri:contacts-line', 611,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Permission', 'PermissionButtonAuth', 'button-auth', '/examples/permission/button-auth',
            'menus.examples.permission.buttonAuth', 'ri:mouse-line', 612,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE),
        ('Permission', 'PermissionPageVisibility', 'page-visibility', '/examples/permission/page-visibility',
            'menus.examples.permission.pageVisibility', 'ri:user-3-line', 613,
            TRUE, FALSE, FALSE, FALSE, FALSE, NULL, NULL, NULL, TRUE, TRUE)
) AS seed(
    parent_name, name, path, component, title, icon, sort_order,
    keep_alive, fixed_tab, hide_tab, full_page, iframe, link, active_path,
    text_badge, visible, enabled
)
JOIN sys_menu parent ON parent.name = seed.parent_name;

-- 发布入口只出现在超级管理员的系统菜单；列表权限可授予其他管理员。
INSERT INTO sys_menu(parent_id, name, path, component, title, icon, sort_order, keep_alive, visible, enabled,
        fixed_tab, hide_tab, full_page, iframe)
SELECT id, 'AdminNoticePublisher', 'notifications', '/system/notifications', '发布管理通知',
        'ri:notification-3-line', 96, TRUE, TRUE, TRUE, FALSE, FALSE, FALSE, FALSE
FROM sys_menu WHERE name = 'System';

INSERT INTO sys_role (code, name, description, enabled, authority_level, data_scope)
VALUES
    ('R_SUPER', '超级管理员', '拥有全部管理权限', TRUE, 100, 'ALL'),
    ('R_ADMIN', '管理员', '负责日常用户管理', TRUE, 50, 'ALL'),
    ('R_USER', '只读用户', '仅访问基础功能', TRUE, 0, 'SELF');

INSERT INTO sys_permission (code, name, type, menu_id, enabled)
SELECT seed.code, seed.name, 'BUTTON', menu.id, TRUE
FROM (
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
        ('system:notification:view', '查看个人管理通知', 'AdminNoticePublisher'),
        ('system:notification:publish', '发布管理通知', 'AdminNoticePublisher'),
        ('add', '新增', 'PermissionButtonAuth'),
        ('edit', '编辑', 'PermissionButtonAuth'),
        ('delete', '删除', 'PermissionButtonAuth'),
        ('export', '导出', 'PermissionButtonAuth'),
        ('view', '查看', 'PermissionButtonAuth'),
        ('publish', '发布', 'PermissionButtonAuth'),
        ('config', '配置', 'PermissionButtonAuth'),
        ('manage', '管理', 'PermissionButtonAuth')
) AS seed(code, name, menu_name)
JOIN sys_menu menu ON menu.name = seed.menu_name;

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role.id, menu.id
FROM sys_role role
CROSS JOIN sys_menu menu
WHERE role.code = 'R_SUPER';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT role.id, permission.id
FROM sys_role role
CROSS JOIN sys_permission permission
WHERE role.code = 'R_SUPER';

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role.id, menu.id
FROM sys_role role
CROSS JOIN sys_menu menu
WHERE role.code = 'R_ADMIN'
  AND menu.name NOT IN ('Role', 'Menus', 'OperationLog', 'OnlineSession', 'PermissionPageVisibility', 'AdminNoticePublisher');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT role.id, permission.id
FROM sys_role role
CROSS JOIN sys_permission permission
WHERE role.code = 'R_ADMIN'
  AND (
      permission.code = 'system:role:view'
      OR (
          permission.code NOT LIKE 'system:role:%'
          AND permission.code NOT LIKE 'system:menu:%'
          AND permission.code NOT LIKE 'system:audit:%'
          AND permission.code NOT LIKE 'system:session:%'
          AND permission.code <> 'system:notification:publish'
      )
  );
