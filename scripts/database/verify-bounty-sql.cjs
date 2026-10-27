const assert = require("node:assert/strict");
const { withPostgres } = require("./postgres-test-cluster.cjs");

withPostgres(({ apply, query }) => {
  apply("docs/sql/init.sql");
  const tables = ["wf_official_bounty_root", "wf_official_bounty_stages_items"];
  for (const table of tables) {
    const relation = `nnmbot.${table}`;
    assert.equal(query(`SELECT to_regclass('${relation}') IS NOT NULL`), "t", relation);
    assert.notEqual(query(`SELECT obj_description('${relation}'::regclass)`), "");
    assert.equal(query(`SELECT count(*) FROM pg_attribute
      WHERE attrelid='${relation}'::regclass AND attnum > 0 AND NOT attisdropped
        AND col_description(attrelid, attnum) IS NULL`), "0");
  }
  assert.equal(query(`SELECT count(*) FROM pg_constraint c JOIN pg_class t ON t.oid=c.conrelid
    JOIN pg_namespace n ON n.oid=t.relnamespace
    WHERE c.contype='f' AND n.nspname='nnmbot' AND t.relname LIKE 'wf_official_bounty_%'`), "0",
  "Static data trees use logical parent ids rather than foreign-key cascades");
  assert.equal(query("SELECT count(*) FROM information_schema.tables"
    + " WHERE table_schema='nnmbot' AND left(table_name,13)='wf_community_'"), "0",
  "Retired community-source tables must not enter the baseline");
  console.log("PASS typed official bounty model and retired community-source isolation");
}).catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
