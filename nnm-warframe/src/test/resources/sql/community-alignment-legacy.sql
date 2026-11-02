-- Historical rows for disposable migration verification, never application seeds.
INSERT INTO nnmbot.wf_community_dataset_reward_pool (
    record_key, source_code, record_source, current_json, rewards_json,
    upstream_json, original_json, manual_fields_json, upstream_hash, source_hash,
    manual_modified, created_at, updated_at, last_imported_at
)
SELECT data.record_key, data.source_code, data.record_source, data.payload, data.legacy_rewards,
    ' { "verifyAlignment": true, "audit": "latest" } ',
    ' { "verifyAlignment": true, "audit": "first" } ', '{"custom":"keep"}',
    repeat('a',64), repeat('b',64), true,
    timestamp '2026-09-01 12:00:00', timestamp '2026-09-02 12:00:00', timestamp '2026-09-02 11:00:00'
FROM (VALUES
    ('/Lotus/Rewards/VerifyTable', 'warframe-public-export-plus:ExportRewards.json', 'COMMUNITY',
     '{"stages":[[{"type":"/Lotus/StoreItems/A/Shared","itemCount":2,"probability":0.2,"custom":"raw"}],[],[{"item":{"uniqueName":"/Lotus/StoreItems/B/Shared"},"count":0,"rarity":"rare","chance":50}]],"custom":"pool"}', NULL),
    ('/Lotus/Rewards/VerifyTable', 'kingprimes:warframe/reward_pool.json', 'COMMUNITY',
     '{"rewards":[{"stage":2,"itemKey":"/Lotus/StoreItems/Fallback","count":1,"rarity":"common"}]}', NULL),
    ('/Lotus/Rewards/VerifySelf', 'legacy:reward_pool', 'SELF_MAINTAINED',
     '{"rewards":[{"stage":3,"item":"/Lotus/StoreItems/Self","count":4,"probability":1}]}', NULL),
    ('/Lotus/Rewards/VerifyEmpty', 'warframe-public-export-plus:ExportRewards.json', 'COMMUNITY',
     '{"stages":[]}', NULL),
    ('/Lotus/Rewards/VerifyLegacy', 'warframe-info-api:reward_pool_local.json', 'COMMUNITY',
     '{"uniqueName":"/Lotus/Rewards/VerifyLegacy","custom":"legacy"}',
     '[{"itemKey":"/Lotus/StoreItems/Legacy","count":1,"probability":0,"rarity":"uncommon"}]')
) AS data(record_key, source_code, record_source, payload, legacy_rewards);

INSERT INTO nnmbot.wf_community_dataset_translation (
    record_key, source_code, record_source, locale, chinese_name, english_name, current_json,
    source_id, prime_item, set_item, upstream_json, original_json, manual_fields_json,
    upstream_hash, source_hash, manual_modified, created_at, updated_at, last_imported_at
)
SELECT '/Lotus/Language/Verify/Shared', data.source_code, data.record_source, data.locale,
    data.chinese_name, data.english_name, data.payload, 42, true, false,
    ' { "verifyAlignment": true, "audit": "latest" } ',
    ' { "verifyAlignment": true, "audit": "first" } ', '{"custom":"keep"}',
    repeat('c',64), repeat('d',64), true,
    timestamp '2026-09-01 12:00:00', timestamp '2026-09-02 12:00:00', timestamp '2026-09-02 11:00:00'
FROM (VALUES
    ('warframe-public-export-plus:dict.zh.json', 'COMMUNITY', 'zh', '中文词条', NULL,
     '{"cn":"中文词条","custom":"zh"}'),
    ('warframe-public-export-plus:dict.en.json', 'COMMUNITY', 'zh', NULL, 'English entry',
     '{"en":"English entry","custom":"en"}'),
    ('kingprimes:warframe/translation.json', 'COMMUNITY', NULL, '旧中文', 'Legacy English',
     '{"cn":"旧中文","en":"Legacy English","custom":"retired"}'),
    ('legacy:translation', 'SELF_MAINTAINED', ' ZH ', '自维护中文', 'Self English',
     '{"displayName":"自定义中文","cn":"自维护中文","en":"Self English","custom":"self"}')
) AS data(source_code, record_source, locale, chinese_name, english_name, payload);

-- Formal, excluded and self-maintained parents have matching child identities.
INSERT INTO nnmbot.wf_community_relic (
    record_key, relic_name, source_code, record_source, source_hash,
    current_json, upstream_json, original_json
)
SELECT '/Lotus/Types/Game/Projections/VerifyRelic', 'Verify Relic', data.source_code,
    data.record_source, repeat('e',64), '{"name":"Verify Relic"}',
    '{"verifyAlignment":true,"audit":"latest"}', '{"verifyAlignment":true,"audit":"first"}'
FROM (VALUES
    ('warframe-relic-data:data/Relics.json', 'COMMUNITY'),
    ('warframe-relic-data:data/Relics.min.json', 'COMMUNITY'),
    ('warframe-items:data/json/Relics.json', 'COMMUNITY'),
    ('warframe-public-export-plus:ExportRelics.json', 'COMMUNITY'),
    ('nnmbot:relic', 'SELF_MAINTAINED')
) AS data(source_code, record_source);
INSERT INTO nnmbot.wf_community_relic_reward (
    record_key, relic_record_key, source_code, record_source, reward_order, source_hash,
    current_json, upstream_json, original_json
)
SELECT record_key || ':reward:1', record_key, source_code, record_source, 1, source_hash,
    '{"data":{"rarity":"Rare","chance":2}}', upstream_json, original_json
FROM nnmbot.wf_community_relic WHERE original_json::jsonb ? 'verifyAlignment';
INSERT INTO nnmbot.wf_community_relic_drop (
    record_key, relic_record_key, relic_name, source_code, record_source, source_hash,
    current_json, upstream_json, original_json
)
SELECT record_key || ':drop:1', record_key, relic_name, source_code, record_source, source_hash,
    '{"data":{"location":"Verify","rarity":"Rare","chance":2}}', upstream_json, original_json
FROM nnmbot.wf_community_relic WHERE original_json::jsonb ? 'verifyAlignment';
