// Always creates its own disposable cluster; never reads application database settings.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const { execFileSync } = require("node:child_process");
const { root, withPostgres } = require("./postgres-test-cluster.cjs");

const hibernate = process.argv.includes("--hibernate");
const unsupported = process.argv.slice(2).filter(argument => argument !== "--hibernate");
assert.equal(unsupported.length, 0,
  `SQL history is merged into init.sql; unsupported options: ${unsupported.join(", ")}`);
const quote = value => `'${String(value).replaceAll("'", "''")}'`;

function javaFiles(directory) {
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
    const file = path.join(directory, entry.name);
    return entry.isDirectory() ? javaFiles(file) : file.endsWith(".java") ? [file] : [];
  });
}

function entityTables() {
  return ["nnm-core", "nnm-admin", "nnm-warframe"].flatMap(module =>
    javaFiles(path.join(root, module, "src/main/java")).flatMap(file => {
      const text = fs.readFileSync(file, "utf8");
      if (!/@Entity\b/.test(text)) return [];
      const table = text.match(/@Table\(\s*name\s*=\s*"([^"]+)"/)?.[1];
      assert(table, `Entity must declare its table: ${file}`);
      return [table];
    }));
}

withPostgres(async cluster => {
  const { apply, query, jdbcUrl } = cluster;
  apply("docs/sql/init.sql");

  const tables = entityTables();
  for (const table of tables) {
    assert.equal(query(`SELECT to_regclass(${quote(`nnmbot.${table}`)}) IS NOT NULL`), "t", table);
  }
  assert.equal(query("SELECT count(*) FROM information_schema.tables"
    + " WHERE table_schema='public' AND table_type='BASE TABLE'"), "0");
  assert.equal(query(`SELECT count(*) FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
    WHERE n.nspname='nnmbot' AND c.relkind='r' AND obj_description(c.oid) IS NULL`), "0",
  "Every business table needs a comment");
  assert.equal(query(`SELECT count(*) FROM pg_attribute a JOIN pg_class c ON c.oid=a.attrelid
    JOIN pg_namespace n ON n.oid=c.relnamespace
    WHERE n.nspname='nnmbot' AND c.relkind='r' AND a.attnum > 0 AND NOT a.attisdropped
      AND col_description(c.oid,a.attnum) IS NULL`), "0",
  "Every business column needs a comment");

  for (const [table, columns] of [
    ["bot_command_template", ["html_content", "markdown_content", "plain_text_content"]],
    ["bot_command_group_member", ["parameter_bindings"]],
    ["bot_notification_job", ["owner_id", "request", "resources", "scheduled_at", "status", "report"]],
    ["wf_official_weapon_root", ["bkey", "maintenance_type", "raw_json"]],
    ["wf_community_alias", ["record_key", "alias_name", "target_name"]],
    ["wf_community_riven_alias", ["attribute_code", "alias_name"]],
    ["wf_official_relic_states_entries_rewards_items", ["id", "bkey", "sort_order"]]
  ]) {
    assert.equal(query(`SELECT count(*) FROM information_schema.columns
      WHERE table_schema='nnmbot' AND table_name=${quote(table)}
        AND column_name IN (${columns.map(quote).join(",")})`), String(columns.length), table);
  }
  const officialModels = JSON.parse(fs.readFileSync(path.join(root,
    "nnm-warframe/src/main/resources/warframe/official-models.json"), "utf8"));
  const officialTreeTables = officialModels.models.map(model => quote(model.table)).join(",");
  const mainTables = officialModels.models.filter(model => !model.parentTable);
  assert.equal(mainTables.length, 71, "Every official JSON has an independent main table");
  assert.equal(query("SELECT to_regclass('nnmbot.wf_official_record') IS NULL"), "t",
    "No cross-dataset total-parent table is permitted");
  for (const model of mainTables) {
    assert.equal(query(`SELECT count(*) FROM information_schema.columns
      WHERE table_schema='nnmbot' AND table_name=${quote(model.table)} AND column_name='parent_id'`), "0",
      `Independent main table has no total-parent id: ${model.table}`);
    assert.equal(query(`SELECT count(*) FROM information_schema.columns
      WHERE table_schema='nnmbot' AND table_name=${quote(model.table)}
        AND column_name IN ('bkey','maintenance_type','raw_json')`), "3", model.table);
  }
  for (const model of officialModels.models.filter(model => model.parentTable)) {
    assert.equal(query(`SELECT count(*) FROM information_schema.columns
      WHERE table_schema='nnmbot' AND table_name=${quote(model.table)} AND column_name='bkey'
        AND data_type='bigint'`), "1", `Child bkey must be the parent id: ${model.table}`);
    assert.equal(query(`SELECT count(*) FROM information_schema.columns
      WHERE table_schema='nnmbot' AND table_name=${quote(model.table)} AND column_name='parent_id'`), "0",
      `Child tables use bkey, not parent_id: ${model.table}`);
  }

  assert.equal(query(`SELECT count(*) FROM pg_constraint c JOIN pg_class t ON t.oid=c.conrelid
    JOIN pg_namespace n ON n.oid=t.relnamespace WHERE c.contype='f' AND n.nspname='nnmbot'
      AND t.relname IN (${officialTreeTables})`), "0", "Official trees use direct logical bkey associations without database foreign keys");
  assert.equal(query("SELECT count(*) FROM information_schema.tables WHERE table_schema='nnmbot' AND table_name LIKE 'wf_official_%'"),
    String(officialModels.models.length + 4), "Official model and dataset tables use the official prefix");
  assert.equal(query("SELECT count(*) FROM information_schema.tables WHERE table_schema='nnmbot' AND table_name LIKE 'wf_community_%'"), "7",
    "Manual community tables remain isolated and use their source prefix");
  assert.equal(query("SELECT count(*) FROM information_schema.tables WHERE table_schema='nnmbot' AND table_name='wf_arbitration_schedule'"), "0",
    "Dynamic arbitration snapshots must remain in JVM memory");
  assert.equal(query("SELECT count(*) FROM nnmbot.wf_community_riven_alias"), "6", "Retain the existing manual aliases and combinations");
  assert.equal(query(`SELECT count(*) FROM information_schema.columns WHERE table_schema='nnmbot'
    AND table_name LIKE 'wf_community_%' AND column_name IN ('record_source','source_code','upstream_json','current_json')`), "0",
    "Community business rows have no source protocol metadata");
  const commands = JSON.parse(fs.readFileSync(path.join(root,
    "nnm-app/src/test/resources/db/command-catalog.json"), "utf8"));
  for (const command of commands) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command
      WHERE code=${quote(command.code)}`), "1", `Missing command configuration: ${command.code}`);
  }
  for (const table of ["sys_user", "bot_channel_instance", "bot_onebot_config",
    "bot_qq_official_config", "bot_scope", "bot_group", "sys_operation_log"]) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table}`), "0",
      `Baseline must not contain personal/runtime data: ${table}`);
  }
  for (const [template, code] of [
    ["wf-relics-luxe-v1", "relics"], ["wf-warframes-luxe-v1", "warframes"],
    ["wf-riven-weekly-luxe-v1", "riven-weekly"]
  ]) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command_template template
      JOIN nnmbot.bot_command command ON command.id=template.command_id
      WHERE template.template_code=${quote(template)} AND command.code=${quote(code)}
        AND NOT template.enabled`), "1", `${template}: bound to its command and opt-in`);
  }
  console.log(`PASS tables=${tables.length}, comments, official trees and community whitelist`);

  // 只在隔离库演练明确列出的退役导入表清理，人工资料表必须保留。
  const communityBefore = query(`SELECT row_to_json(t)::text FROM nnmbot.wf_community_riven_alias t ORDER BY id`);
  query(`CREATE TABLE nnmbot.wf_arbitration_schedule (id bigint PRIMARY KEY);
    CREATE TABLE nnmbot.wf_community_source (id bigint PRIMARY KEY);
    CREATE TABLE nnmbot.wf_community_record (id bigint PRIMARY KEY, source_id bigint REFERENCES nnmbot.wf_community_source);
    CREATE TABLE nnmbot.wf_community_raw (id bigint PRIMARY KEY, record_id bigint REFERENCES nnmbot.wf_community_record);
    CREATE VIEW nnmbot.cleanup_dependency_guard AS SELECT id FROM nnmbot.wf_community_source;
    CREATE TABLE nnmbot.wf_communityx_cleanup_guard (id bigint);
    COMMENT ON TABLE nnmbot.wf_communityx_cleanup_guard IS '测试字面前缀匹配，禁止连带删除';
    COMMENT ON COLUMN nnmbot.wf_communityx_cleanup_guard.id IS '测试非退役表身份';`);
  assert.throws(() => apply("docs/sql/01_remove_legacy_community_and_arbitration_runtime_tables.sql"),
    "Non-retired dependencies must block the whole cleanup transaction");
  assert.equal(query("SELECT to_regclass('nnmbot.wf_arbitration_schedule') IS NOT NULL"), "t",
    "Failed cleanup must roll back the arbitration drop as well");
  query("DROP VIEW nnmbot.cleanup_dependency_guard");
  apply("docs/sql/01_remove_legacy_community_and_arbitration_runtime_tables.sql");
  apply("docs/sql/01_remove_legacy_community_and_arbitration_runtime_tables.sql");
  assert.equal(query(`SELECT count(*) FROM pg_tables WHERE schemaname='nnmbot'
    AND tablename IN ('wf_community_source', 'wf_community_record', 'wf_community_raw', 'wf_arbitration_schedule')`), "0");
  assert.equal(query("SELECT to_regclass('nnmbot.wf_communityx_cleanup_guard') IS NOT NULL"), "t",
    "Cleanup must match the literal retired prefix only");
  assert.equal(query(`SELECT row_to_json(t)::text FROM nnmbot.wf_community_riven_alias t ORDER BY id`),
    communityBefore, "Community rows must remain unchanged");
  query("DROP TABLE nnmbot.wf_communityx_cleanup_guard");
  console.log("PASS legacy cleanup: atomic rollback, explicit scope, idempotence, community rows preserved");

  // 在本脚本自己的临时数据库验证手工补充入口，不接触应用配置或开发者数据库。
  const htmlBefore = query(`SELECT md5(string_agg(id::text || ':' || html_content, ',' ORDER BY id))
    FROM nnmbot.bot_command_template`);
  query(`DROP TABLE nnmbot.bot_notification_job;
    ALTER TABLE nnmbot.bot_command_template DROP COLUMN markdown_content;
    ALTER TABLE nnmbot.bot_command_template DROP COLUMN plain_text_content;
    ALTER TABLE nnmbot.bot_command_group_member DROP COLUMN parameter_bindings;`);
  apply("nnm-app/src/test/resources/db/notification-and-presentation.sql");
  assert.equal(query(`SELECT md5(string_agg(id::text || ':' || html_content, ',' ORDER BY id))
    FROM nnmbot.bot_command_template`), htmlBefore, "Manual schema supplement must preserve HTML templates");
  assert.equal(query("SELECT count(*) FROM nnmbot.bot_notification_job"), "0");
  console.log("PASS manual notification/presentation supplement, existing HTML preserved");

  if (hibernate) {
    execFileSync("sh", ["mvnw", "-pl", "nnm-app", "-am", "test",
      "-Dtest=PostgresSchemaValidationTest,OfficialRivenWeaponTreeTest", "-Dsurefire.failIfNoSpecifiedTests=false",
      `-Dnnm.test.postgres-url=${jdbcUrl}`], {
      cwd: root, stdio: "inherit", timeout: 300000
    });
  }
}, { jdbc: hibernate }).catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
