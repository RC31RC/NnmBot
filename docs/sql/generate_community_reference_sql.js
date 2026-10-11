/* Generate the hand-executed community reference import script from the ten
 * checked-out reference projects. This is intentionally offline: production
 * never loads these files from the classpath. */
const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

const root = "/Users/ren/RcGit/waframe机器人参考";
const out = process.argv[2];
if (!out) {
  throw new Error("Specify an output path for review; never overwrite init.sql automatically");
}

const quote = value => "'" + String(value).replace(/'/g, "''") + "'";
const sql = value => {
  if (value === null || value === undefined) return "NULL";
  if (typeof value === "boolean") return value ? "TRUE" : "FALSE";
  if (typeof value === "number") return String(value);
  return quote(value);
};
const hash = value => crypto.createHash("sha256")
  .update(JSON.stringify(value))
  .digest("hex");
const read = file => JSON.parse(fs.readFileSync(path.join(root, file), "utf8"));
const rows = [];
const importedAt = new Date().toISOString();

function add(table, key, payload, project, sourcePath, columns = {}) {
  rows.push({
    table,
    key,
    payload,
    sourceCode: `${project}:${sourcePath}`,
    columns,
    hash: hash(payload)
  });
}

function addTerm(group, key, english, chinese, payload, sourcePath) {
  add("wf_community_term", key, payload, "warframe-info-api", sourcePath, {
    termGroup: group,
    englishText: english,
    chineseText: chinese
  });
}

function addAlias(alias, target, sourcePath) {
  add("wf_community_dataset_alias", `${sourcePath}:${alias}:${target}`,
    { alias, target }, "warframe-info-api", sourcePath, {
      aliasName: alias,
      targetName: target
    });
}

function communityInsert(row, columns, values) {
  const encoded = JSON.stringify(row.payload);
  const allColumns = columns.concat([
    "source_code", "last_imported_at", "created_at", "updated_at",
    "current_json", "upstream_json",
    "original_json", "manual_fields_json", "upstream_hash", "source_hash",
    "record_source", "manual_modified", "enabled"
  ]);
  const allValues = values.concat([
    row.sourceCode, importedAt, importedAt, importedAt,
    encoded, encoded, encoded, "{}",
    row.hash, row.hash, "COMMUNITY", false, true
  ]);
  return `INSERT INTO nnmbot.${row.table} (${allColumns.join(",")}) VALUES ` +
    `(${allValues.map(sql).join(",")});`;
}

const infoRoot = "warframe-info-api";
for (const [index, value] of read(`${infoRoot}/utils/dict/wfExtraTerms.json`).entries()) {
  const item = value && typeof value === "object"
    ? value
    : { en: String(value), zh: String(value) };
  const key = item.en || String(index);
  addTerm("EXTRA", key, item.en || key, item.zh || item.en || key, item,
    "utils/dict/wfExtraTerms.json");
}
for (const [key, value] of Object.entries(read(`${infoRoot}/utils/dict/archimedeaTerms.json`))) {
  if (key !== "_comment") {
    addTerm("ARCHIMEDEA", key, key, value, { en: key, zh: value },
      "utils/dict/archimedeaTerms.json");
  }
}
for (const [key, value] of Object.entries(read(`${infoRoot}/utils/dict/RivenStatsName.json`))) {
  addTerm("RIVEN_STAT", key, key, value, { en: key, zh: value },
    "utils/dict/RivenStatsName.json");
}
for (const [key, value] of Object.entries(read(`${infoRoot}/utils/dict/WmRivenAttribute.json`))) {
  addTerm("RIVEN_ATTRIBUTE", key, value, key, { en: value, zh: key },
    "utils/dict/WmRivenAttribute.json");
}
for (const item of read(`${infoRoot}/api/dataSource/alias_local.json`)) {
  // alias_local.zh is the English canonical target in the reference project.
  addAlias(item.key, item.zh, "api/dataSource/alias_local.json");
}
for (const [index, item] of read(`${infoRoot}/utils/dict/custom.json`).entries()) {
  addTerm("CUSTOM_PATTERN", `${item.en || index}:${item.zh || index}`, item.en || String(index),
    item.zh || item.en || String(index), item, "utils/dict/custom.json");
}
for (const item of read(`${infoRoot}/api/dataSource/state_translation_local.json`)) {
  add("wf_community_worldstate_rule",
    `state_translation:zh:${item.uniqueName || item.name}`,
    item, infoRoot, "api/dataSource/state_translation_local.json", {
      datasetCode: "state_translation",
      locale: "zh"
    });
}

// warframe-hub contains curated fishing instructions, not official item data.
for (const file of fs.readdirSync(path.join(root, "warframe-hub/data/json/fish"))
  .filter(name => name.endsWith(".json"))) {
  const data = read(`warframe-hub/data/json/fish/${file}`);
  for (const item of (Array.isArray(data) ? data : [data])) {
    add("wf_community_fish_guide", item.name || file, item, "warframe-hub",
      `data/json/fish/${file}`, { name: item.name });
  }
}

// Map annotations are GeoJSON features with custom in-game map coordinates.
for (const world of ["deimos", "plains", "vallis"]) {
  const dir = path.join(root, `warframe-hub/data/json/geo/${world}`);
  for (const file of fs.readdirSync(dir).filter(name => name.endsWith(".json"))) {
    const data = read(`warframe-hub/data/json/geo/${world}/${file}`);
    const features = data.features || data || [];
    features.forEach((feature, index) => {
      const properties = feature.properties || {};
      const key = `${world}#${properties.name || `feature-${index}`}#${index}`;
      const normalized = { ...feature, world, recordKey: key };
      add("wf_community_map_feature", key, normalized, "warframe-hub",
        `data/json/geo/${world}/${file}`, {
          world,
          featureType: properties.type,
          name: properties.name,
          geometryType: feature.geometry && feature.geometry.type
        });
    });
  }
}

// relic-data is a curated drop table. It also supplies acquisition/drop
// sources that are not part of the official relic metadata.
const relics = read("warframe-relic-data/data/Relics.json");
for (const [key, payload] of Object.entries(relics)) {
  const name = payload.name || (Array.isArray(relics) ? undefined : key);
  if (!name) throw new Error(`Missing relic name: ${key}`);
  const relicKey = payload.uniqueName || name;
  add("wf_community_relic", relicKey, { ...payload, recordKey: relicKey }, "warframe-relic-data",
    "data/Relics.json", {
      relicName: name,
      uniqueName: payload.uniqueName,
      marketId: payload.warframeMarket && payload.warframeMarket.id,
      urlName: payload.warframeMarket && payload.warframeMarket.urlName,
      vaulted: payload.vaultInfo && payload.vaultInfo.vaulted
    });
  (payload.rewards || []).forEach((reward, index) => {
    const item = reward.item || {};
    const recordKey = `${relicKey}#reward:${index}`;
    const normalized = {
      recordKey,
      relicRecordKey: relicKey,
      relicName: name,
      rewardOrder: index,
      data: reward
    };
    add("wf_community_relic_reward",
      recordKey,
      normalized, "warframe-relic-data", "data/Relics.json", {
        relicKey,
        rewardName: item.name,
        uniqueName: item.uniqueName,
        marketId: item.warframeMarket && item.warframeMarket.id,
        urlName: item.warframeMarket && item.warframeMarket.urlName,
        rarity: reward.rarity,
        chance: reward.chance,
        rewardOrder: index
      });
  });
  (payload.locations || []).forEach((location, index) => {
    const recordKey = `${relicKey}#drop:${index}`;
    add("wf_community_relic_drop", recordKey, {
      recordKey,
      relicRecordKey: relicKey,
      relicName: name,
      rewardOrder: index,
      data: location
    },
      "warframe-relic-data", "data/Relics.json", {
        relicName: name,
        relicKey,
        locationText: location.location || location.node,
        rarity: location.rarity,
        chance: location.chance,
        dropOrder: index
      });
  });
}

const unrollables = read("warframe-public-export-plus/supplementals/riven_unrollables.json");
for (const [weaponCode, attributes] of Object.entries(unrollables)) {
  for (const attribute of attributes) {
    const recordKey = `${weaponCode}#${attribute}`;
    add("wf_community_riven_unrollable", recordKey, {
      recordKey,
      weaponCode,
      attributeCode: attribute
    }, "warframe-public-export-plus", "supplementals/riven_unrollables.json",
    { attributeCode: attribute });
  }
}

// WFCD static rules used by worldstate/status APIs.
const wfcdNames = [
  "arcanes", "archonShards", "conclaveData", "eventsData", "fissureModifiers",
  "operationTypes", "persistentEnemyData", "sortieData", "steelPath",
  "syndicatesData", "synthTargets", "tutorials", "upgradeTypes"
];
for (const name of wfcdNames) {
  for (const locale of ["en", "zh"]) {
    const file = locale === "en"
      ? `warframe-worldstate-data/data/${name}.json`
      : `warframe-worldstate-data/data/zh/${name}.json`;
    if (!fs.existsSync(path.join(root, file))) continue;
    const payload = read(file);
    const values = Array.isArray(payload)
      ? payload
      : Object.entries(payload).map(([key, value]) => ({ key, value }));
    values.forEach((value, index) => add(
      "wf_community_worldstate_rule",
      `${name}:${locale}:${value.key || value.name || index}`,
      value,
      "warframe-worldstate-data",
      file.replace("warframe-worldstate-data/", ""),
      { datasetCode: name, locale }
    ));
  }
}

const lines = [
  "\\set ON_ERROR_STOP on",
  "",
  "BEGIN;",
  "",
  "-- 本脚本由 generate_community_reference_sql.js 从参考项目离线生成。",
  "-- 先执行 docs/sql/init.sql；本脚本只追加社区记录，不创建第二套表结构。",
  "-- 生成结果必须人工复核后再交给 PostgreSQL 执行。",
  ""
];

const identities = new Set();
const exactDuplicates = new Set();
for (const row of rows) {
  if (row.sourceCode.length > 200) {
    throw new Error(`source_code exceeds 200 characters: ${row.sourceCode}`);
  }
  const identity = `${row.table}\u0000${row.sourceCode}\u0000${row.key}`;
  if (identities.has(identity)) {
    const previous = rows.find(candidate =>
      candidate !== row
      && candidate.table === row.table
      && candidate.sourceCode === row.sourceCode
      && candidate.key === row.key
    );
    if (JSON.stringify(previous.payload) !== JSON.stringify(row.payload)) {
      throw new Error(
        `Conflicting source identity in ${row.table}: ${row.sourceCode} / ${row.key}`
      );
    }
    exactDuplicates.add(identity);
    continue;
  }
  identities.add(identity);

  let columns;
  let values;
  switch (row.table) {
    case "wf_community_fish_guide":
      columns = ["record_key", "name"];
      values = [row.key, row.columns.name];
      break;
    case "wf_community_map_feature":
      columns = ["record_key", "world", "feature_type", "name", "geometry_type"];
      values = [
        row.key,
        row.columns.world,
        row.columns.featureType,
        row.columns.name,
        row.columns.geometryType
      ];
      break;
    case "wf_community_relic":
      columns = ["record_key", "relic_name", "unique_name", "market_id", "url_name", "vaulted"];
      values = [
        row.key,
        row.columns.relicName,
        row.columns.uniqueName,
        row.columns.marketId,
        row.columns.urlName,
        row.columns.vaulted
      ];
      break;
    case "wf_community_relic_reward":
      columns = [
        "record_key", "relic_record_key", "reward_name", "unique_name", "market_id",
        "url_name", "rarity", "chance", "reward_order"
      ];
      values = [
        row.key,
        row.columns.relicKey,
        row.columns.rewardName,
        row.columns.uniqueName,
        row.columns.marketId,
        row.columns.urlName,
        row.columns.rarity,
        row.columns.chance,
        row.columns.rewardOrder
      ];
      break;
    case "wf_community_relic_drop":
      columns = [
        "record_key", "relic_name", "relic_record_key", "location_text",
        "rarity", "chance", "drop_order"
      ];
      values = [
        row.key,
        row.columns.relicName,
        row.columns.relicKey,
        row.columns.locationText,
        row.columns.rarity,
        row.columns.chance,
        row.columns.dropOrder
      ];
      break;
    case "wf_community_riven_unrollable":
      columns = ["record_key", "attribute_code"];
      values = [row.key, row.columns.attributeCode];
      break;
    case "wf_community_term":
      columns = ["record_key", "term_group", "english_text", "chinese_text"];
      values = [
        row.key,
        row.columns.termGroup,
        row.columns.englishText,
        row.columns.chineseText
      ];
      break;
    case "wf_community_dataset_alias":
      columns = ["record_key", "alias_name", "target_name"];
      values = [row.key, row.columns.aliasName, row.columns.targetName];
      break;
    case "wf_community_worldstate_rule":
      columns = ["record_key", "dataset_code", "locale"];
      values = [row.key, row.columns.datasetCode, row.columns.locale];
      break;
    default:
      throw new Error(`Unsupported generated table: ${row.table}`);
  }
  lines.push(communityInsert(row, columns, values));
}

lines.push("", "COMMIT;", "");
fs.writeFileSync(out, lines.join("\n"), "utf8");
console.log(`generated ${rows.length - exactDuplicates.size} rows: ${out}`);
if (exactDuplicates.size > 0) {
  console.log(`omitted ${exactDuplicates.size} exact duplicate source records`);
}
