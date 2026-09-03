-- 社区数据允许管理员补充自维护记录，来源字段用于隔离上游刷新和本地维护边界。
ALTER TABLE nnmbot.wf_community_dataset_translation
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_alias
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_nodes
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_reward_pool
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_state_translation
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_market_element
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_market_riven_tion
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_market_riven_tion_alias
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_riven_analyse_trend
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';

ALTER TABLE nnmbot.wf_community_dataset_riven_trend
    ADD COLUMN IF NOT EXISTS record_source VARCHAR(32) NOT NULL DEFAULT 'COMMUNITY';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.record_source
    IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';
