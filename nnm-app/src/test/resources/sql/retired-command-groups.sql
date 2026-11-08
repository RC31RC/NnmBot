-- 隔离库夹具：模拟旧开发库残留关联表、页面、权限及角色授权。
CREATE TABLE nnmbot.bot_command_group (id bigint PRIMARY KEY);
CREATE TABLE nnmbot.bot_command_group_member (
    id bigint PRIMARY KEY,
    group_id bigint REFERENCES nnmbot.bot_command_group(id)
);
CREATE SEQUENCE nnmbot.bot_command_group_id_seq;
CREATE SEQUENCE nnmbot.bot_command_group_member_id_seq;
INSERT INTO nnmbot.bot_command_group (id) VALUES (987654321);
INSERT INTO nnmbot.bot_command_group_member (id, group_id) VALUES (987654321, 987654321);
INSERT INTO nnmbot.sys_menu (id, name, path, component, title)
VALUES (987654321, 'BotCommandGroups', 'command-groups', '/bot/command-group', '关联命令');
INSERT INTO nnmbot.sys_role_menu (role_id, menu_id) VALUES (1, 987654321);
INSERT INTO nnmbot.sys_permission (id, code, name, type, menu_id)
VALUES (987654321, 'bot:command-group:view', '查看关联命令', 'BUTTON', 987654321);
INSERT INTO nnmbot.sys_role_permission (role_id, permission_id) VALUES (1, 987654321);
