const assert = require('node:assert/strict');
const { withPostgres } = require('./postgres-test-cluster.cjs');

withPostgres(async ({ apply, query }) => {
  apply('sql/init.sql');
  assert.equal(query('SELECT count(*) FROM nnmbot.wf_official_dataset_file'), '71');
  assert.equal(query('SELECT count(*) FROM nnmbot.wf_community_dataset_lock'), '4');
  // 模拟本轮之前的已用开发库：保留一个已采纳文件，再执行实际手工升级脚本。
  query("UPDATE nnmbot.wf_official_dataset_file SET adopted_sha256=repeat('a',64), adopted_record_count=10 WHERE product_path='equipment/warframes.json.gz'");
  query("DELETE FROM nnmbot.wf_official_dataset_file WHERE product_path<>'equipment/warframes.json.gz'");
  query('ALTER TABLE nnmbot.wf_official_dataset_file DROP COLUMN data_revision');
  query('ALTER TABLE nnmbot.wf_community_dataset_lock DROP COLUMN data_revision');
  apply('sql/01_dataset_cache_revision.sql');
  assert.equal(query('SELECT count(*) FROM nnmbot.wf_official_dataset_file'), '71');
  assert.equal(query("SELECT adopted_record_count FROM nnmbot.wf_official_dataset_file WHERE product_path='equipment/warframes.json.gz'"), '10');
  assert.equal(query("SELECT adopted_sha256 FROM nnmbot.wf_official_dataset_file WHERE product_path='equipment/warframes.json.gz'"), 'a'.repeat(64));
  assert.equal(query('SELECT sum(data_revision) FROM nnmbot.wf_official_dataset_file'), '0');
  assert.equal(query('SELECT sum(data_revision) FROM nnmbot.wf_community_dataset_lock'), '0');
  query("BEGIN; UPDATE nnmbot.wf_official_dataset_file SET data_revision=data_revision+1 WHERE product_path='equipment/warframes.json.gz'; ROLLBACK;");
  assert.equal(query("SELECT data_revision FROM nnmbot.wf_official_dataset_file WHERE product_path='equipment/warframes.json.gz'"), '0');
  assert.throws(() => query("UPDATE nnmbot.wf_community_dataset_lock SET data_revision=-1 WHERE dataset='alias'", false));
  console.log('PASS existing database upgrade preserves adopted metadata, seeds file locks, and constrains independent revisions');
}).catch(error => { console.error(error); process.exitCode = 1; });
