const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');

// 历史实现仅供追溯；当前定义、词典和奖励池必须按来源主动刷新、预览后采纳。
throw new Error('此历史种子生成器已停用，请使用赏金定义和奖励池的管理端刷新流程');

// Offline SQL generator only. The running application reads the database, never this checkout.
const root = process.argv[2];
const output = process.argv[3];
if (!root || !output) {
  throw new Error('Usage: node generate-bounty-supplement.cjs <public-export-plus-dir> <new-sql-file>');
}
if (fs.existsSync(output)) {
  throw new Error('Refusing to overwrite an existing migration');
}
const read = name => JSON.parse(fs.readFileSync(path.join(root, name), 'utf8'));
const hash = value => crypto.createHash('sha256').update(value).digest('hex');
const zh = read('dict.zh.json');
const en = read('dict.en.json');
const bounties = read('ExportBounties.json');
const rewards = read('ExportRewards.json');
const exportsByKey = new Map();
for (const file of fs.readdirSync(root).filter(file => /^Export.*\.json$/.test(file)).sort()) {
  if (file === 'ExportRewards.json') continue;
  for (const [key, value] of Object.entries(read(file))) {
    if (value && typeof value.name === 'string') exportsByKey.set(key, value);
  }
}
const localize = (key, dictionary) => dictionary[key] || null;
const rows = new Map();
const source = file => ({
  project: 'warframe-public-export-plus',
  path: file,
  fileSha256: hash(fs.readFileSync(path.join(root, file))),
  chineseDictionary: 'dict.zh.json',
  chineseDictionarySha256: hash(fs.readFileSync(path.join(root, 'dict.zh.json'))),
  englishDictionary: 'dict.en.json',
  englishDictionarySha256: hash(fs.readFileSync(path.join(root, 'dict.en.json'))),
  importId: '12_bounty_supplement'
});
function translation(key, item, provenance) {
  const nameEn = localize(item.name, en);
  const name = localize(item.name, zh) || nameEn;
  if (!name) return;
  rows.set(key, {
    uniqueName: key, name, nameEn,
    description: localize(item.description, zh) || localize(item.description, en) || '',
    descriptionEn: localize(item.description, en) || '',
    source: provenance
  });
}
for (const [key, bounty] of Object.entries(bounties)) {
  translation(key, bounty, source('ExportBounties.json'));
}
const pools = [];
for (const [key, stages] of Object.entries(rewards)) {
  if (!key.startsWith('/Lotus/') || !/VenusJob|EidolonJob|DeimosMissionRewards/i.test(key)) continue;
  if (!Array.isArray(stages) || stages.some(stage => !Array.isArray(stage))) {
    throw new Error(`Unexpected reward stages: ${key}`);
  }
  const entries = stages.flatMap((stage, index) => stage.map(reward => {
    if (typeof reward.type !== 'string' || !Number.isFinite(reward.probability)) {
      throw new Error(`Invalid reward in ${key}`);
    }
    const exportKey = reward.type.replace('/Lotus/StoreItems/', '/Lotus/');
    const item = exportsByKey.get(reward.type) || exportsByKey.get(exportKey);
    if (item) translation(reward.type, item, source('ExportRewards.json'));
    return {
      item: reward.type, count: reward.itemCount,
      chance: Number((reward.probability * 100).toFixed(6)), stage: index + 1
    };
  }));
  pools.push({ uniqueName: key, rewards: entries, source: source('ExportRewards.json') });
}
const literal = value => "'" + value.replaceAll("'", "''") + "'";
function migration(table, records, fields) {
  const incoming = records.map(record => ({
    key: record.uniqueName, data: record, hash: hash(JSON.stringify(record))
  }));
  const assignments = fields.map(([column, expression]) => `${column} = ${expression}`).join(',\n    ');
  return `
WITH incoming AS (
  SELECT * FROM jsonb_to_recordset(${literal(JSON.stringify(incoming))}::jsonb)
    AS r(key text, data jsonb, hash text)
), updated AS (
  UPDATE nnmbot.${table} t SET
    ${assignments},
    record_source = 'SELF_MAINTAINED',
    current_json = r.data::text, upstream_json = r.data::text,
    source_hash = r.hash, upstream_hash = r.hash, updated_at = CURRENT_TIMESTAMP
  FROM incoming r
  WHERE (t.unique_name = r.key OR t.record_key = r.key)
    AND t.record_source = 'COMMUNITY' AND NOT t.manual_modified
    AND COALESCE(NULLIF(t.manual_fields_json::text, '')::jsonb, '{}'::jsonb) = '{}'::jsonb
  RETURNING t.id
)
INSERT INTO nnmbot.${table} (
  record_key, record_source, ${fields.map(([column]) => column).join(', ')},
  current_json, upstream_json, original_json, manual_fields_json,
  source_hash, upstream_hash, manual_modified, enabled, created_at, updated_at
)
SELECT r.key, 'SELF_MAINTAINED', ${fields.map(([, expression]) => expression).join(', ')},
  r.data::text, r.data::text, r.data::text, '{}',
  r.hash, r.hash, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM incoming r
WHERE NOT EXISTS (
  SELECT 1 FROM nnmbot.${table} t WHERE t.unique_name = r.key OR t.record_key = r.key
)
ON CONFLICT (record_key) DO NOTHING;
`;
}
const sql = `-- 12: 赏金动态参数所需的简体中文名称、说明和分阶段奖励池。
-- 前置：现有 01-11 结构。仅执行本文件，不修改或重新执行历史脚本。
-- 来源：warframe-public-export-plus 的 ExportBounties/ExportRewards + dict.zh/dict.en。
-- 仅使用简体 zh；不使用 tc、不在运行时代码中繁简转换。
-- 使用现有社区表，每个 uniqueName 一条中英合并记录，来源及 SHA-256 保存在 JSON.source。
-- 补充数据按 SELF_MAINTAINED 接管，排除旧 datasource 同步，避免双同步来源。
-- 仅替换未手工修改的 COMMUNITY 行；管理员维护、停用状态和首次导入快照保留。
-- 重复执行不会覆盖 SELF_MAINTAINED；本脚本不接管后续自动刷新。
-- 奖励 stage 是静态奖励池的阶段编号，不是当天随机抽出的任务阶段。
-- 名称/说明/奖励池更新后重启应用，使社区缓存和世界状态投影重新构建。
-- ${Object.keys(bounties).length} bounty definitions; ${rows.size} translations; ${pools.length} pools.
BEGIN;
${migration('wf_community_dataset_state_translation', [...rows.values()], [
  ['unique_name', 'r.key'], ['display_name', "r.data->>'name'"],
  ['description', "r.data->>'description'"]
])}
${migration('wf_community_dataset_reward_pool', pools, [
  ['unique_name', 'r.key'], ['reward_count', "jsonb_array_length(r.data->'rewards')"],
  ['rewards_json', "(r.data->'rewards')::text"]
])}
COMMENT ON TABLE nnmbot.wf_community_dataset_state_translation IS
  '社区世界状态名称及说明；赏金补充来源为 public-export-plus 的 ExportBounties 和简体 dict.zh，英文兜底，JSON.source 记录来源与校验值';
COMMENT ON TABLE nnmbot.wf_community_dataset_reward_pool IS
  '社区奖励池；赏金补充来源为 public-export-plus ExportRewards，rewards 数组保留 stage、item、count、chance（百分数），JSON.source 记录来源';
COMMIT;
`;
fs.writeFileSync(output, sql, { flag: 'wx' });
console.log(`Generated ${output}: ${rows.size} translations, ${pools.length} pools`);
