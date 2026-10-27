const assert = require("node:assert/strict");
const { withPostgres } = require("./postgres-test-cluster.cjs");

withPostgres(({ apply, query }) => {
  apply("docs/sql/init.sql");
  const template = query(`SELECT html_content FROM nnmbot.bot_command_template
    WHERE template_code='arbitrations'`);
  assert(template.includes("仲裁排期"));
  assert(template.includes("当前范围暂无可用排期"));
  assert.equal(query(`SELECT count(*) FROM (
    SELECT lower(btrim(code)) FROM nnmbot.bot_command
    GROUP BY lower(btrim(code)) HAVING count(*) > 1
  ) duplicates`), "0");
  assert.equal(query(`SELECT count(*) FROM (
    SELECT lower(btrim(alias_name)) FROM nnmbot.bot_command_alias
    GROUP BY lower(btrim(alias_name)) HAVING count(DISTINCT command_id) > 1
  ) duplicates`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command_alias a
    JOIN nnmbot.bot_command c ON c.id=a.command_id
    WHERE c.code='descents'
      AND lower(btrim(a.alias_name)) IN ('深层下降','descent','descendia')`), "3");
  assert.equal(query("SELECT count(*) FROM information_schema.tables"
    + " WHERE table_schema='nnmbot' AND table_name='wf_arbitration_schedule'"), "0",
  "Dynamic arbitration snapshots must not be persisted");
  assert.equal(query("SELECT count(*) FROM information_schema.tables"
    + " WHERE table_schema='nnmbot' AND table_name='wf_community_arbitration_essence'"), "1",
  "Community arbitration essence remains persistent");
  console.log("PASS command/trigger uniqueness and in-memory arbitration boundary");
}).catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
