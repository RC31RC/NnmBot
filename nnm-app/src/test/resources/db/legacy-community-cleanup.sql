-- 隔离库夹具：模拟带内部外键的旧来源表与动态排期表，不进入生产初始化基线。
CREATE TABLE nnmbot.wf_community_cleanup_source (id bigint PRIMARY KEY);
COMMENT ON TABLE nnmbot.wf_community_cleanup_source IS '测试旧社区来源清理';
COMMENT ON COLUMN nnmbot.wf_community_cleanup_source.id IS '测试来源身份';
-- 子表引用旧来源，只允许整体清理退役对象，不连带其他业务表。
CREATE TABLE nnmbot.wf_community_cleanup_entry (
    id bigint PRIMARY KEY,
    source_id bigint REFERENCES nnmbot.wf_community_cleanup_source(id)
);
COMMENT ON TABLE nnmbot.wf_community_cleanup_entry IS '测试旧社区来源内部依赖清理';
COMMENT ON COLUMN nnmbot.wf_community_cleanup_entry.id IS '测试条目身份';
COMMENT ON COLUMN nnmbot.wf_community_cleanup_entry.source_id IS '测试来源依赖';
CREATE TABLE nnmbot.wf_arbitration_schedule (id bigint PRIMARY KEY);
COMMENT ON TABLE nnmbot.wf_arbitration_schedule IS '测试不再持久化的仲裁排期清理';
COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.id IS '旧排期身份';
