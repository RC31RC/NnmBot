-- 手工执行于已有 PostgreSQL 数据库；与本轮代码一起更新，不由应用自动迁移。
BEGIN;
-- 数据版本与实际写入原子提交；版本通知遗漏由各实例定时读取这两列补偿。
-- 版本只能从零向上递增。
ALTER TABLE nnmbot.wf_official_dataset_file ADD COLUMN data_revision bigint NOT NULL DEFAULT 0
    CONSTRAINT ck_wf_official_dataset_revision CHECK (data_revision >= 0);
COMMENT ON COLUMN nnmbot.wf_official_dataset_file.data_revision IS '数据库可见内容版本；采纳、删除和自维护覆盖均在原写事务中递增，与上游 SHA 无关';
-- 社区各表版本独立，不能为负。
ALTER TABLE nnmbot.wf_community_dataset_lock ADD COLUMN data_revision bigint NOT NULL DEFAULT 0
    CONSTRAINT ck_wf_community_dataset_revision CHECK (data_revision >= 0);
COMMENT ON COLUMN nnmbot.wf_community_dataset_lock.data_revision IS '社区数据集可见内容版本；与数据变更同事务递增，用于本地缓存及跨实例版本补偿';
-- 每个 JSON 预建文件锁及版本行，首次官方导入前的自维护写入也有独立版本。
INSERT INTO nnmbot.wf_official_dataset_file(product_path, checked_at) VALUES
    ('crafting/recipes.json.gz', TIMESTAMPTZ 'epoch'),
    ('crafting/dojo-recipes.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/abilities.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/archwings.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/companions-animals.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/companions-sentinels.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/gear.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/railjack-weapons.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/resource-drones.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/ships.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/warframes.json.gz', TIMESTAMPTZ 'epoch'),
    ('equipment/weapons.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/ayatan-treasures.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/boosters.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/bundles.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/captura-scenes.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/components.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/credit-bundles.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/customizations.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/decorations.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/endo-bundles.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/internal-definitions.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/misc-items.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/resources.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/reward-packs.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/skins.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/songs.json.gz', TIMESTAMPTZ 'epoch'),
    ('items/supply-drops.json.gz', TIMESTAMPTZ 'epoch'),
    ('market/trade-items.json.gz', TIMESTAMPTZ 'epoch'),
    ('progression/achievements.json.gz', TIMESTAMPTZ 'epoch'),
    ('progression/challenges.json.gz', TIMESTAMPTZ 'epoch'),
    ('progression/intrinsics.json.gz', TIMESTAMPTZ 'epoch'),
    ('progression/nightwave.json.gz', TIMESTAMPTZ 'epoch'),
    ('reference/images.json.gz', TIMESTAMPTZ 'epoch'),
    ('reference/item-localizations.json.gz', TIMESTAMPTZ 'epoch'),
    ('reference/ability-owner-localizations.json.gz', TIMESTAMPTZ 'epoch'),
    ('reference/upgrade-localizations.json.gz', TIMESTAMPTZ 'epoch'),
    ('reference/language-strings.json.gz', TIMESTAMPTZ 'epoch'),
    ('reference/text-icons.json.gz', TIMESTAMPTZ 'epoch'),
    ('rewards/bounties.json.gz', TIMESTAMPTZ 'epoch'),
    ('rewards/drop-tables.json.gz', TIMESTAMPTZ 'epoch'),
    ('rewards/relics.json.gz', TIMESTAMPTZ 'epoch'),
    ('rewards/reward-pools.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/arcanes.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/avionics.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/focus-upgrades.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/mod-sets.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/mods.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/riven-attributes.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/riven-definitions.json.gz', TIMESTAMPTZ 'epoch'),
    ('upgrades/riven-weapons.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/codex.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/enemies.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/inbox-messages.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/quest-keys.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/star-chart.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/tilesets.json.gz', TIMESTAMPTZ 'epoch'),
    ('world/vendors.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/activities.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/bosses.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/factions.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/fissure-modifiers.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/mission-types.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/syndicates.json.gz', TIMESTAMPTZ 'epoch'),
    ('worldstate/upgrade-types.json.gz', TIMESTAMPTZ 'epoch'),
    ('statistics/usage-2020.json.gz', TIMESTAMPTZ 'epoch'),
    ('statistics/usage-2021.json.gz', TIMESTAMPTZ 'epoch'),
    ('statistics/usage-2022.json.gz', TIMESTAMPTZ 'epoch'),
    ('statistics/usage-2023.json.gz', TIMESTAMPTZ 'epoch'),
    ('statistics/usage-2024.json.gz', TIMESTAMPTZ 'epoch'),
    ('statistics/usage-2025.json.gz', TIMESTAMPTZ 'epoch')
ON CONFLICT (product_path) DO NOTHING;

COMMIT;
