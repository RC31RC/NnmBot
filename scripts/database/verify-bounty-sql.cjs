// Uses a private disposable PostgreSQL cluster, never the configured application database.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { execFileSync } = require('node:child_process');

const root = path.resolve(__dirname, '../..');
const directory = fs.mkdtempSync('/tmp/nnmbot-bounty-sql-');
const data = path.join(directory, 'data');
const binary = name => process.env.PG_BIN ? path.join(process.env.PG_BIN, name) : name;
const env = Object.fromEntries(Object.entries(process.env).filter(([key]) => !key.startsWith('PG')));
const connection = ['-X', '-h', directory, '-p', '55439', '-U', 'sql_test', '-d', 'postgres',
  '-v', 'ON_ERROR_STOP=1'];
let started = false;
function run(name, args) {
  try {
    return execFileSync(binary(name), args, {
      env, encoding: 'utf8', timeout: 120000, maxBuffer: 8 * 1024 * 1024,
      stdio: ['ignore', 'pipe', 'pipe']
    });
  } catch (error) {
    if (error.stderr) console.error(error.stderr);
    throw error;
  }
}
function query(sql) {
  return run('psql', [...connection, '-At', '-c', sql]).trim();
}
const translation = 'nnmbot.wf_community_dataset_state_translation';
const pool = 'nnmbot.wf_community_dataset_reward_pool';
try {
  run('initdb', ['-D', data, '-U', 'sql_test', '-A', 'trust', '--no-locale', '-E', 'UTF8']);
  run('pg_ctl', ['-D', data, '-l', path.join(directory, 'postgres.log'),
    '-o', `-k ${directory} -p 55439 -c listen_addresses='' -c shared_buffers=32MB -c max_connections=5`,
    '-w', 'start']);
  started = true;
  run('psql', [...connection, '-q', '-f', path.join(root, 'docs/sql/init.sql')]);
  const counts = () => query(`SELECT (SELECT count(*) FROM ${translation}), (SELECT count(*) FROM ${pool})`);
  assert.equal(counts(), '0|0');
  assert.equal(query(`SELECT count(*) FROM ${translation}
    WHERE display_name IS NULL OR current_json::jsonb->>'name' IS NULL`), '0');
  assert.equal(query(`SELECT count(*) FROM ${pool}
    WHERE reward_count != jsonb_array_length(rewards_json::jsonb)
      OR source_code IS NULL`), '0');
  assert.equal(query(`SELECT count(*) FROM pg_indexes
    WHERE schemaname='nnmbot'
      AND tablename='wf_community_dataset_state_translation'
      AND indexdef LIKE '%(source_code, record_key)%'`), '1');
  assert.notEqual(query(`SELECT obj_description('${translation}'::regclass)`), '');
  assert.notEqual(query(`SELECT col_description('${translation}'::regclass,
    (SELECT attnum FROM pg_attribute WHERE attrelid='${translation}'::regclass
      AND attname='display_name'))`), '');
  const definition = 'nnmbot.wf_community_bounty_definition';
  const stage = 'nnmbot.wf_community_bounty_stage';
  assert.equal(query(`SELECT (SELECT count(*) FROM ${definition}),
    (SELECT count(*) FROM ${stage})`), '0|0');
  for (const table of [definition, stage]) {
    assert.notEqual(query(`SELECT obj_description('${table}'::regclass)`), '');
    assert.equal(query(`SELECT count(*) FROM pg_attribute
      WHERE attrelid='${table}'::regclass AND attnum > 0 AND NOT attisdropped
        AND col_description(attrelid, attnum) IS NULL`), '0');
  }
  // The same job from two sources remains distinct, with stages owned by a definition.
  query(`INSERT INTO ${definition} (
    id, record_key, record_source, source_code, current_json, upstream_json,
    original_json, upstream_hash, source_hash, name_key, description_key, icon,
    created_at, updated_at
  ) VALUES
    (9001, '/Job', 'COMMUNITY', 'verify:one', '{}', '{}', '{}', 'a', 'a', '/Name', '/Desc', '', now(), now()),
    (9002, '/Job', 'COMMUNITY', 'verify:two', '{}', '{}', '{}', 'b', 'b', '/Name', '/Desc', '', now(), now());
  INSERT INTO ${stage} (definition_id, entry_order, stage_number, option_number, job_reference)
    VALUES (9001, 0, 1, 1, '/Encounter/A'), (9002, 0, 1, 1, '/Encounter/B')`);
  assert.equal(query(`SELECT count(*) FROM ${definition} WHERE record_key='/Job'`), '2');
  query(`BEGIN; DELETE FROM ${definition} WHERE id=9001; ROLLBACK`);
  assert.equal(query(`SELECT count(*) FROM ${stage}`), '2');
  query(`DELETE FROM ${definition} WHERE id=9001`);
  assert.equal(query(`SELECT job_reference FROM ${stage}`), '/Encounter/B');
  console.log('PASS empty remote seeds, bounty definition/stage ownership, source isolation, cascade deletion, rollback and comments');
} finally {
  if (started) run('pg_ctl', ['-D', data, '-m', 'fast', '-w', 'stop']);
  fs.rmSync(directory, { recursive: true, force: true });
}
