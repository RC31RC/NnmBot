-- 仅供临时数据库回归测试：从初始化基线移除通知表后重建，不在已有数据库自动执行。
-- 通知先持久化再认领；提交结果未知的任务禁止自动重放平台副作用。
CREATE TABLE nnmbot.bot_notification_job (
    id uuid PRIMARY KEY,
    owner_id bigint NOT NULL,
    request jsonb NOT NULL,
    resources jsonb NOT NULL,
    scheduled_at timestamp with time zone NOT NULL,
    status character varying(24) NOT NULL,
    task_id uuid,
    report jsonb,
    error character varying(1000),
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    -- 状态显式区分待发送、取消请求、已知结果与结果未知。
    CONSTRAINT ck_bot_notification_job_status CHECK (status IN
      ('SCHEDULED','SUBMITTING','RUNNING','CANCEL_REQUESTED','COMPLETED','CANCELLED','FAILED','UNKNOWN'))
);
COMMENT ON TABLE nnmbot.bot_notification_job IS '跨节点即时与定时群通知任务及逐目标回执';
COMMENT ON COLUMN nnmbot.bot_notification_job.id IS '由发起人和提交编号派生的幂等任务标识';
COMMENT ON COLUMN nnmbot.bot_notification_job.owner_id IS '发起人系统用户主键，执行时重新检查停用状态和权限';
COMMENT ON COLUMN nnmbot.bot_notification_job.request IS '提交时的接收群组、组合内容和发送时间快照';
COMMENT ON COLUMN nnmbot.bot_notification_job.resources IS '已冻结图片资源清单及有效期';
COMMENT ON COLUMN nnmbot.bot_notification_job.scheduled_at IS '计划提交时间，统一保存为绝对时间';
COMMENT ON COLUMN nnmbot.bot_notification_job.status IS '持久化生命周期状态，UNKNOWN 禁止自动重发';
COMMENT ON COLUMN nnmbot.bot_notification_job.task_id IS '有界后台执行任务编号，定时任务认领后才生成';
COMMENT ON COLUMN nnmbot.bot_notification_job.report IS '逐群组逐平台消息的投递回执，取消不删除已有回执';
COMMENT ON COLUMN nnmbot.bot_notification_job.error IS '失败或结果未知原因摘要';
COMMENT ON COLUMN nnmbot.bot_notification_job.created_at IS '提交创建时间';
COMMENT ON COLUMN nnmbot.bot_notification_job.updated_at IS '状态最近更新时间，供超时核对和历史清理';
-- 按到期时间有界扫描待发送任务，多个节点用数据库行锁认领。
CREATE INDEX idx_bot_notification_job_due ON nnmbot.bot_notification_job (status, scheduled_at);
-- 按发起人查询最近任务并统计未完成任务配额。
CREATE INDEX idx_bot_notification_job_owner ON nnmbot.bot_notification_job (owner_id, created_at DESC);
-- 扫描超时执行任务和过期历史，避免全表遍历。
CREATE INDEX idx_bot_notification_job_updated ON nnmbot.bot_notification_job (status, updated_at);
