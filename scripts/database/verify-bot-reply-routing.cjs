// This verifier never reads application connection settings.
const assert = require("node:assert/strict");
const { withPostgres } = require("./postgres-test-cluster.cjs");

function verifyRouting(query) {
  assert.equal(query(`SELECT column_default FROM information_schema.columns
    WHERE table_schema='nnmbot' AND table_name='bot_group_bot'
      AND column_name='reply_priority'`), "100");
  assert.equal(query(`SELECT col_description('nnmbot.bot_group_bot'::regclass, attnum)
    IS NOT NULL FROM pg_attribute WHERE attrelid='nnmbot.bot_group_bot'::regclass
      AND attname='reply_priority'`), "t");
  assert.match(query(`SELECT pg_get_constraintdef(oid) FROM pg_constraint
    WHERE conrelid='nnmbot.bot_group_bot'::regclass AND conname='uq_bot_group_bot'`),
  /^UNIQUE \(bot_instance_id, platform, scope_type, scope_key\)$/);
  for (const value of [-1, 10001]) {
    assert.throws(() => query(`UPDATE nnmbot.bot_group_bot
      SET reply_priority=${value}`, false), /check constraint/i);
  }
}

withPostgres(async ({ apply, query }) => {
  apply("docs/sql/init.sql");
  query(`INSERT INTO nnmbot.bot_group_bot
    (id, group_id, bot_instance_id, platform, scope_type, scope_key)
    VALUES (92001, 91001, 93001, 'ONEBOT', 'GROUP', '94001')`);
  verifyRouting(query);
  assert.equal(query("SELECT reply_priority FROM nnmbot.bot_group_bot"), "100");
  query("UPDATE nnmbot.bot_group_bot SET reply_priority=7, enabled=false");
  const before = query("SELECT to_jsonb(b) FROM nnmbot.bot_group_bot b ORDER BY id");
  assert.throws(() => query(`INSERT INTO nnmbot.bot_group_bot
    (id, group_id, bot_instance_id, platform, scope_type, scope_key)
    VALUES (92002, 91002, 93001, 'ONEBOT', 'GROUP', '94001')`, false),
  /uq_bot_group_bot/i, "A platform scope cannot belong to multiple logical groups");
  assert.throws(() => query(`UPDATE nnmbot.bot_group_bot
    SET reply_priority=NULL`, false), /not-null constraint/i);
  assert.equal(query("SELECT to_jsonb(b) FROM nnmbot.bot_group_bot b ORDER BY id"), before,
    "Rejected changes must preserve bindings and routing preferences");
  query(`INSERT INTO nnmbot.bot_group_bot
    (id, group_id, bot_instance_id, platform, scope_type, scope_key)
    VALUES (92003, 91002, 93001, 'ONEBOT', 'GROUP', '94002')`);
  assert.equal(query("SELECT count(*) FROM nnmbot.bot_group_bot"), "2");
  console.log("PASS baseline reply priority, validation, comments, scope uniqueness and rejected-change rollback");
}).catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
