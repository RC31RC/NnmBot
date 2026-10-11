// Run only against a disposable PostgreSQL cluster, never an existing database.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const { execFileSync } = require("node:child_process");

const root = path.resolve(__dirname, "../..");
const directory = fs.mkdtempSync("/tmp/nnmbot-worldstate-sql-");
const data = path.join(directory, "data");
const binary = name => process.env.PG_BIN ? path.join(process.env.PG_BIN, name) : name;
const env = Object.fromEntries(Object.entries(process.env).filter(([key]) => !key.startsWith("PG")));
const connection = ["-X", "-h", directory, "-p", "55439", "-U", "sql_test", "-d", "postgres",
  "-v", "ON_ERROR_STOP=1"];
let started = false;

function run(name, args) {
  try {
    return execFileSync(binary(name), args, {
      env,
      encoding: "utf8",
      stdio: ["ignore", "pipe", "pipe"],
      timeout: 120000,
      maxBuffer: 16 * 1024 * 1024
    });
  } catch (error) {
    if (error.stderr) console.error(error.stderr);
    throw error;
  }
}

function query(sql) {
  return run("psql", [...connection, "-At", "-c", sql]).trim();
}

function apply(file) {
  run("psql", [...connection, "-q", "-f", path.join(root, "docs/sql", file)]);
  console.log(`PASS ${file}`);
}

function command(code) {
  return query(`SELECT name || '|' || output_mode || '|' || enabled
    FROM nnmbot.bot_command WHERE code=${quote(code)}`);
}

function quote(value) {
  return `'${String(value).replaceAll("'", "''")}'`;
}

try {
  run("initdb", ["-D", data, "-U", "sql_test", "-A", "trust", "--no-locale", "-E", "UTF8"]);
  // Private socket and disabled TCP keep this verification isolated from configured databases.
  run("pg_ctl", ["-D", data, "-l", path.join(directory, "postgres.log"),
    "-o", `-k ${directory} -p 55439 -c listen_addresses='' -c shared_buffers=32MB -c max_connections=5`,
    "-w", "start"]);
  started = true;

  apply("init.sql");
  const templateContent = () => query(`SELECT html_content FROM nnmbot.bot_command_template
    WHERE template_code='arbitrations'`);
  const arbitrationTemplate = templateContent();
  assert(arbitrationTemplate.includes("仲裁排期"));
  assert(arbitrationTemplate.includes("当前范围暂无已存档任务"));
  const expectedCodes = [
    "conclave", "news", "flash-sales", "persistent-enemies", "syndicate-missions",
    "construction-progress", "simaris", "sentient-outposts", "clan-initiative",
    "circuit-normal", "circuit-hard", "calendar", "vault-trader", "timestamp",
    "archimedeas", "deep-archimedeas", "hex-archimedeas", "descents", "cavia",
    "holdfasts", "hex-bounties", "events", "global-upgrades"
  ];
  const missing = expectedCodes.filter(code => query(
    `SELECT count(*) FROM nnmbot.bot_command WHERE code=${quote(code)}`
  ) !== "1");
  assert.deepEqual(missing, []);

  const aliasesBefore = query(`SELECT count(*) FROM nnmbot.bot_command_alias
    WHERE lower(btrim(alias_name)) IN ('竞技场','conclave','pvp','深层下降','descent','descendia')`);
  assert.equal(aliasesBefore, "6");
  assert.equal(query(`SELECT count(*) FROM (
    SELECT lower(btrim(alias_name)) FROM nnmbot.bot_command_alias
    WHERE lower(btrim(alias_name)) IN ('竞技场','conclave','pvp','深层下降','descent','descendia')
    GROUP BY lower(btrim(alias_name)) HAVING count(*) > 1
  ) duplicates`), "0");

  assert.equal(query(`SELECT count(*) FROM (
    SELECT lower(btrim(alias_name)) FROM nnmbot.bot_command_alias
    WHERE lower(btrim(alias_name)) IN ('竞技场','conclave','pvp','深层下降','descent','descendia')
    GROUP BY lower(btrim(alias_name)) HAVING count(*) > 1
  ) duplicates`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command_alias a
    JOIN nnmbot.bot_command c ON c.id=a.command_id
    WHERE c.code='descents'
      AND lower(btrim(a.alias_name)) IN ('深层下降','descent','descendia')`), "3");
  console.log("PASS world-state commands, arbitration template and alias uniqueness");

  const schedule = "nnmbot.wf_arbitration_schedule";
  assert.equal(query(`SELECT count(*) FROM information_schema.columns
    WHERE table_schema='nnmbot' AND table_name='wf_arbitration_schedule'`), "10");
  assert.equal(command("today-arbitration"), "今日仲裁|TEXT|true");
  assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command_alias
    WHERE lower(btrim(alias_name)) IN
      ('今日仲裁','today-arbitration','高效仲裁','efficient-arbitration','地球','地球状态','平原状态')`), "7");
  assert.equal(query(`SELECT count(*) FROM pg_attribute
    WHERE attrelid='${schedule}'::regclass AND attnum > 0 AND NOT attisdropped
      AND col_description(attrelid,attnum) IS NULL`), "0");
  const row = (start, node) => `('${start}'::timestamptz,
    '${start}'::timestamptz + interval '1 hour','upstream','${node}','Defense',
    'Infestation','Ceres','{}','https://example.test/arbys','2026-09-12T04:00:00Z')`;
  query(`INSERT INTO ${schedule} VALUES
    ${row("2026-09-12T03:00:00Z", "past")},
    ${row("2026-09-12T04:00:00Z", "current")},
    ${row("2026-09-12T05:00:00Z", "future")}`);
  assert.equal(query(`SELECT node FROM ${schedule}
    WHERE activation <= '2026-09-12T04:00:00Z' AND expiry > '2026-09-12T04:00:00Z'`), "current");
  query(`INSERT INTO ${schedule} VALUES ${row("2026-09-12T04:00:00Z", "corrected")}
    ON CONFLICT(activation) DO UPDATE SET node=excluded.node`);
  assert.equal(query(`SELECT count(*) FROM ${schedule}`), "3");
  assert.equal(query(`SELECT node FROM ${schedule} WHERE activation='2026-09-12T04:00:00Z'`), "corrected");
  // A constraint failure must roll back the entire refresh transaction.
  let rejected = false;
  try {
    run("psql", [...connection, "-q", "-c", `BEGIN;
      UPDATE ${schedule} SET node='should-rollback';
      UPDATE ${schedule} SET expiry=activation; COMMIT;`]);
  } catch {
    rejected = true;
  }
  assert(rejected, "Invalid intervals must be rejected");
  assert.equal(query(`SELECT count(*) FROM ${schedule} WHERE node='should-rollback'`), "0");
  console.log("PASS 06: baseline, existing-db upgrade, template migration/customization protection, repeat execution, comments, hour boundaries and atomic rollback");
} finally {
  if (started) run("pg_ctl", ["-D", data, "-m", "fast", "-w", "stop"]);
  fs.rmSync(directory, { recursive: true, force: true });
}
