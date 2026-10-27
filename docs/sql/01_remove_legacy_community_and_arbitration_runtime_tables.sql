-- 本地开发手工清理已退役通用导入结构；人工 community 资料表继续保留。
-- 只列明已退役对象，禁止按 wf_community_ 前缀删除人工资料。
BEGIN;
DROP TABLE IF EXISTS nnmbot.wf_arbitration_schedule;
DROP TABLE IF EXISTS nnmbot.wf_community_raw,
    nnmbot.wf_community_record,
    nnmbot.wf_community_source;
COMMIT;
