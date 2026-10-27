-- Restore the former binding shape only inside a disposable SQL verification cluster.
ALTER TABLE nnmbot.bot_group_bot DROP COLUMN reply_priority;
ALTER TABLE nnmbot.bot_group_bot DROP CONSTRAINT uq_bot_group_bot;
-- Formerly the same physical scope could belong to several logical groups.
ALTER TABLE nnmbot.bot_group_bot ADD CONSTRAINT uq_bot_group_bot
    UNIQUE (group_id, bot_instance_id, platform, scope_type, scope_key);

INSERT INTO nnmbot.bot_group (id, code, name, enabled)
VALUES (91001, 'reply-test-a', 'Reply A', true),
       (91002, 'reply-test-b', 'Reply B', true);
INSERT INTO nnmbot.bot_group_bot
    (id, group_id, bot_instance_id, platform, scope_type, scope_key, enabled)
VALUES (92001, 91001, 93001, 'ONEBOT', 'GROUP', '94001', true);
