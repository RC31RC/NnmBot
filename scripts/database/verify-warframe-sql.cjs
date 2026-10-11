// Run only against a disposable PostgreSQL cluster, never an existing database.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const { execFileSync } = require("node:child_process");

const root = path.resolve(__dirname, "../..");
const fixtureRoot = path.join(root, "nnm-warframe/src/test/resources/sql");
const alignment = path.join(root, "docs/sql/01_warframe_community_dataset_alignment.sql");
const communityImport = process.env.COMMUNITY_IMPORT_SQL
  ? path.resolve(process.env.COMMUNITY_IMPORT_SQL) : null;
const directory = fs.mkdtempSync("/tmp/nnmbot-sql-test-");
const data = path.join(directory, "data");
const binary = name => process.env.PG_BIN ? path.join(process.env.PG_BIN, name) : name;
const env = Object.fromEntries(Object.entries(process.env).filter(([key]) => !key.startsWith("PG")));
const connection = ["-X", "-h", directory, "-p", "55439", "-U", "sql_test", "-d", "postgres",
  "-v", "ON_ERROR_STOP=1"];
let started = false;

function run(name, args, reportError = true) {
  try {
    return execFileSync(binary(name), args, {
      env, encoding: "utf8", stdio: ["ignore", "pipe", "pipe"],
      timeout: 120000, maxBuffer: 32 * 1024 * 1024
    });
  } catch (error) {
    if (reportError && error.stderr) console.error(error.stderr);
    throw error;
  }
}

function query(sql) {
  return run("psql", [...connection, "-At", "-c", sql]).trim();
}

function execute(sql) {
  return run("psql", [...connection, "-At", "-c", sql]).trim();
}

function apply(file) {
  run("psql", [...connection, "-q", "-f", path.join(root, "docs/sql", file)]);
  console.log(`PASS ${file}`);
}

function literal(value) {
  return value == null ? "NULL" : `'${value.replaceAll("'", "''")}'`;
}

function verifyRollback() {
  assert.equal(query("SELECT to_regclass('nnmbot.wf_community_command_alias') IS NOT NULL"), "t");
  assert.equal(query(`SELECT count(*) FROM information_schema.columns
    WHERE table_schema='nnmbot' AND
      ((table_name='wf_community_dataset_reward_pool' AND column_name IN ('pool_key','reward_items_json'))
       OR (table_name='wf_community_dataset_translation' AND column_name='display_name'))`), "0");
  assert.equal(query(`SELECT count(*) FROM pg_indexes WHERE schemaname='nnmbot'
    AND indexname='uq_wf_community_dataset_translation_src_record_4de03b93'`), "1");
}

function expectAlignmentFailure(label) {
  assert.throws(() => run("psql", [...connection, "-q", "-f", alignment], false),
    error => error.status === 3, `${label}: migration must fail`);
  verifyRollback();
}

function verifyAlignment() {
  const commandAliases = query(`SELECT jsonb_agg(to_jsonb(a) ORDER BY id) FROM nnmbot.bot_command_alias a`);
  const invalidRewards = JSON.parse(fs.readFileSync(
    path.join(fixtureRoot, "community-alignment-invalid-rewards.json"), "utf8"));
  for (const fixture of invalidRewards) {
    execute(`INSERT INTO nnmbot.wf_community_dataset_reward_pool (
      record_key, source_code, current_json, rewards_json, upstream_json, original_json,
      upstream_hash, source_hash, created_at, updated_at
    ) VALUES ('verify:invalid', 'verify:rewards', ${literal(JSON.stringify(fixture.payload))},
      ${literal(fixture.legacyRewards ? JSON.stringify(fixture.legacyRewards) : null)},
      '{}', '{}', repeat('0',64), repeat('0',64), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)`);
    expectAlignmentFailure(fixture.label);
    assert.equal(query(`SELECT current_json FROM nnmbot.wf_community_dataset_reward_pool
      WHERE record_key='verify:invalid'`), JSON.stringify(fixture.payload));
    execute("DELETE FROM nnmbot.wf_community_dataset_reward_pool WHERE record_key='verify:invalid'");
  }
  execute(`INSERT INTO nnmbot.wf_community_dataset_translation (
    record_key, source_code, record_source, locale, current_json, upstream_json, original_json,
    upstream_hash, source_hash, created_at, updated_at
  ) VALUES ('verify:invalid', 'legacy:self', 'SELF_MAINTAINED', 'zh', '{}', '{}', '{}',
    repeat('0',64), repeat('0',64), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)`);
  expectAlignmentFailure("missing self-maintained display name");
  execute(`UPDATE nnmbot.wf_community_dataset_translation SET chinese_name='Verify'
    WHERE record_key='verify:invalid';
    INSERT INTO nnmbot.wf_community_dataset_translation (
      record_key, source_code, record_source, locale, chinese_name,
      current_json, upstream_json, original_json, upstream_hash, source_hash, created_at, updated_at
    ) VALUES ('verify:invalid', 'other:self', 'SELF_MAINTAINED', 'zh', 'Verify duplicate',
      '{}', '{}', '{}', repeat('0',64), repeat('0',64), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)`);
  expectAlignmentFailure("colliding self-maintained identities");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_translation
    WHERE record_key='verify:invalid' AND source_code IN ('legacy:self','other:self')`), "2");
  execute("DELETE FROM nnmbot.wf_community_dataset_translation WHERE record_key='verify:invalid'");
  console.log(`PASS migration rollback; invalid rewards=${invalidRewards.length}, invalid translations=2`);

  run("psql", [...connection, "-q", "-f", path.join(fixtureRoot, "community-alignment-legacy.sql")]);
  const tables = ["wf_community_dataset_reward_pool", "wf_community_dataset_translation",
    "wf_community_relic", "wf_community_relic_reward", "wf_community_relic_drop"];
  const audit = table => JSON.parse(query(`SELECT jsonb_agg(
    to_jsonb(r) - ARRAY['current_json','source_code','locale','display_name','enabled',
      'pool_key','reward_items_json'] ORDER BY id)
    FROM nnmbot.${table} r WHERE original_json::jsonb ? 'verifyAlignment'`));
  const snapshots = Object.fromEntries(tables.map(table => [table, audit(table)]));
  apply("01_warframe_community_dataset_alignment.sql");
  for (const table of tables) {
    const ids = new Set(snapshots[table].map(row => row.id));
    assert.deepEqual(audit(table).filter(row => ids.has(row.id)), snapshots[table],
      `${table}: audit JSON and metadata must remain unchanged`);
  }
  assert.equal(query(`SELECT jsonb_agg(to_jsonb(a) ORDER BY id) FROM nnmbot.bot_command_alias a`),
    commandAliases, "backend command aliases must remain unchanged");
  assert.equal(query("SELECT to_regclass('nnmbot.wf_community_command_alias') IS NULL"), "t");
  assert.equal(query("SELECT to_regclass('nnmbot.wf_community_command_alias_id_seq') IS NULL"), "t");
  assert.equal(query(`SELECT count(*) FROM pg_attribute
    WHERE attrelid IN ('nnmbot.wf_community_dataset_reward_pool'::regclass,
      'nnmbot.wf_community_dataset_translation'::regclass)
      AND attname IN ('pool_key','reward_items_json','display_name')
      AND col_description(attrelid, attnum) IS NOT NULL`), "3");

  const rewards = JSON.parse(query(`SELECT reward_items_json FROM nnmbot.wf_community_dataset_reward_pool
    WHERE source_code='warframe-public-export-plus:ExportRewards.json'
      AND record_key='/Lotus/Rewards/VerifyTable'`));
  assert.deepEqual(rewards.map(({ stage, itemKey, displayName, count, probability, rarity }) =>
    ({ stage, itemKey, displayName, count, probability, rarity })), [
    { stage: 1, itemKey: "/Lotus/StoreItems/A/Shared", displayName: "/Lotus/StoreItems/A/Shared",
      count: 2, probability: 0.2, rarity: null },
    { stage: 3, itemKey: "/Lotus/StoreItems/B/Shared", displayName: "/Lotus/StoreItems/B/Shared",
      count: 0, probability: null, rarity: "RARE" }
  ]);
  assert.equal(rewards[0].custom, "raw");
  assert.equal(rewards[1].chance, 50, "legacy chance is audit-only, not a probability");
  assert.equal(query(`SELECT current_json::jsonb->>'custom' FROM nnmbot.wf_community_dataset_reward_pool
    WHERE source_code='warframe-public-export-plus:ExportRewards.json'
      AND record_key='/Lotus/Rewards/VerifyTable'`), "pool");
  assert.equal(query(`SELECT jsonb_array_length(current_json::jsonb->'stages')
    FROM nnmbot.wf_community_dataset_reward_pool WHERE source_code='warframe-public-export-plus:ExportRewards.json'
      AND record_key='/Lotus/Rewards/VerifyTable'`), "3");
  assert.equal(query(`SELECT reward_items_json FROM nnmbot.wf_community_dataset_reward_pool
    WHERE record_key='/Lotus/Rewards/VerifyEmpty'`), "[]");
  assert.equal(query(`SELECT source_code FROM nnmbot.wf_community_dataset_reward_pool
    WHERE record_source='SELF_MAINTAINED'`), "nnmbot:reward_pool");
  assert.equal(query(`SELECT reward_items_json::jsonb->0->>'probability'
    FROM nnmbot.wf_community_dataset_reward_pool WHERE record_key='/Lotus/Rewards/VerifyLegacy'`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_reward_pool
    WHERE pool_key IS DISTINCT FROM record_key OR reward_items_json::jsonb
      IS DISTINCT FROM current_json::jsonb->'rewards'`), "0");

  const translations = JSON.parse(query(`SELECT jsonb_agg(jsonb_build_object(
    'source',source_code,'locale',locale,'name',display_name,'enabled',enabled) ORDER BY source_code,locale)
    FROM nnmbot.wf_community_dataset_translation WHERE original_json::jsonb ? 'verifyAlignment'`));
  assert.deepEqual(translations, [
    { source: "kingprimes:warframe/translation.json", locale: "en", name: "Legacy English", enabled: false },
    { source: "kingprimes:warframe/translation.json", locale: "zh", name: "旧中文", enabled: false },
    { source: "nnmbot:translation", locale: "en", name: "Self English", enabled: true },
    { source: "nnmbot:translation", locale: "zh", name: "自定义中文", enabled: true },
    { source: "warframe-public-export-plus:dict.en.json", locale: "en", name: "English entry", enabled: true },
    { source: "warframe-public-export-plus:dict.zh.json", locale: "zh", name: "中文词条", enabled: true }
  ]);
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_translation
    WHERE record_key IS DISTINCT FROM current_json::jsonb->>'recordKey'
      OR locale IS DISTINCT FROM current_json::jsonb->>'locale'
      OR display_name IS DISTINCT FROM current_json::jsonb->>'displayName'`), "0");
  const splitRows = audit("wf_community_dataset_translation")
    .filter(row => !snapshots.wf_community_dataset_translation.some(old => old.id === row.id));
  for (const row of splitRows) {
    const original = snapshots.wf_community_dataset_translation
      .find(old => old.record_key === row.record_key
        && old.record_source === row.record_source && old.chinese_name === row.chinese_name);
    assert(original);
    assert.deepEqual({ ...row, id: original.id }, original, "split languages must retain all audit fields");
  }
  assert.throws(() => run("psql", [...connection, "-c", `INSERT INTO nnmbot.wf_community_dataset_translation (
    record_key, source_code, record_source, locale, display_name, current_json, upstream_json, original_json,
    upstream_hash, source_hash, created_at, updated_at)
    SELECT record_key, source_code, record_source, locale, display_name, current_json, upstream_json,
      original_json, upstream_hash, source_hash, created_at, updated_at
    FROM nnmbot.wf_community_dataset_translation WHERE source_code='nnmbot:translation' AND locale='zh'`], false),
  error => error.status === 1 && error.stderr.includes("uq_wf_community_dataset_translation_source_record_locale"));
  for (const table of tables.slice(2)) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table}
      WHERE enabled AND original_json::jsonb ? 'verifyAlignment'`), "2");
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table}
      WHERE original_json::jsonb ? 'verifyAlignment'
        AND enabled IS DISTINCT FROM (record_source='SELF_MAINTAINED'
          OR source_code='warframe-relic-data:data/Relics.json')`), "0");
  }
  for (const table of tables.toReversed()) {
    execute(`DELETE FROM nnmbot.${table} WHERE original_json::jsonb ? 'verifyAlignment'`);
  }
  console.log("PASS full reward keys, bilingual identities, audit preservation, self-maintained and retired sources");
}

function counts() {
  return query(`SELECT
    (SELECT count(*) FROM nnmbot.wf_community_relic),
    (SELECT count(*) FROM nnmbot.wf_community_relic_reward),
    (SELECT count(*) FROM nnmbot.wf_community_worldstate_faction),
    (SELECT count(*) FROM nnmbot.wf_community_worldstate_syndicate),
    (SELECT count(*) FROM nnmbot.wf_community_worldstate_operation_type),
    (SELECT count(*) FROM nnmbot.wf_community_worldstate_upgrade_type)`);
}

function verifySeedSources() {
  const sources = {
    wf_community_term: ["term", "kingprimes:warframe/term.json"],
    wf_community_arbitration_essence: ["arbitration_essence", "warframe-info-api:routes/arbitrationEssence.js"],
    wf_community_dataset_alias: ["alias", "kingprimes:warframe/alias.json"],
    wf_community_dataset_market_component: ["market_component", "nnmbot:market_component"],
    wf_community_dataset_market_riven_tion: ["market_riven_tion", "kingprimes:warframe/market_riven_tion.json"],
    wf_community_dataset_market_riven_tion_alias: ["market_riven_tion_alias", "kingprimes:warframe/market_riven_tion_alias.json"],
    wf_community_dataset_reward_pool: ["reward_pool", "kingprimes:warframe/reward_pool.json"],
    wf_community_dataset_state_translation: ["state_translation", "kingprimes:warframe/state_translation.json"],
    wf_community_worldstate_faction: ["worldstate_faction", "warframe-worldstate-data:data/zh/factionsData.json"],
    wf_community_worldstate_fissure_modifier: ["worldstate_fissure_modifier", "warframe-worldstate-data:data/zh/fissureModifiers.json"],
    wf_community_worldstate_mission_type: ["worldstate_mission_type", "warframe-worldstate-data:data/zh/missionTypes.json"],
    wf_community_worldstate_node: ["worldstate_node", "warframe-worldstate-data:data/zh/solNodes.json"],
    wf_community_worldstate_operation_type: ["worldstate_operation_type", "warframe-worldstate-data:data/zh/operationTypes.json"],
    wf_community_worldstate_sortie: ["worldstate_sortie", "warframe-worldstate-data:data/zh/sortieData.json"],
    wf_community_worldstate_syndicate: ["worldstate_syndicate", "warframe-worldstate-data:data/zh/syndicatesData.json"],
    wf_community_worldstate_upgrade_type: ["worldstate_upgrade_type", "warframe-worldstate-data:data/zh/upgradeTypes.json"]
  };
  for (const [table, [dataset, source]] of Object.entries(sources)) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table}
      WHERE source_code IS DISTINCT FROM CASE record_source
        WHEN 'SELF_MAINTAINED' THEN 'nnmbot:${dataset}'
        ELSE '${source}' END`), "0", `${table}: seed source differs from runtime identity`);
  }
  // Mixed upstream/local aliases must retain separate identities and ownership.
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_market_riven_tion_alias
    WHERE record_source='SELF_MAINTAINED'
      AND source_code='nnmbot:market_riven_tion_alias'`), "6");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_market_riven_tion_alias
    WHERE record_source='COMMUNITY'
      AND source_code='kingprimes:warframe/market_riven_tion_alias.json'`), "0");
  console.log(`PASS canonical seed sources; tables=${Object.keys(sources).length}`);
}

try {
  run("initdb", ["-D", data, "-U", "sql_test", "-A", "trust", "--no-locale", "-E", "UTF8"]);
  // A private Unix socket and disabled TCP prevent access to the user's server.
  run("pg_ctl", ["-D", data, "-l", path.join(directory, "postgres.log"),
    "-o", `-k ${directory} -p 55439 -c listen_addresses='' -c shared_buffers=32MB -c max_connections=5`,
    "-w", "start"]);
  started = true;
  apply("init.sql");
  verifyAlignment();
  verifySeedSources();

  const first = counts();
  assert.equal(first, "0|0|0|0|0|0", "remote community seeds must remain empty");
  assert.equal(query(`SELECT count(*) FROM information_schema.tables
    WHERE table_schema='nnmbot'
      AND table_name='wf_community_worldstate_sortie'`), "1");
  assert.equal(query(`SELECT count(*) FROM information_schema.columns
    WHERE table_schema='nnmbot'
      AND table_name='wf_community_worldstate_sortie'
      AND column_name='record_type'`), "1");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_bounty_definition`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_bounty_stage`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_merge_receipt`), "0");
  const communityTables = query(`SELECT table_name FROM information_schema.columns
    WHERE table_schema='nnmbot' AND table_name LIKE 'wf_community_%'
      AND column_name='record_source'`).split(/\r?\n/);
  let localSeedCount = 0;
  for (const table of communityTables) {
    assert.equal(query(`SELECT count(*) FROM nnmbot.${table}
      WHERE record_source <> 'SELF_MAINTAINED'
        OR source_code IS NULL OR source_code NOT LIKE 'nnmbot:%'`), "0",
      `${table}: remote or incorrectly attributed seed`);
    localSeedCount += Number(query(`SELECT count(*) FROM nnmbot.${table}`));
  }
  assert.equal(localSeedCount, 19);
  assert.equal(query(`SELECT count(*) FROM pg_attribute
    WHERE attrelid='nnmbot.wf_community_worldstate_sortie'::regclass
      AND attnum > 0 AND NOT attisdropped
      AND col_description(attrelid, attnum) IS NULL`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_relic
    WHERE relic_name IS DISTINCT FROM COALESCE(
      current_json::jsonb->>'name',
      current_json::jsonb->>'uniqueName',
      current_json::jsonb->>'recordKey',
      record_key
    )`), "0");
  assert.equal(query(`SELECT count(*)
    FROM information_schema.columns
    WHERE table_schema='nnmbot'
      AND table_name='wf_community_relic'
      AND column_name IN (
        'category', 'era', 'icon', 'codex_secret', 'description_key',
        'quality', 'reward_manifest', 'introduced_at'
      )`), "8");
  assert.equal(query(`SELECT count(*)
    FROM pg_attribute
    WHERE attrelid='nnmbot.wf_community_relic'::regclass
      AND attnum > 0 AND NOT attisdropped
      AND attname IN (
        'category', 'era', 'icon', 'codex_secret', 'description_key',
        'quality', 'reward_manifest', 'introduced_at'
      )
      AND col_description(attrelid, attnum) IS NOT NULL`), "8");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_relic_reward r
    LEFT JOIN nnmbot.wf_community_relic p
      ON p.record_key = r.relic_record_key
      AND p.source_code = r.source_code
    WHERE p.id IS NULL`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_relic
    WHERE source_code IS NULL OR last_imported_at IS NOT NULL`), "0");
  assert.equal(query(`SELECT count(*)
    FROM information_schema.columns
    WHERE table_schema = 'nnmbot'
      AND table_name IN (
        'wf_community_fish_guide', 'wf_community_map_feature',
        'wf_community_relic', 'wf_community_relic_reward',
        'wf_community_relic_drop', 'wf_community_riven_unrollable',
        'wf_community_worldstate_rule'
      )
      AND column_name IN ('payload', 'source_project', 'source_path')`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_map_feature
    WHERE source_code='warframe-hub:data/json/geo/plains/fishing.json'
      AND record_key='plains:fishing.json:34b058e0cacd47e4de1648a2e43adbd760859fd1b770d6bf702d1e08b3349b0e'`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_relic
    WHERE source_code IS NULL OR relic_name IS DISTINCT FROM COALESCE(
      current_json::jsonb->>'name',
      current_json::jsonb->>'uniqueName',
      current_json::jsonb->>'recordKey',
      record_key
    )`), "0");
  assert.equal(query(`SELECT count(*)
    FROM nnmbot.wf_community_relic_drop
    WHERE relic_record_key IS DISTINCT FROM current_json::jsonb->>'relicRecordKey'
      OR location_text IS DISTINCT FROM COALESCE(
        current_json::jsonb #>> '{data,location}',
        current_json::jsonb #>> '{data,node}'
      )
      OR rarity IS DISTINCT FROM current_json::jsonb #>> '{data,rarity}'
      OR chance IS DISTINCT FROM NULLIF(
        current_json::jsonb #>> '{data,chance}', ''
      )::numeric(12,4)
      OR drop_order IS DISTINCT FROM NULLIF(
        current_json::jsonb ->> 'rewardOrder', ''
      )::integer`), "0");
  assert.equal(
    query(`SELECT count(*) FROM nnmbot.wf_community_relic_drop
      WHERE source_code='warframe-relic-data:data/Relics.json'
        AND relic_record_key IS NOT NULL
        AND location_text IS NOT NULL
        AND rarity IS NOT NULL
        AND chance IS NOT NULL
        AND drop_order IS NOT NULL`),
    "0"
  );

  const identityCheck = execute(`BEGIN;
    INSERT INTO nnmbot.wf_community_relic (
      record_key, relic_name, source_code, current_json, upstream_json,
      original_json, manual_fields_json, upstream_hash, source_hash, record_source
    ) VALUES
      ('verify:shared-key', 'Verify A', 'verify-a:data.json',
       '{"name":"Verify A"}', '{"name":"Verify A"}',
       '{"name":"Verify A"}', '{}', repeat('a',64), repeat('a',64), 'COMMUNITY'),
      ('verify:shared-key', 'Verify B', 'verify-b:data.json',
       '{"name":"Verify B"}', '{"name":"Verify B"}',
       '{"name":"Verify B"}', '{}', repeat('b',64), repeat('b',64), 'COMMUNITY');
    SELECT count(*) FROM nnmbot.wf_community_relic
      WHERE record_key='verify:shared-key';
    INSERT INTO nnmbot.wf_community_relic (
      record_key, relic_name, source_code, current_json, upstream_json,
      original_json, manual_fields_json, upstream_hash, source_hash, record_source
    ) VALUES
      ('verify:shared-key', 'Verify A duplicate', 'verify-a:data.json',
       '{"name":"Verify A duplicate"}', '{"name":"Verify A duplicate"}',
       '{"name":"Verify A duplicate"}', '{}', repeat('c',64), repeat('c',64), 'COMMUNITY')
    ON CONFLICT (source_code, record_key) DO NOTHING;
    SELECT count(*) FROM nnmbot.wf_community_relic
      WHERE record_key='verify:shared-key';
    ROLLBACK;`);
  assert.deepEqual(identityCheck.split(/\r?\n/).filter(line => line === "2"), ["2", "2"]);
  assert.notEqual(
    query(`SELECT count(*) FROM pg_indexes
      WHERE schemaname='nnmbot'
        AND tablename='wf_community_relic'
        AND indexdef LIKE '%(source_code, record_key)%'`),
    "0"
  );

  // 这些数据已经合并到 init.sql，验证的是空库基线本身，而不是重放历史增量。
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_market_riven_tion`), "0");
  assert.equal(query(`SELECT attribute_code FROM nnmbot.wf_community_dataset_market_riven_tion_alias
    WHERE alias_name='基双爆'`), "base_damage_/_melee_damage,critical_chance,critical_damage");
  assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command_alias a
    JOIN nnmbot.bot_command c ON c.id=a.command_id
    WHERE c.code='market-riven' AND a.alias_name IN ('zk','/zk')`), "2");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_market_component`), "13");
  assert.equal(query(`SELECT count(*) FROM nnmbot.wf_community_dataset_market_riven_tion`), "0");
  assert.equal(query(`SELECT count(*) FROM nnmbot.bot_command
    WHERE output_mode NOT IN ('TEXT','IMAGE','FILE','MARKDOWN')`), "0");
  console.log(`PASS names, reward links, consolidated market data and command modes; counts=${first}`);

  if (communityImport) {
    const sql = fs.readFileSync(communityImport, "utf8");
    const insertTables = [...sql.matchAll(/^INSERT INTO (nnmbot\.[a-z0-9_]+)/gm)]
      .map(match => match[1]);
    const tables = [...new Set(insertTables)];
    assert(insertTables.length > 0, "generated community SQL contains no inserts");
    execute(`TRUNCATE TABLE ${tables.join(", ")} CASCADE`);
    run("psql", [...connection, "-q", "-f", communityImport]);

    const actualRows = Number(query(`SELECT ${tables.map(table =>
      `(SELECT count(*) FROM ${table})`
    ).join(" + ")}`));
    assert.equal(actualRows, insertTables.length);
    console.log(`PASS generated community SQL; tables=${tables.length}, rows=${actualRows}`);
  }
} finally {
  if (started) run("pg_ctl", ["-D", data, "-m", "fast", "-w", "stop"]);
  fs.rmSync(directory, { recursive: true, force: true });
}
