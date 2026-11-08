-- 两个群组共用同一机器人，验证删除一个群组不会删除实例或另一个群组。
INSERT INTO nnmbot.bot_channel_instance (id, platform, instance_id, display_name, bot_account_id)
VALUES (93001, 'ONEBOT', 'cascade-test-bot', 'Cascade test bot', 'cascade-test-account');
INSERT INTO nnmbot.bot_group_bot (id, group_id, bot_instance_id, platform, scope_type, scope_key) VALUES
(92001, 91001, 93001, 'ONEBOT', 'GROUP', '94001'),
(92002, 91002, 93001, 'ONEBOT', 'GROUP', '94002');
INSERT INTO nnmbot.bot_group_member (id, group_id, qq_id) VALUES
(95001, 91001, 'member-one'), (95002, 91002, 'member-two');
INSERT INTO nnmbot.bot_group_point_transaction
(id, group_id, member_id, transaction_type, amount, balance_after, idempotency_key) VALUES
(96001, 91001, 95001, 'SIGN_IN', 5, 5, 'cascade-point-one'),
(96002, 91002, 95002, 'SIGN_IN', 5, 5, 'cascade-point-two');
INSERT INTO nnmbot.bot_group_member_merge (id, group_id, source_member_id, target_member_id) VALUES
(97001, 91001, 95101, 95001), (97002, 91002, 95102, 95002);
INSERT INTO nnmbot.wf_group_watch_rule (id, group_id, member_id, watch_type, watch_key, watch_value) VALUES
(98001, 91001, 95001, 'fissures', 'filter', '*'),
(98002, 91002, 95002, 'fissures', 'filter', '*');
-- 平台作用域独立维护；删除作用域仅级联清理该作用域内的成员绑定。
INSERT INTO nnmbot.sys_user (id, username, password) VALUES
(93101, 'cascade-test-user', 'test-fixture-only');
INSERT INTO nnmbot.bot_scope (id, platform, scope_type, scope_key, bot_instance_id) VALUES
(99001, 'ONEBOT', 'GROUP', '94001', 93001),
(99002, 'ONEBOT', 'GROUP', '94002', 93001);
INSERT INTO nnmbot.bot_scope_member (id, scope_id, user_id, platform_user_id) VALUES
(99101, 99001, 93101, 'scope-member-one'), (99102, 99002, 93101, 'scope-member-two');
