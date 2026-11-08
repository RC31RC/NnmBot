// Runs only against an isolated temporary cluster and never reads application connections.
const assert = require("node:assert/strict");
const { withPostgres } = require("./postgres-test-cluster.cjs");

const groupTables = ["bot_group_bot", "bot_group_member", "bot_group_point_transaction",
  "bot_group_member_merge", "wf_group_watch_rule"];
const relations = groupTables.map(table => [table, `fk_${table}_group`])
  .concat([["bot_scope_member", "fk_bot_scope_member_scope"]]);

function verifyConstraints(query) {
  for (const [table, constraint] of relations) {
    assert.equal(query(`SELECT confdeltype::text || ':' || convalidated::text FROM pg_constraint
      WHERE conrelid='nnmbot.${table}'::regclass AND conname='${constraint}'`), "c:true", constraint);
  }
}

withPostgres(async ({ apply, query }) => {
  apply("sql/init.sql");
  verifyConstraints(query);
  for (const column of ["retry_expires_at", "objects_released_at"]) {
    assert.equal(query(`SELECT data_type FROM information_schema.columns
      WHERE table_schema='nnmbot' AND table_name='wf_official_dataset_batch' AND column_name='${column}'`),
    "timestamp with time zone", column);
  }
  apply("nnm-app/src/test/resources/db/bot-group-roots.sql");
  apply("nnm-app/src/test/resources/db/group-cascade-cleanup.sql");
  assert.throws(() => query(`INSERT INTO nnmbot.bot_group_bot
    (id, group_id, bot_instance_id, platform, scope_type, scope_key)
    VALUES (92003, 91999, 93001, 'ONEBOT', 'GROUP', 'missing-parent')`, false),
  /fk_bot_group_bot_group/i);
  query("BEGIN; DELETE FROM nnmbot.bot_group WHERE id=91001; ROLLBACK;");
  for (const table of groupTables)
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table} WHERE group_id=91001`), "1", `${table}: rollback`);
  query("DELETE FROM nnmbot.bot_group WHERE id=91001");
  for (const table of groupTables) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table} WHERE group_id=91001`), "0", table);
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table} WHERE group_id=91002`), "1", `${table}: other group`);
  }
  query("DELETE FROM nnmbot.bot_scope WHERE id=99001");
  assert.equal(query("SELECT count(*) FROM nnmbot.bot_scope_member WHERE scope_id=99001"), "0");
  assert.equal(query("SELECT count(*) FROM nnmbot.bot_scope_member WHERE scope_id=99002"), "1");
  assert.equal(query("SELECT count(*) FROM nnmbot.bot_channel_instance WHERE id=93001"), "1");
  assert.equal(query("SELECT count(*) FROM nnmbot.sys_user WHERE id=93101"), "1");
  console.log("PASS baseline constraints, group cascade, rollback, scope members and shared identities");
}).catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
