-- 命令事务测试专用最小账户、流水和业务写入夹具，不参与应用初始化。
CREATE TABLE IF NOT EXISTS test_account (id INT PRIMARY KEY, balance INT);
CREATE TABLE IF NOT EXISTS test_ledger (kind VARCHAR(20));
CREATE TABLE IF NOT EXISTS test_business (id INT);
COMMENT ON TABLE test_account IS '命令事务测试积分账户';
COMMENT ON COLUMN test_account.id IS '测试账户编号';
COMMENT ON COLUMN test_account.balance IS '可回滚的积分余额';
COMMENT ON TABLE test_ledger IS '命令事务测试积分流水';
COMMENT ON COLUMN test_ledger.kind IS '扣费、奖励或投递补偿类型';
COMMENT ON TABLE test_business IS '命令事务测试业务写入';
COMMENT ON COLUMN test_business.id IS '业务写入标识';
DELETE FROM test_account;
INSERT INTO test_account VALUES (1, 100);
DELETE FROM test_ledger;
DELETE FROM test_business;
