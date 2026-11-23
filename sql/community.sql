-- 新数据库在 sql/init.sql 后执行；已运行带种子的旧版 init/01 的数据库无需因本次拆分重跑。
-- 仅承载游戏业务资料初始化：社区白名单及官方业务表的 SELF_MAINTAINED 记录。
-- 数据集锁行、权限、菜单等应用运行所需固定数据仍由 init.sql 负责。
BEGIN;
-- 原有纯人工紫卡中文别名及组合；已有同业务身份的记录优先。
INSERT INTO nnmbot.wf_community_riven_alias (record_key, attribute_code, alias_name, updated_at, version)
SELECT '暴击', 'critical_chance', '暴击', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='暴击');
INSERT INTO nnmbot.wf_community_riven_alias (record_key, attribute_code, alias_name, updated_at, version)
SELECT '基础伤害', 'base_damage_/_melee_damage', '基础伤害', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='基础伤害');
INSERT INTO nnmbot.wf_community_riven_alias (record_key, attribute_code, alias_name, updated_at, version)
SELECT '基双爆', 'base_damage_/_melee_damage+critical_chance+critical_damage', '基双爆', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='基双爆');
INSERT INTO nnmbot.wf_community_riven_alias (record_key, attribute_code, alias_name, updated_at, version)
SELECT '多双爆', 'multishot+critical_chance+critical_damage', '多双爆', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='多双爆');
INSERT INTO nnmbot.wf_community_riven_alias (record_key, attribute_code, alias_name, updated_at, version)
SELECT '滑爆', 'critical_chance_on_slide_attack', '滑爆', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='滑爆');
INSERT INTO nnmbot.wf_community_riven_alias (record_key, attribute_code, alias_name, updated_at, version)
SELECT '划爆', 'critical_chance_on_slide_attack', '划爆', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='划爆');

-- BEGIN WARFRAME REFERENCE SEED
-- 离线来源、取舍和校验摘要见 scripts/database/data/warframe-reference-review.json。
-- 中文解释与逐项清单见 scripts/database/data/参考数据核对说明.md。
-- 只插入缺失自维护资料，不覆盖人工记录，不推进缓存版本；请在应用启动前执行。
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_ALAD', 'Alad V', '阿拉德 V', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Alad V', 2, 'Alad V', 2, '/Lotus/Language/Bosses/BossAladV', 2, '阿拉德 V', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_ALAD'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_AMBULAS', 'Ambulas', '智踱恐鸟', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Ambulas', 2, 'Ambulas', 2, '/Lotus/Language/Game/BossAmbulas', 2, '智踱恐鸟', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_AMBULAS'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_CORRUPTED_VOR', 'Corrupted Vor', '堕落的沃尔', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Corrupted Vor', 2, 'Corrupted Vor', 2, '/Lotus/Language/Game/VorTwo', 2, '堕落的沃尔', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_CORRUPTED_VOR'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_HEK', 'Councilor Vay Hek', '韦·海克议员', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Councilor Vay Hek', 2, 'Councilor Vay Hek', 2, '/Lotus/Language/Bosses/BossCouncilorVayHek', 2, '韦·海克议员', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_HEK'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_HYENA', 'Hyena Pack', '鬣狗群', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Hyena Pack', 2, 'Hyena Pack', 2, '/Lotus/Language/Game/QuadHyena', 2, '鬣狗群', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_HYENA'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_INFALAD', 'Mutalist Alad V', '异融阿拉德 V', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Mutalist Alad V', 2, 'Mutalist Alad V', 2, '/Lotus/Language/Bosses/BossInfestedAladV', 2, '异融阿拉德 V', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_INFALAD'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_JACKAL', 'Jackal', '豺狼', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Jackal', 2, 'Jackal', 2, '/Lotus/Language/Game/QuadRobot', 2, '豺狼', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_JACKAL'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_KELA', 'Kela De Thaym', '凯拉·德·塞姆', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Kela De Thaym', 2, 'Kela De Thaym', 2, '/Lotus/Language/Bosses/BossKelaDeThaym', 2, '凯拉·德·塞姆', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_KELA'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_KRIL', 'Lech Kril', 'Lech Kril 中尉', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Lech Kril', 2, 'Lech Kril', 2, '/Lotus/Language/Bosses/BossLieutenantLechKril', 2, 'Lech Kril 中尉', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_KRIL'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_LEPHANTIS', 'Lephantis', '雷凡魔像', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Lephantis', 2, 'Lephantis', 2, '/Lotus/Language/Bosses/BossLephantis', 2, '雷凡魔像', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_LEPHANTIS'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_NEF', 'Nef Anyo', '奈富·安尤', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Nef Anyo', 2, 'Nef Anyo', 2, '/Lotus/Language/Bosses/NefAnyoName', 2, '奈富·安尤', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_NEF'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_PAAZUL', 'Archon Paazul', '帕祖', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Archon Paazul', 2, 'Archon Paazul', 0, 2, '帕祖', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_PAAZUL'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_PHORID', 'Phorid', 'Phorid', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Phorid', 2, 'Phorid', 2, '/Lotus/Language/Bosses/BossPhorid', 2, 'Phorid', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_PHORID'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_RAPTOR', 'Raptor', '猛禽', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Raptor', 2, 'Raptor', 2, '/Lotus/Language/Game/Raptor', 2, '猛禽', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_RAPTOR'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_RUK', 'General Sargas Ruk', 'Sargas Ruk 将军', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'General Sargas Ruk', 2, 'General Sargas Ruk', 2, '/Lotus/Language/Bosses/BossGeneralSargasRuk', 2, 'Sargas Ruk 将军', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_RUK'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_TYL', 'Tyl Regor', '泰尔·雷工', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Tyl Regor', 2, 'Tyl Regor', 2, '/Lotus/Language/Bosses/BossTylRegor', 2, '泰尔·雷工', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_TYL'));
-- BOSS：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_boss_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, name_state, name, nameen_state, nameen, namekey_state, namekey, namezh_state, namezh, shardcategory_selection_state, shardcategory_variants_state)
SELECT 'SELF_MAINTAINED', 'SORTIE_BOSS_VOR', 'Captain Vor', '沃尔上尉', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'Captain Vor', 2, 'Captain Vor', 2, '/Lotus/Language/Bosses/BossCaptainVor', 2, '沃尔上尉', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_boss_root WHERE lower(bkey)=lower('SORTIE_BOSS_VOR'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/AbilityStrength', NULL, '力量飙升', '增加25%技能强度。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/AbilityStrength', 2, '增加25%技能强度。', 2, '力量飙升'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/AbilityStrength'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/Armor', NULL, '硬化装甲', '增加250护甲。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/Armor', 2, '增加250护甲。', 2, '硬化装甲'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/Armor'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/AttackAndMovementSpeedOnCritMelee', NULL, '快刀斩乱麻', '近战暴击有几率增加5%攻击速度和移动速度，持续10秒。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/AttackAndMovementSpeedOnCritMelee', 2, '近战暴击有几率增加5%攻击速度和移动速度，持续10秒。', 2, '快刀斩乱麻'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/AttackAndMovementSpeedOnCritMelee'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/CompanionsRadiationChance', NULL, '友军余波', '分身或同伴的攻击有25%几率造成辐射异常状态。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/CompanionsRadiationChance', 2, '分身或同伴的攻击有25%几率造成辐射异常状态。', 2, '友军余波'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/CompanionsRadiationChance'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/ElectricDamagePerDistance', NULL, '远距电击', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/ElectricDamagePerDistance', 0, 2, '远距电击'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/ElectricDamagePerDistance'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/ElectricStatusDamageAndChance', NULL, '瓶装闪电', '为所有武器增加25%的异常状态伤害和电击异常状态触发几率。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/ElectricStatusDamageAndChance', 2, '为所有武器增加25%的异常状态伤害和电击异常状态触发几率。', 2, '瓶装闪电'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/ElectricStatusDamageAndChance'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/EnergyRestoration', NULL, '特浓咖啡', '增加每秒2能量恢复。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/EnergyRestoration', 2, '增加每秒2能量恢复。', 2, '特浓咖啡'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/EnergyRestoration'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/ExplodingHealthOrbs', NULL, '劣质医疗', '生命球拾取后会产生范围10米的爆炸，伤害附近的敌人。每击中一个敌人，增加50护甲，持续30秒。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/ExplodingHealthOrbs', 2, '生命球拾取后会产生范围10米的爆炸，伤害附近的敌人。每击中一个敌人，增加50护甲，持续30秒。', 2, '劣质医疗'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/ExplodingHealthOrbs'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/FinisherChancePerComboMultiplier', NULL, '杀意连段', '连击倍率将增加敌人在受到近战攻击后进入可处决状态的几率。每层倍率提高5%几率，使用凯旋之爪Prime时最大可达65%', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/FinisherChancePerComboMultiplier', 2, '连击倍率将增加敌人在受到近战攻击后进入可处决状态的几率。每层倍率提高5%几率，使用凯旋之爪Prime时最大可达65%', 2, '杀意连段'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/FinisherChancePerComboMultiplier'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/GasChanceToPrimaryAndSecondary', NULL, '毒气弹头', '主要武器和副武器增加25%毒气触发几率。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/GasChanceToPrimaryAndSecondary', 2, '主要武器和副武器增加25%毒气触发几率。', 2, '毒气弹头'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/GasChanceToPrimaryAndSecondary'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/HealingEffects', NULL, '应急特效药', '所有补给球拾取物提供的恢复效果提高25%。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/HealingEffects', 2, '所有补给球拾取物提供的恢复效果提高25%。', 2, '应急特效药'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/HealingEffects'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/MagazineCapacity', NULL, '重型弹夹', '增加25% 的弹匣容量。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/MagazineCapacity', 2, '增加25% 的弹匣容量。', 2, '重型弹夹'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/MagazineCapacity'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/MagnetStatusPull', NULL, '引力', 'C:磁力异常状态会将1米范围内的敌人拉近，每层磁力异常状态将增加1米范围。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/MagnetStatusPull', 2, 'C:磁力异常状态会将1米范围内的敌人拉近，每层磁力异常状态将增加1米范围。', 2, '引力'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/MagnetStatusPull'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/MeleeAttackSpeed', NULL, '绝不留情', '近战攻击速度 +25%。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/MeleeAttackSpeed', 2, '近战攻击速度 +25%。', 2, '绝不留情'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/MeleeAttackSpeed'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/MeleeCritChance', NULL, '熟能生巧', '增加20%近战暴击几率(重击时x2)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/MeleeCritChance', 2, '增加20%近战暴击几率(重击时x2)', 2, '熟能生巧'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/MeleeCritChance'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/OrbsDuplicateOnPickup', NULL, '靶向治疗', '可通过射击生命球来拾取它们。生命球拾取后有25%的几率复制。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/OrbsDuplicateOnPickup', 2, '可通过射击生命球来拾取它们。生命球拾取后有25%的几率复制。', 2, '靶向治疗'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/OrbsDuplicateOnPickup'));
-- ITEM_LOCALIZATION：只补充本领域的名称／描述，其他业务主表的完整属性保持独立。
INSERT INTO nnmbot.wf_official_item_localization_root (maintenance_type, bkey, official_name, localized_name, description, enabled, created_at, updated_at, root_state, description_value_state, iconurl_state, name_state, uniquename_state, uniquename, zhdescription_state, zhdescription, zhname_state, zhname)
SELECT 'SELF_MAINTAINED', '/Lotus/Upgrades/Calendar/ReviveEnemyAsSpectreOnKill', NULL, '救赎契机', '敌人死亡时每有一层异常状态，就增加1%的几率复活为友方魅影。持续30秒。', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 0, 0, 0, 2, '/Lotus/Upgrades/Calendar/ReviveEnemyAsSpectreOnKill', 2, '敌人死亡时每有一层异常状态，就增加1%的几率复活为友方魅影。持续30秒。', 2, '救赎契机'
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_official_item_localization_root WHERE lower(bkey)=lower('/Lotus/Upgrades/Calendar/ReviveEnemyAsSpectreOnKill'));
-- 社区alias：按业务内容去重，多目标别名分别保留，既有人工记录优先。
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d47deffa0afaedec5e17b7916c5ff6c40a04cd4f592c33fbeea23e20deaf9392', 'C喷', '弧电离子枪', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d47deffa0afaedec5e17b7916c5ff6c40a04cd4f592c33fbeea23e20deaf9392' OR (alias_name='C喷' AND target_name='弧电离子枪'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:69ac9bd897914f77a055f61e6ac8be1611ea4ee213c431e8493009fb0aed66cf', 'C矛', '铁晶磁轨炮', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:69ac9bd897914f77a055f61e6ac8be1611ea4ee213c431e8493009fb0aed66cf' OR (alias_name='C矛' AND target_name='铁晶磁轨炮'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6195617da4ebed89b5d869f0206351f46e9de41eb4228262d03378030f79e8f0', 'DJ P', 'Octavia Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6195617da4ebed89b5d869f0206351f46e9de41eb4228262d03378030f79e8f0' OR (alias_name='DJ P' AND target_name='Octavia Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e6bed4933b7759da3ab328b1c02b73887d809bb8d7854298e03f7a6147a2e65b', 'Dj', 'Octavia', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e6bed4933b7759da3ab328b1c02b73887d809bb8d7854298e03f7a6147a2e65b' OR (alias_name='Dj' AND target_name='Octavia'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0702db8bb6124cdda0be0951b7a5b2b95c313fe094138e6226b618af0cabc728', 'I鞭', '脊椎节鞭', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0702db8bb6124cdda0be0951b7a5b2b95c313fe094138e6226b618af0cabc728' OR (alias_name='I鞭' AND target_name='脊椎节鞭'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c840a2a85b55333fb06d24a293df6b3453ef493a9119d84eb95a0f19f95a9572', 'O炮', '奥堤克光子枪', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c840a2a85b55333fb06d24a293df6b3453ef493a9119d84eb95a0f19f95a9572' OR (alias_name='O炮' AND target_name='奥堤克光子枪'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:97a8f64ee4de8fafb0b9f8ab7a42b1af5e09af4e5d22a70dec3fe6c0f013e131', '☯️暗影阴阳双子生 PRIME', 'EQUINOX PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:97a8f64ee4de8fafb0b9f8ab7a42b1af5e09af4e5d22a70dec3fe6c0f013e131' OR (alias_name='☯️暗影阴阳双子生 PRIME' AND target_name='EQUINOX PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6bd776af8e306a9a23becbe47c1746ee4332bb5e37cf71dbd5f252e229604fb6', '☵♒︎💧水男 PRIME', 'HYDROID PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6bd776af8e306a9a23becbe47c1746ee4332bb5e37cf71dbd5f252e229604fb6' OR (alias_name='☵♒︎💧水男 PRIME' AND target_name='HYDROID PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:de81d9983fddf5973161b19ecc9f97bb3b082d3dd89b8da9bbf51e53ed42097f', '✝主教 PRIME', 'HARROW PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:de81d9983fddf5973161b19ecc9f97bb3b082d3dd89b8da9bbf51e53ed42097f' OR (alias_name='✝主教 PRIME' AND target_name='HARROW PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c417071123179494c8897ada591201462560da1550fc5144d9c5bfb1ea25a7af', '一拳超人', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c417071123179494c8897ada591201462560da1550fc5144d9c5bfb1ea25a7af' OR (alias_name='一拳超人' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:cf1cfc70ef76984f7ba41a6d73c8b0f4d78f68a26641b0a08aeab3216a2ddaf3', '三位一体 PRIME', 'TRINITY PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:cf1cfc70ef76984f7ba41a6d73c8b0f4d78f68a26641b0a08aeab3216a2ddaf3' OR (alias_name='三位一体 PRIME' AND target_name='TRINITY PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:61a373f9b67277f05ecac69bac5497e26628a4270c09cf3768f49a1b6c39c8bf', '三傻', 'Hydrolyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:61a373f9b67277f05ecac69bac5497e26628a4270c09cf3768f49a1b6c39c8bf' OR (alias_name='三傻' AND target_name='Hydrolyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5f73719962746d75c44d90dc8e59490ca7932577c05b003ad2feaa50cdc34260', '主教', 'Harrow', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5f73719962746d75c44d90dc8e59490ca7932577c05b003ad2feaa50cdc34260' OR (alias_name='主教' AND target_name='Harrow'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:21c51e82db6dca4d1ca778a004f93f00a60e385fe8635eec1561425040b00fc1', '主教 P', 'Harrow Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:21c51e82db6dca4d1ca778a004f93f00a60e385fe8635eec1561425040b00fc1' OR (alias_name='主教 P' AND target_name='Harrow Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2ad781d938b682c48a429d3c1c27c568aa8d6ff393787939d278c158ed93d2a6', '二傻', 'Gantulyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2ad781d938b682c48a429d3c1c27c568aa8d6ff393787939d278c158ed93d2a6' OR (alias_name='二傻' AND target_name='Gantulyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a2bc925b25db9c491b4165eee797a86c32fda571f55f51a38194e4706565ca13', '人马', 'Dagath', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a2bc925b25db9c491b4165eee797a86c32fda571f55f51a38194e4706565ca13' OR (alias_name='人马' AND target_name='Dagath'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2106b0e34775829b1c087e02c1483f6265396f7b292be487d4c170aebefaf532', '人马 P', 'Dagath Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2106b0e34775829b1c087e02c1483f6265396f7b292be487d4c170aebefaf532' OR (alias_name='人马 P' AND target_name='Dagath Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a6b732309d90c4a1725f16771f02470e6afe743d8462740a8c4f1687158148e4', '但丁', 'Dante', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a6b732309d90c4a1725f16771f02470e6afe743d8462740a8c4f1687158148e4' OR (alias_name='但丁' AND target_name='Dante'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4b71639711715142f239450c32e0818def5b5aafb01d2eef2f014cbe2e7743be', '但丁 P', 'Dante Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4b71639711715142f239450c32e0818def5b5aafb01d2eef2f014cbe2e7743be' OR (alias_name='但丁 P' AND target_name='Dante Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:468e69cbf8c9510f5f6495b1d8f579d1b0b105b94022b6a6140de676e4188375', '僧甲', 'Baruuk', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:468e69cbf8c9510f5f6495b1d8f579d1b0b105b94022b6a6140de676e4188375' OR (alias_name='僧甲' AND target_name='Baruuk'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ab7c3a0b33d58898a006c6ed9ac2332c75d4a08e3212dd9a39fd2562283282ae', '兆力使', 'Teralyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ab7c3a0b33d58898a006c6ed9ac2332c75d4a08e3212dd9a39fd2562283282ae' OR (alias_name='兆力使' AND target_name='Teralyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6da8dbc382d90867711286db5af32ed408ac4e8bf9e687c11c91b1b8de91677b', '冰男', 'Frost', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6da8dbc382d90867711286db5af32ed408ac4e8bf9e687c11c91b1b8de91677b' OR (alias_name='冰男' AND target_name='Frost'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3f730d765c2625a7e210267daebac7b6c4b667c33e19bac1f78b780868ec53ca', '冰男 P', 'Frost Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3f730d765c2625a7e210267daebac7b6c4b667c33e19bac1f78b780868ec53ca' OR (alias_name='冰男 P' AND target_name='Frost Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:38321e71bbeed072a9678ea60556c22b66ced3aa74ba0867cc7d820775f4a319', '冰队', 'Frost', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:38321e71bbeed072a9678ea60556c22b66ced3aa74ba0867cc7d820775f4a319' OR (alias_name='冰队' AND target_name='Frost'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:30fbdee6d45b4132d62d27593af540fbe3cc184744f2795ad4a5df646a9fafa1', '冰队 P', 'Frost Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:30fbdee6d45b4132d62d27593af540fbe3cc184744f2795ad4a5df646a9fafa1' OR (alias_name='冰队 P' AND target_name='Frost Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d69930053b0b862063017487b9096754c22d58ce83cc8209d8dae9479d7eb583', '冰雪寒霜', 'Frost', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d69930053b0b862063017487b9096754c22d58ce83cc8209d8dae9479d7eb583' OR (alias_name='冰雪寒霜' AND target_name='Frost'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ad955c775e3b0a2e46e5ac00ba660cdcaed2dbd54706c6a89f8e218366ddca1f', '减速娃', 'Nova', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ad955c775e3b0a2e46e5ac00ba660cdcaed2dbd54706c6a89f8e218366ddca1f' OR (alias_name='减速娃' AND target_name='Nova'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:962c1678fd0f73a97de2072259094cea3bcf14de667ae14e42f93f551a38f418', '刀哥', 'Kullervo', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:962c1678fd0f73a97de2072259094cea3bcf14de667ae14e42f93f551a38f418' OR (alias_name='刀哥' AND target_name='Kullervo'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:df8642fbbd7246a0e0573e9d61380c6ef18b641dd11a2dab3ff63c112297d822', '刀哥 P', 'Kullervo Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:df8642fbbd7246a0e0573e9d61380c6ef18b641dd11a2dab3ff63c112297d822' OR (alias_name='刀哥 P' AND target_name='Kullervo Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:745e202621df604b35953c5cebea6bf21e1d2c7d6886a94ed8f6adf5969d685b', '加速娃', 'Nova', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:745e202621df604b35953c5cebea6bf21e1d2c7d6886a94ed8f6adf5969d685b' OR (alias_name='加速娃' AND target_name='Nova'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:eead4639351a3e465ab8ef6662bb982300b28c7c49caf3669b426048d4c64ea0', '加速娃 P', 'Nova Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:eead4639351a3e465ab8ef6662bb982300b28c7c49caf3669b426048d4c64ea0' OR (alias_name='加速娃 P' AND target_name='Nova Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:204d92fa4d231882c7e4825417a8b9672c740a3bcbda06485e061015a3453e7d', '卡利班', 'Caliban', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:204d92fa4d231882c7e4825417a8b9672c740a3bcbda06485e061015a3453e7d' OR (alias_name='卡利班' AND target_name='Caliban'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5a537c144d918493e8e4b36a768df64f27aaa7841a8d98f4727961c98704fd2b', '卡利班 P', 'Caliban Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5a537c144d918493e8e4b36a768df64f27aaa7841a8d98f4727961c98704fd2b' OR (alias_name='卡利班 P' AND target_name='Caliban Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d790eeb48a9521cd1ddc6494f6a5474dbe70e9598d780b22a5d2a48d2f4415e3', '双子', 'Equinox', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d790eeb48a9521cd1ddc6494f6a5474dbe70e9598d780b22a5d2a48d2f4415e3' OR (alias_name='双子' AND target_name='Equinox'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:070910ddaab71cc8e4f7c69875af58bc51a77a262618d93f0df65f0f9039c5ab', '双子', 'Orion', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:070910ddaab71cc8e4f7c69875af58bc51a77a262618d93f0df65f0f9039c5ab' OR (alias_name='双子' AND target_name='Orion'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:75251c6bda06c4acac3edf4edd6d69a92202494c92be88b69eddf86bb0ac19a9', '双子', 'Sirius', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:75251c6bda06c4acac3edf4edd6d69a92202494c92be88b69eddf86bb0ac19a9' OR (alias_name='双子' AND target_name='Sirius'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:bb47b791d07fe315ce169e128ec3a243f3adfe5eea38362cb805c793a73ae933', '双子 p', 'Orion Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:bb47b791d07fe315ce169e128ec3a243f3adfe5eea38362cb805c793a73ae933' OR (alias_name='双子 p' AND target_name='Orion Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:99ca0d8edf5b1083c7c9047eeebb37fd348566f16b6cd6e7aea8703bc33a9983', '双子 p', 'Sirius Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:99ca0d8edf5b1083c7c9047eeebb37fd348566f16b6cd6e7aea8703bc33a9983' OR (alias_name='双子 p' AND target_name='Sirius Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a4e68040df2e093e42121d9a26a8754e6d0a92557fdfb816279268cf9572b438', '吉他 p', 'Temple Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a4e68040df2e093e42121d9a26a8754e6d0a92557fdfb816279268cf9572b438' OR (alias_name='吉他 p' AND target_name='Temple Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c632d36407c37b19b015692bbc8c0da3f28eecc14a6f1e3af17ccdb6586e6bab', '吉他甲', 'Temple', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c632d36407c37b19b015692bbc8c0da3f28eecc14a6f1e3af17ccdb6586e6bab' OR (alias_name='吉他甲' AND target_name='Temple'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a2831bb9948406c8cae93c10c2d45cadd430fdd8ec5180ba2f522a9bead1dd16', '吹风机', '寇恩热能枪', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a2831bb9948406c8cae93c10c2d45cadd430fdd8ec5180ba2f522a9bead1dd16' OR (alias_name='吹风机' AND target_name='寇恩热能枪'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b2f03db1f969717c46889ec670797027f37415c7a78efff2957a9cfbcff585fe', '咖喱', 'Excalibur', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b2f03db1f969717c46889ec670797027f37415c7a78efff2957a9cfbcff585fe' OR (alias_name='咖喱' AND target_name='Excalibur'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1086ee81215c431c8dab8ddd5967a5c62ecb649a10d3f9186abc716b10917689', '咖喱 P', 'Excalibur Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1086ee81215c431c8dab8ddd5967a5c62ecb649a10d3f9186abc716b10917689' OR (alias_name='咖喱 P' AND target_name='Excalibur Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:36fd773ac83ed866a751ea2afa6e7d089872398be01a805179ab74fcbd9cb149', '咖喱棒', 'Excalibur', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:36fd773ac83ed866a751ea2afa6e7d089872398be01a805179ab74fcbd9cb149' OR (alias_name='咖喱棒' AND target_name='Excalibur'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:93c1ed05a31f617923a3e839858dab3cf568f9245b5d2017cfd22ccbdd38ef99', '咖喱棒 P', 'Excalibur Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:93c1ed05a31f617923a3e839858dab3cf568f9245b5d2017cfd22ccbdd38ef99' OR (alias_name='咖喱棒 P' AND target_name='Excalibur Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:29be6da31ec7e6f642b7a2fc5efeb22a53edd2480267ff84655186bdbee18216', '哪吒', 'Nezha', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:29be6da31ec7e6f642b7a2fc5efeb22a53edd2480267ff84655186bdbee18216' OR (alias_name='哪吒' AND target_name='Nezha'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8aea5721c1378dcc2ded82466ecf62336230aa28bb038e3e6e982f588223aee3', '哪吒 P', 'Nezha Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8aea5721c1378dcc2ded82466ecf62336230aa28bb038e3e6e982f588223aee3' OR (alias_name='哪吒 P' AND target_name='Nezha Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d1b578ba93aad06ec9f34ef68d6c4adb341aa7ba5bd199fcc230a21e39a55a1c', '哪吒 PRIME', 'NEZHA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d1b578ba93aad06ec9f34ef68d6c4adb341aa7ba5bd199fcc230a21e39a55a1c' OR (alias_name='哪吒 PRIME' AND target_name='NEZHA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e4baf0f83fd43524dad4fa7980752629acfd10cd49c0a11f5cc14a61ced420ca', '啊什', 'Ash', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e4baf0f83fd43524dad4fa7980752629acfd10cd49c0a11f5cc14a61ced420ca' OR (alias_name='啊什' AND target_name='Ash'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4e91d5a044df5303f285d6f6d6814b3e85f2c4529a7f327a0fdd743da77564b5', '啊屎', 'Ash', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4e91d5a044df5303f285d6f6d6814b3e85f2c4529a7f327a0fdd743da77564b5' OR (alias_name='啊屎' AND target_name='Ash'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:46931e9ce059e354872e6987fb2b466c985ab24d6fb54d50d3197fbc61679bb8', '喵猫😺😸😹😻😼😽🙀😿😾🐱甲 PRIME', 'KHORA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:46931e9ce059e354872e6987fb2b466c985ab24d6fb54d50d3197fbc61679bb8' OR (alias_name='喵猫😺😸😹😻😼😽🙀😿😾🐱甲 PRIME' AND target_name='KHORA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c5a6f4cb9c4f2394ecf10ac3d8fb7d2dcd5189adc576162e4e36e79836c4590d', '喷火器', 'Ignis', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c5a6f4cb9c4f2394ecf10ac3d8fb7d2dcd5189adc576162e4e36e79836c4590d' OR (alias_name='喷火器' AND target_name='Ignis'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5bbaf4c7e018f8efaf0ccc1b2366e7f6c8e194fe670d7a885b8899138a4d8268', '土甲', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5bbaf4c7e018f8efaf0ccc1b2366e7f6c8e194fe670d7a885b8899138a4d8268' OR (alias_name='土甲' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7d6ceec6700ed75b93e54c02aaf9d537efffe44878ab1a4d57c5d984cace5115', '土甲 P', 'Atlas Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7d6ceec6700ed75b93e54c02aaf9d537efffe44878ab1a4d57c5d984cace5115' OR (alias_name='土甲 P' AND target_name='Atlas Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2f57c10da8a3913b95d31fe35e992f9167685539bfe07b58942a02a8e3ac02f1', '土甲 PRIME', 'ATLAS PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2f57c10da8a3913b95d31fe35e992f9167685539bfe07b58942a02a8e3ac02f1' OR (alias_name='土甲 PRIME' AND target_name='ATLAS PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6ef5be8da389ff7f36d6193d086d4242eaf842e5ca76b2b31a83bc603fd2d0ec', '土豆', 'Orokin Catalyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6ef5be8da389ff7f36d6193d086d4242eaf842e5ca76b2b31a83bc603fd2d0ec' OR (alias_name='土豆' AND target_name='Orokin Catalyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f2d099711d659418ac6dbbe431be7defdcfb655f237780b264565a1cd531cbba', '圣剑', 'Excalibur', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f2d099711d659418ac6dbbe431be7defdcfb655f237780b264565a1cd531cbba' OR (alias_name='圣剑' AND target_name='Excalibur'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:29fd44f66346b7aa7fa9ba0b6f4fd3b7d6544cee024794b78511a86f9c78a1b3', '圣剑 p', 'Excalibur Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:29fd44f66346b7aa7fa9ba0b6f4fd3b7d6544cee024794b78511a86f9c78a1b3' OR (alias_name='圣剑 p' AND target_name='Excalibur Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:25a1e513e6615f6813795610d56b47fc8300214dc3453278cc37cbf58c7b427c', '圣拳武僧', 'Baruuk', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:25a1e513e6615f6813795610d56b47fc8300214dc3453278cc37cbf58c7b427c' OR (alias_name='圣拳武僧' AND target_name='Baruuk'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:22a84b5ee2b136b05f310d37420017ffabb8bfa61812fdb02d3aab09a035a836', '圣枪弥撒', 'Mesa', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:22a84b5ee2b136b05f310d37420017ffabb8bfa61812fdb02d3aab09a035a836' OR (alias_name='圣枪弥撒' AND target_name='Mesa'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:305a8e9c2837a29a507672d03cbb9ce08174973849bb3c6dbef3d1c39e4f2f78', '圣殿', 'Arca Plasmor', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:305a8e9c2837a29a507672d03cbb9ce08174973849bb3c6dbef3d1c39e4f2f78' OR (alias_name='圣殿' AND target_name='Arca Plasmor'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ee15fb4d3bc81c67ae962c2d5dc9b8cd1351ee00a7353512374877fc24ae6765', '夜店', 'Revenant', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ee15fb4d3bc81c67ae962c2d5dc9b8cd1351ee00a7353512374877fc24ae6765' OR (alias_name='夜店' AND target_name='Revenant'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:cb448339365f036c14a9dc067891d9eb075d2cc0b35b31b25876c828b5192608', '夜店 P', 'Revenant Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:cb448339365f036c14a9dc067891d9eb075d2cc0b35b31b25876c828b5192608' OR (alias_name='夜店 P' AND target_name='Revenant Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6dce4bab69917437a617c3fdfe29fc28ad084f6370034126d64c27b3f633b3d6', '夜灵', 'Revenant', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6dce4bab69917437a617c3fdfe29fc28ad084f6370034126d64c27b3f633b3d6' OR (alias_name='夜灵' AND target_name='Revenant'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ac8363caa25fa993ef1b48930f6faffc909933b6c88f8b4d6fef6dfeb6968d90', '夜灵夜店 PRIME', 'REVENANT PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ac8363caa25fa993ef1b48930f6faffc909933b6c88f8b4d6fef6dfeb6968d90' OR (alias_name='夜灵夜店 PRIME' AND target_name='REVENANT PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4442df71b25e98377c15b9edda04ccd5c10e28aaf00d16c394ecf6f2097d33c6', '夜灵甲', 'Revenant', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4442df71b25e98377c15b9edda04ccd5c10e28aaf00d16c394ecf6f2097d33c6' OR (alias_name='夜灵甲' AND target_name='Revenant'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c4485d1da107083b2325b26f4bc59750c8c87645dc121551c782e6a987239bc5', '夜灵甲 P', 'Revenant Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c4485d1da107083b2325b26f4bc59750c8c87645dc121551c782e6a987239bc5' OR (alias_name='夜灵甲 P' AND target_name='Revenant Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:bfff4dfe6491669366750055d188e0fa910dad11b74c583dfd54c433c73e3f5a', '大neinei', 'Trinity', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:bfff4dfe6491669366750055d188e0fa910dad11b74c583dfd54c433c73e3f5a' OR (alias_name='大neinei' AND target_name='Trinity'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:515e019632e67c86fe4511cd3fad3bba8f428f18e9197445e7fce002e6b9dad8', '大傻', 'Teralyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:515e019632e67c86fe4511cd3fad3bba8f428f18e9197445e7fce002e6b9dad8' OR (alias_name='大傻' AND target_name='Teralyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8a0aab5e5782d3d6243d71211926dabd94e76a692588dce3679c52a3623a35ae', '女枪', 'Mesa', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8a0aab5e5782d3d6243d71211926dabd94e76a692588dce3679c52a3623a35ae' OR (alias_name='女枪' AND target_name='Mesa'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2d29b3dbdb99387225361158ceb1649924e1c33ed24131398bf053276e8b823d', '女枪 P', 'Mesa Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2d29b3dbdb99387225361158ceb1649924e1c33ed24131398bf053276e8b823d' OR (alias_name='女枪 P' AND target_name='Mesa Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e9f1dbfb1c79cd8e8719adc08629f4310abc781814262ff558cee7aa12601269', '女枪🔫 PRIME', 'MESA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e9f1dbfb1c79cd8e8719adc08629f4310abc781814262ff558cee7aa12601269' OR (alias_name='女枪🔫 PRIME' AND target_name='MESA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:64f29842376d40c187972c80872957e8b578030c92cbd513bb0e91fd2dfc6341', '女汉 P', 'Valkyr Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:64f29842376d40c187972c80872957e8b578030c92cbd513bb0e91fd2dfc6341' OR (alias_name='女汉 P' AND target_name='Valkyr Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8e1c178bf1109033f0128015b991fed71b11b448484a1f677dc9bfec9a608891', '女汉子', 'Valkyr', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8e1c178bf1109033f0128015b991fed71b11b448484a1f677dc9bfec9a608891' OR (alias_name='女汉子' AND target_name='Valkyr'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:41294008b44b94d0b74531bd2b9ff138bb16f3b2f54d8576c8e68e8b1fbe63f8', '女汉子 PRIME', 'VALKYR PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:41294008b44b94d0b74531bd2b9ff138bb16f3b2f54d8576c8e68e8b1fbe63f8' OR (alias_name='女汉子 PRIME' AND target_name='VALKYR PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:694a199cac3121a7206dfe2eeff6375cbde49f67af300e6663df18b3a50ea88c', '奶', 'Trinity', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:694a199cac3121a7206dfe2eeff6375cbde49f67af300e6663df18b3a50ea88c' OR (alias_name='奶' AND target_name='Trinity'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1416c01c20d73fe6675a816fbf571aec877684eea980d2ab784ff449f3cdea10', '奶 P', 'Trinity Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1416c01c20d73fe6675a816fbf571aec877684eea980d2ab784ff449f3cdea10' OR (alias_name='奶 P' AND target_name='Trinity Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0e93a2d59cbda37f09d26aa63c9d8f08e04edf60909db0c645059d5aa0076681', '奶妈', 'Trinity', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0e93a2d59cbda37f09d26aa63c9d8f08e04edf60909db0c645059d5aa0076681' OR (alias_name='奶妈' AND target_name='Trinity'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:675b66b33016e1a942147f4fcf938abf4285e179bd5d8719747a9a24ddde418a', '奶妈 P', 'Trinity Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:675b66b33016e1a942147f4fcf938abf4285e179bd5d8719747a9a24ddde418a' OR (alias_name='奶妈 P' AND target_name='Trinity Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:964adc00f0e1770982f92a757f760c1379f9a94027f488cb2a0d987f7b5dec2d', '奶爸', 'Oberon', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:964adc00f0e1770982f92a757f760c1379f9a94027f488cb2a0d987f7b5dec2d' OR (alias_name='奶爸' AND target_name='Oberon'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e89699bb4b9cab60ed7f57a65b37c69373902db6a5aab9ad6b105ba5b540abcb', '奶爸 p', 'Oberon Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e89699bb4b9cab60ed7f57a65b37c69373902db6a5aab9ad6b105ba5b540abcb' OR (alias_name='奶爸 p' AND target_name='Oberon Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:05d15e4a1f6e9801114a1fe6298c6666c5b3a547b2d84ef2a47b07a97a634463', '奶🍼👵🐄👨‍🍼爸 PRIME', 'OBERON PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:05d15e4a1f6e9801114a1fe6298c6666c5b3a547b2d84ef2a47b07a97a634463' OR (alias_name='奶🍼👵🐄👨‍🍼爸 PRIME' AND target_name='OBERON PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:25b6e01dc1b4fd8fcc3273d723ad0e700ccffd07a0d91bc6b4c0c5dd52ee784b', '奶🍼👵🐄👩‍🍼妈 PRIME', 'TRINITY PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:25b6e01dc1b4fd8fcc3273d723ad0e700ccffd07a0d91bc6b4c0c5dd52ee784b' OR (alias_name='奶🍼👵🐄👩‍🍼妈 PRIME' AND target_name='TRINITY PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:efa136607d31396da424f1f88aac77e45eb78165fd2c22e3cdac6d51e3c81e90', '奸商', 'Baro Ki''Teer', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:efa136607d31396da424f1f88aac77e45eb78165fd2c22e3cdac6d51e3c81e90' OR (alias_name='奸商' AND target_name='Baro Ki''Teer'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:09fe91f3a10346d7fdd0baf6ab32a6e4597a2ca3db9b198f54e9acbdfb866bd8', '姐妹', 'Sister', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:09fe91f3a10346d7fdd0baf6ab32a6e4597a2ca3db9b198f54e9acbdfb866bd8' OR (alias_name='姐妹' AND target_name='Sister'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a6c9d7dea700d55d48b8f49ace750209f321b3e285426134265b3f59d660e73e', '孔梅', 'Koumei', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a6c9d7dea700d55d48b8f49ace750209f321b3e285426134265b3f59d660e73e' OR (alias_name='孔梅' AND target_name='Koumei'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:08a248b047181afb7fe8ad853dd4e5386ced859beef6566d89e001910f8f9504', '孔梅 P', 'Koumei Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:08a248b047181afb7fe8ad853dd4e5386ced859beef6566d89e001910f8f9504' OR (alias_name='孔梅 P' AND target_name='Koumei Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6dab087bc03a12dde7255e498dfc3a101ae59bc2fc83c6c5396af8338c832895', '孙悟空 PRIME', 'WUKONG PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6dab087bc03a12dde7255e498dfc3a101ae59bc2fc83c6c5396af8338c832895' OR (alias_name='孙悟空 PRIME' AND target_name='WUKONG PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5d78619c7491da9708b14a1a2ed5b67b51a0bcd8e02b413a48845374d30d411f', '宝石妹', 'Citrine', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5d78619c7491da9708b14a1a2ed5b67b51a0bcd8e02b413a48845374d30d411f' OR (alias_name='宝石妹' AND target_name='Citrine'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a683defd17080358d72588636476b0767bb9a38308e9c6008686c996a8539c1f', '宝石妹 P', 'Citrine Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a683defd17080358d72588636476b0767bb9a38308e9c6008686c996a8539c1f' OR (alias_name='宝石妹 P' AND target_name='Citrine Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:9110ace7389c8c225c97e250304e1369b021d6a386c9b57bc925099d9a69d6dc', '宝石甲', 'Citrine', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:9110ace7389c8c225c97e250304e1369b021d6a386c9b57bc925099d9a69d6dc' OR (alias_name='宝石甲' AND target_name='Citrine'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f8d5c1fdd8b94a7a37f6f5998267409cf8045ccf2e0d4b34921ff4225aedce35', '小丑', 'Mirage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f8d5c1fdd8b94a7a37f6f5998267409cf8045ccf2e0d4b34921ff4225aedce35' OR (alias_name='小丑' AND target_name='Mirage'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6aa65cca54695ce23c44d483e5d995be7718676f3f3978d465b3f3b1c08368af', '小丑 P', 'Mirage Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6aa65cca54695ce23c44d483e5d995be7718676f3f3978d465b3f3b1c08368af' OR (alias_name='小丑 P' AND target_name='Mirage Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a53c9be97611ef30444e900887deacaa678aaa89723a30fade817041aebd58b0', '小丑🃠🃏🤡🃏 PRIME', 'MIRAGE PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a53c9be97611ef30444e900887deacaa678aaa89723a30fade817041aebd58b0' OR (alias_name='小丑🃠🃏🤡🃏 PRIME' AND target_name='MIRAGE PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0eb6ec0e0f409d5f610a7df3c63850cd9c51821fad8f2a36aaf426aaba118167', '小明', 'Limbo', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0eb6ec0e0f409d5f610a7df3c63850cd9c51821fad8f2a36aaf426aaba118167' OR (alias_name='小明' AND target_name='Limbo'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:da19d853caa34fbb794651b2944c037339635c1b2f1891cf697203bff8caff88', '小明 P', 'Limbo Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:da19d853caa34fbb794651b2944c037339635c1b2f1891cf697203bff8caff88' OR (alias_name='小明 P' AND target_name='Limbo Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ad84b602af0757894e11f6ffe04f94c524ea1fb123434696be419b9968a69b32', '小李明博 PRIME', 'LIMBO PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ad84b602af0757894e11f6ffe04f94c524ea1fb123434696be419b9968a69b32' OR (alias_name='小李明博 PRIME' AND target_name='LIMBO PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3d8599085a952cc45f59959d2eaf576f2112b2c143fb3eb21dcfa4b29512772c', '工程', 'Vauban', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3d8599085a952cc45f59959d2eaf576f2112b2c143fb3eb21dcfa4b29512772c' OR (alias_name='工程' AND target_name='Vauban'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a7e3c553d589d147c5fc57a7c45ce96c9a04bac82157b30f5b7a2ec53f17a23f', '工程 P', 'Vauban Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a7e3c553d589d147c5fc57a7c45ce96c9a04bac82157b30f5b7a2ec53f17a23f' OR (alias_name='工程 P' AND target_name='Vauban Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4b0540571b533cab61ddcc3a46586e3f3e95392c77b811de8720e74b8263b43b', '工程 PRIME', 'VAUBAN PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4b0540571b533cab61ddcc3a46586e3f3e95392c77b811de8720e74b8263b43b' OR (alias_name='工程 PRIME' AND target_name='VAUBAN PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:85b1b691a6c1eec17bf54b72b8ce263d75b99d293808f31ad156d63f30c3b9eb', '巨力使', 'Gantulyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:85b1b691a6c1eec17bf54b72b8ce263d75b99d293808f31ad156d63f30c3b9eb' OR (alias_name='巨力使' AND target_name='Gantulyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c2cd8fcffcac16f06faa91712718baf86fb41aea311468a30e358b4d3a2bfa1a', '弓妹', 'Ivara', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c2cd8fcffcac16f06faa91712718baf86fb41aea311468a30e358b4d3a2bfa1a' OR (alias_name='弓妹' AND target_name='Ivara'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ba5c0f1c533835b6d00e455dcd70f1aad0f4d006325b0df5877dec8541f83d44', '弓妹 P', 'Ivara Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ba5c0f1c533835b6d00e455dcd70f1aad0f4d006325b0df5877dec8541f83d44' OR (alias_name='弓妹 P' AND target_name='Ivara Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:37a657a21fdfe4ad591190271a88bec0a1003a678b58fa838ad4180f8de1028d', '弓🏹️妹 PRIME', 'IVARA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:37a657a21fdfe4ad591190271a88bec0a1003a678b58fa838ad4180f8de1028d' OR (alias_name='弓🏹️妹 PRIME' AND target_name='IVARA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fba427e4e9e51851ab572afdb81745763475f415cbbae3eca0e311b159025b3d', '弱鸡', 'Loki', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fba427e4e9e51851ab572afdb81745763475f415cbbae3eca0e311b159025b3d' OR (alias_name='弱鸡' AND target_name='Loki'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:28219f09976acc6b3eae54fd77a4a1330531e18bca62e021d3fa5f43716c7d0e', '御魂主宰', 'Nekros', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:28219f09976acc6b3eae54fd77a4a1330531e18bca62e021d3fa5f43716c7d0e' OR (alias_name='御魂主宰' AND target_name='Nekros'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:43e3edd33f1779b3c54f8d70a411b5fd31b1e14e7cad2e27f3ecce02b96e86db', '恶魔甲', 'Uriel', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:43e3edd33f1779b3c54f8d70a411b5fd31b1e14e7cad2e27f3ecce02b96e86db' OR (alias_name='恶魔甲' AND target_name='Uriel'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c2442e1bfdcc6fa61bf1e722e302d17265fef124f3d1ee45626cef04d03e9081', '恶魔甲 p', 'Uriel Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c2442e1bfdcc6fa61bf1e722e302d17265fef124f3d1ee45626cef04d03e9081' OR (alias_name='恶魔甲 p' AND target_name='Uriel Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:381c95c55ce2117e5ba13388c5d1f3bed0ad17ede37e52baf53c1e5d684c5c58', '恸哭女妖', 'Banshee', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:381c95c55ce2117e5ba13388c5d1f3bed0ad17ede37e52baf53c1e5d684c5c58' OR (alias_name='恸哭女妖' AND target_name='Banshee'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:81e16a2bc349e3fc295898d964d49cb29ea2de24e0f8c18228787225bbd170a3', '悟空', 'Wukong', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:81e16a2bc349e3fc295898d964d49cb29ea2de24e0f8c18228787225bbd170a3' OR (alias_name='悟空' AND target_name='Wukong'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:055b85d84083b066ec44ba21a54dad32400d010d0db406b36fb34fc480a421d2', '悟空 P', 'Wukong Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:055b85d84083b066ec44ba21a54dad32400d010d0db406b36fb34fc480a421d2' OR (alias_name='悟空 P' AND target_name='Wukong Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8539bf54a9cb5de2ec20b4ed35e2aaee11a446ea94bf43f20e40841090cf424b', '惊涛骇浪', 'Hydroid', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8539bf54a9cb5de2ec20b4ed35e2aaee11a446ea94bf43f20e40841090cf424b' OR (alias_name='惊涛骇浪' AND target_name='Hydroid'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:298fa8885cbd52e6f52036c48780b55a6a95dfdd9c3c1bce3a4616b4195af57f', '感染甲', 'Nidus', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:298fa8885cbd52e6f52036c48780b55a6a95dfdd9c3c1bce3a4616b4195af57f' OR (alias_name='感染甲' AND target_name='Nidus'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:04b4f41d32d949aace1410b254523e142b6fb7a38ecd904988955e19827e719d', '扶他', 'Equinox', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:04b4f41d32d949aace1410b254523e142b6fb7a38ecd904988955e19827e719d' OR (alias_name='扶他' AND target_name='Equinox'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:afb4c94be32531cf469eed39c5aed1e653b3a44673d6cd0e9a463b064bf5e7bd', '扶她', 'Equinox', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:afb4c94be32531cf469eed39c5aed1e653b3a44673d6cd0e9a463b064bf5e7bd' OR (alias_name='扶她' AND target_name='Equinox'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:77615e76381f8fd9bc6b96cae512f3ba2c91d901218631ee6b0a6a5f1b45a5e5', '扶她 P', 'Equinox Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:77615e76381f8fd9bc6b96cae512f3ba2c91d901218631ee6b0a6a5f1b45a5e5' OR (alias_name='扶她 P' AND target_name='Equinox Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8e81fa62d575f65f1b1dab5cb4e55f3b2ce73596ac1f2992120b55c6a21d372f', '扶她 PRIME', 'EQUINOX PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8e81fa62d575f65f1b1dab5cb4e55f3b2ce73596ac1f2992120b55c6a21d372f' OR (alias_name='扶她 PRIME' AND target_name='EQUINOX PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:448ac17515f5192beb5141860483401e908b1af2db845dcdb38b129bbf8908bd', '摸', 'Nekros', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:448ac17515f5192beb5141860483401e908b1af2db845dcdb38b129bbf8908bd' OR (alias_name='摸' AND target_name='Nekros'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f97f99a32f2f3a63df9c7d0d1393291d9dfcc0524b9f629b5e3f599e667b6e04', '摸尸', 'Nekros', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f97f99a32f2f3a63df9c7d0d1393291d9dfcc0524b9f629b5e3f599e667b6e04' OR (alias_name='摸尸' AND target_name='Nekros'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:38e243e566b94aad91ed5397ec7acae2a96418f5b09d0a138aa95ae576b8538d', '摸尸 P', 'Nekros Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:38e243e566b94aad91ed5397ec7acae2a96418f5b09d0a138aa95ae576b8538d' OR (alias_name='摸尸 P' AND target_name='Nekros Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0acb76e0b4618f06fb863afc94330cc0c7c44f1b7031741ae4c693f01c39e71c', '摸尸 PRIME', 'NEKROS PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0acb76e0b4618f06fb863afc94330cc0c7c44f1b7031741ae4c693f01c39e71c' OR (alias_name='摸尸 PRIME' AND target_name='NEKROS PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:bee89ce7b29ae7fd11fb99e7c4187708cd202cf86fe91080843918ac449c8c52', '撼天', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:bee89ce7b29ae7fd11fb99e7c4187708cd202cf86fe91080843918ac449c8c52' OR (alias_name='撼天' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3ba8a3ffcd4289672ac79d513db485c750556c102fe2d1f4ee1d4830eee87849', '撼天刑者', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3ba8a3ffcd4289672ac79d513db485c750556c102fe2d1f4ee1d4830eee87849' OR (alias_name='撼天刑者' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c0f4cd1d64c78425a62e0e4a2e02c90e39f8e4053637288a9165fe91aab90e7a', '救赎者', 'Redeemer', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c0f4cd1d64c78425a62e0e4a2e02c90e39f8e4053637288a9165fe91aab90e7a' OR (alias_name='救赎者' AND target_name='Redeemer'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5289da220d1c0bc8ca5da25f013a5234757eca1e4dfb9f8db17d9981918c1b0a', '斯巴达', 'Styanax', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5289da220d1c0bc8ca5da25f013a5234757eca1e4dfb9f8db17d9981918c1b0a' OR (alias_name='斯巴达' AND target_name='Styanax'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5d1a0840204bc9ab5afadbc076c4c833a753de936722f7601aaa2ad283fde7d4', '斯巴达 P', 'Styanax Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5d1a0840204bc9ab5afadbc076c4c833a753de936722f7601aaa2ad283fde7d4' OR (alias_name='斯巴达 P' AND target_name='Styanax Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:edc0ad9c8db7274cdccdbf5989deb2cdf60448b0f39292e7b5a22db9dd7a1c82', '无头', 'Dagath', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:edc0ad9c8db7274cdccdbf5989deb2cdf60448b0f39292e7b5a22db9dd7a1c82' OR (alias_name='无头' AND target_name='Dagath'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0cb13da2985d515bdac2f7713e268f55ac1cba9bad767c7b3e50231b550f0d4e', '无头 P', 'Dagath Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0cb13da2985d515bdac2f7713e268f55ac1cba9bad767c7b3e50231b550f0d4e' OR (alias_name='无头 P' AND target_name='Dagath Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:79d3ce2660cc265ee1f4cdd7a81576259b21fda3e82f131be2241f71a1e9de05', '易爆反弹', 'Volatile Rebound', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:79d3ce2660cc265ee1f4cdd7a81576259b21fda3e82f131be2241f71a1e9de05' OR (alias_name='易爆反弹' AND target_name='Volatile Rebound'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:cfbdcf7d3379e6cf9445422449604b2655478770b45959148044a32c064cb1b2', '易爆速返', 'Volatile Quick Return', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:cfbdcf7d3379e6cf9445422449604b2655478770b45959148044a32c064cb1b2' OR (alias_name='易爆速返' AND target_name='Volatile Quick Return'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d53ae4c1cdb5cb76787e1e96b0e483cf74618c77f4dd1fde88f32dc7c0d3ea95', '杀妈刀', 'Paracesis', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d53ae4c1cdb5cb76787e1e96b0e483cf74618c77f4dd1fde88f32dc7c0d3ea95' OR (alias_name='杀妈刀' AND target_name='Paracesis'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b96baf628aae250164ee84313d49608d4ccb08e27ff98bdf64b406536b0c7bb5', '李明博', 'Limbo', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b96baf628aae250164ee84313d49608d4ccb08e27ff98bdf64b406536b0c7bb5' OR (alias_name='李明博' AND target_name='Limbo'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a923d03c5f5998f32b0edf43ae2ca6892f8f0fdf57cc0d656c7e84c05a3da796', '杰德', 'Jade', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a923d03c5f5998f32b0edf43ae2ca6892f8f0fdf57cc0d656c7e84c05a3da796' OR (alias_name='杰德' AND target_name='Jade'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:58101216ca400bef582145f12be4aeaa052a4224cf474f9bfb4a0c034409ece5', '杰德 P', 'Jade Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:58101216ca400bef582145f12be4aeaa052a4224cf474f9bfb4a0c034409ece5' OR (alias_name='杰德 P' AND target_name='Jade Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:9ecc208070ca2316c8cd78b1b5df5ea694dfe20e268edfee588eecdd22f5cdf6', '核桃', 'Relic', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:9ecc208070ca2316c8cd78b1b5df5ea694dfe20e268edfee588eecdd22f5cdf6' OR (alias_name='核桃' AND target_name='Relic'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1e0a289cd34aaf90cc19ac5d25b855ba4cf8389893d8c3f3561dd2409db89dc8', '歌姬', 'Octavia', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1e0a289cd34aaf90cc19ac5d25b855ba4cf8389893d8c3f3561dd2409db89dc8' OR (alias_name='歌姬' AND target_name='Octavia'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fdfc92a6fe6c012f1f1c17c12a12aae164da3bd6d9d555eaf00157d71a8e9f05', '武僧', 'Baruuk', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fdfc92a6fe6c012f1f1c17c12a12aae164da3bd6d9d555eaf00157d71a8e9f05' OR (alias_name='武僧' AND target_name='Baruuk'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4a1c7c5ba066d3a1376d1b83213e1d4b3040001b3f9612c8cedd6ce6d15e04cc', '武僧拳师 PRIME', 'BARUUK PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4a1c7c5ba066d3a1376d1b83213e1d4b3040001b3f9612c8cedd6ce6d15e04cc' OR (alias_name='武僧拳师 PRIME' AND target_name='BARUUK PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c7a435bb4f4c624142728f7f0b694089d12293a168984622a250562f1f9739ef', '武增', 'Baruuk', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c7a435bb4f4c624142728f7f0b694089d12293a168984622a250562f1f9739ef' OR (alias_name='武增' AND target_name='Baruuk'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f748e967e42e3c3505c0099fa5c7ce3c256bc9b32099df79e2e4a348a867f3fe', '武增', 'Baruuk Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f748e967e42e3c3505c0099fa5c7ce3c256bc9b32099df79e2e4a348a867f3fe' OR (alias_name='武增' AND target_name='Baruuk Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7f28fe937af655813c4d0d7622463b91535a37d9dd693344dbc8a248660214c2', '母牛', 'Hildryn', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7f28fe937af655813c4d0d7622463b91535a37d9dd693344dbc8a248660214c2' OR (alias_name='母牛' AND target_name='Hildryn'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:73714ee5fa7bcdfbf77f12647e61d1471deba50521cdeae02eece7c7851db1f5', '母牛 P', 'Hildryn Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:73714ee5fa7bcdfbf77f12647e61d1471deba50521cdeae02eece7c7851db1f5' OR (alias_name='母牛 P' AND target_name='Hildryn Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:eae47c04fd6301326fcf0d45866fe2429e033488ef8fadd3425a505f82ebad77', '毒妈', 'Saryn', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:eae47c04fd6301326fcf0d45866fe2429e033488ef8fadd3425a505f82ebad77' OR (alias_name='毒妈' AND target_name='Saryn'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ed283ee5adfe8f8cc525e56ffbffc45234dff731c611cf7209bc11130121fb19', '毒妈 P', 'Saryn Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ed283ee5adfe8f8cc525e56ffbffc45234dff731c611cf7209bc11130121fb19' OR (alias_name='毒妈 P' AND target_name='Saryn Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4fc625148314a7714a7c56d2d367aa7fd5c55f43a60b350660f32b29219c4dfb', '水力使', 'Hydrolyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4fc625148314a7714a7c56d2d367aa7fd5c55f43a60b350660f32b29219c4dfb' OR (alias_name='水力使' AND target_name='Hydrolyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f7b4ea5d8ec77d93039cb9c338901741e255a83be6c2b6dc387ce475faf5e28b', '水妹', 'Yareli', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f7b4ea5d8ec77d93039cb9c338901741e255a83be6c2b6dc387ce475faf5e28b' OR (alias_name='水妹' AND target_name='Yareli'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4d250cc1e0d086653200e5e0c3ee3e290d079873cef76433321081e59970cd54', '水妹 P', 'Yareli Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4d250cc1e0d086653200e5e0c3ee3e290d079873cef76433321081e59970cd54' OR (alias_name='水妹 P' AND target_name='Yareli Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0221fefd26054f335ec28069fee02e71a0da9f67c525ae44eaf55c88f1a38fa0', '水晶', 'Citrine', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0221fefd26054f335ec28069fee02e71a0da9f67c525ae44eaf55c88f1a38fa0' OR (alias_name='水晶' AND target_name='Citrine'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:26943687bb8b2a32e42bce5c651079015709b8974bb6645ae9922036ac5f80f3', '水晶 P', 'Citrine Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:26943687bb8b2a32e42bce5c651079015709b8974bb6645ae9922036ac5f80f3' OR (alias_name='水晶 P' AND target_name='Citrine Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:88e04a826a179046855bfc5cdb7574d89bcadfe2b1e8326624179fcad8cfa191', '水甲', 'Hydroid', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:88e04a826a179046855bfc5cdb7574d89bcadfe2b1e8326624179fcad8cfa191' OR (alias_name='水甲' AND target_name='Hydroid'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0218859d4812a4c651328a109423993d1f9744800346e88654086c28794e9229', '水男', 'Hydroid', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0218859d4812a4c651328a109423993d1f9744800346e88654086c28794e9229' OR (alias_name='水男' AND target_name='Hydroid'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6268d28e74744cf6bcf259a2cd63624e80f2b02e0c658ffa93376d145fde995e', '水男 P', 'Hydroid Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6268d28e74744cf6bcf259a2cd63624e80f2b02e0c658ffa93376d145fde995e' OR (alias_name='水男 P' AND target_name='Hydroid Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:add7732ca6ca5da2bec1311a0a4c492b83e4a6a0d6a0f51e3168ed74f9628618', '永恒烈焰', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:add7732ca6ca5da2bec1311a0a4c492b83e4a6a0d6a0f51e3168ed74f9628618' OR (alias_name='永恒烈焰' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:095202cf438958ab813bd237ab2fc0f923351ce637510e2d0555d9806bf94056', '沙⏳🏜️甲 PRIME', 'INAROS PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:095202cf438958ab813bd237ab2fc0f923351ce637510e2d0555d9806bf94056' OR (alias_name='沙⏳🏜️甲 PRIME' AND target_name='INAROS PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1c624cf37393de7bbf2e4810e68cad11d9e17f1bf1bf6b5346d41888021a54fe', '沙甲', 'Inaros', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1c624cf37393de7bbf2e4810e68cad11d9e17f1bf1bf6b5346d41888021a54fe' OR (alias_name='沙甲' AND target_name='Inaros'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:993e9b949e1207109e439ce19e04b1f54d4e74a28371e664ffe3699c0d5fb2be', '沙甲 P', 'Inaros Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:993e9b949e1207109e439ce19e04b1f54d4e74a28371e664ffe3699c0d5fb2be' OR (alias_name='沙甲 P' AND target_name='Inaros Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ca25daec5f0238d34a7960fd1c2f2218d6779dd060dce2e4817ee796b6876fed', '洛基', 'Loki', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ca25daec5f0238d34a7960fd1c2f2218d6779dd060dce2e4817ee796b6876fed' OR (alias_name='洛基' AND target_name='Loki'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:bb24385e4e40c00a7e16bd9709db2e8f31ab5c644410d4a8fbaad330b4a5da0a', '洛基 P', 'Loki Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:bb24385e4e40c00a7e16bd9709db2e8f31ab5c644410d4a8fbaad330b4a5da0a' OR (alias_name='洛基 P' AND target_name='Loki Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7a4f0fc82cd6c771945a1518afda1f70f76996542e2a31cee308ae761f3f7217', '洛弱基鸡🐔🐤🐓🐥🐣 PRIME', 'LOKI PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7a4f0fc82cd6c771945a1518afda1f70f76996542e2a31cee308ae761f3f7217' OR (alias_name='洛弱基鸡🐔🐤🐓🐥🐣 PRIME' AND target_name='LOKI PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:02a735ff208d921ecb7f40752cc44bfb467703cdb3d7d37135332fa516bb5700', '海王', 'Hydroid', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:02a735ff208d921ecb7f40752cc44bfb467703cdb3d7d37135332fa516bb5700' OR (alias_name='海王' AND target_name='Hydroid'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7c9f19762c91a6ac7e037ed55b593135fe5edd65a84db1eb6c1435f8ce7e861a', '滑板妹', 'Yareli', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7c9f19762c91a6ac7e037ed55b593135fe5edd65a84db1eb6c1435f8ce7e861a' OR (alias_name='滑板妹' AND target_name='Yareli'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2fd803c6dfb42921497d39848b22324eebac4e8aadb1e016beab6dc4840e7955', '滑板妹 P', 'Yareli Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2fd803c6dfb42921497d39848b22324eebac4e8aadb1e016beab6dc4840e7955' OR (alias_name='滑板妹 P' AND target_name='Yareli Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7073e52180791a7ff03016282fd9ce6206e3f42a358a8dbe6c8352836a36a562', '火女', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7073e52180791a7ff03016282fd9ce6206e3f42a358a8dbe6c8352836a36a562' OR (alias_name='火女' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5fc7f499d39d86ef9746685e561729813328ee9613f233c9129f306c8e50f101', '火妹', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5fc7f499d39d86ef9746685e561729813328ee9613f233c9129f306c8e50f101' OR (alias_name='火妹' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:625e2c536d9bb28c23e20b9c3f0c925530df23e5b8d299f07e8d567c584e290b', '火妹 P', 'Ember Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:625e2c536d9bb28c23e20b9c3f0c925530df23e5b8d299f07e8d567c584e290b' OR (alias_name='火妹 P' AND target_name='Ember Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:69f16c29ebd0973c76d52e27b2d4e6573f0d84adcb5f75d50054f976e4de19c9', '火甲', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:69f16c29ebd0973c76d52e27b2d4e6573f0d84adcb5f75d50054f976e4de19c9' OR (alias_name='火甲' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:72a009d84952ad0e2a648afcdd1fe2675a74a8a9206ee1bc95d78ed29221c88c', '火男', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:72a009d84952ad0e2a648afcdd1fe2675a74a8a9206ee1bc95d78ed29221c88c' OR (alias_name='火男' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5f5be036c92da660c614276f80f4b6782ee1d01da98c4fa75f952cdd8179e377', '火鸡', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5f5be036c92da660c614276f80f4b6782ee1d01da98c4fa75f952cdd8179e377' OR (alias_name='火鸡' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d1651a1350dd46bed886474b83f4092e7efe5926ea28718f08e032a3d7a730a2', '火鸡 P', 'Ember Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d1651a1350dd46bed886474b83f4092e7efe5926ea28718f08e032a3d7a730a2' OR (alias_name='火鸡 P' AND target_name='Ember Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:77952df917df613779bffec99f881844e580c2fc867f8d54449cbbbc573a390d', '火鸡🦃️🐔🐤🐓🐥🐣 PRIME', 'EMBER PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:77952df917df613779bffec99f881844e580c2fc867f8d54449cbbbc573a390d' OR (alias_name='火鸡🦃️🐔🐤🐓🐥🐣 PRIME' AND target_name='EMBER PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c35d94e3d89cd731059f7d6f9ec690eb25ef3e9a295d9c1282a8e7a580d1569f', '灰烬', 'Ash', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c35d94e3d89cd731059f7d6f9ec690eb25ef3e9a295d9c1282a8e7a580d1569f' OR (alias_name='灰烬' AND target_name='Ash'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a37730661fc11db9a2007892f3c2311ed93d2262d9f324578f8fcbcbd6f1f1e6', '炼金', 'Lavos', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a37730661fc11db9a2007892f3c2311ed93d2262d9f324578f8fcbcbd6f1f1e6' OR (alias_name='炼金' AND target_name='Lavos'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e7eee83a310e931ec9215f2dd49b0160a076402da412ec0aefbda8d93ab116b1', '炼金 P', 'Lavos Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e7eee83a310e931ec9215f2dd49b0160a076402da412ec0aefbda8d93ab116b1' OR (alias_name='炼金 P' AND target_name='Lavos Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:257c0efe111c0a01f690ae91b02a4629fc6277675fb00bcac5a0bccfb14df699', '牛', 'Rhino', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:257c0efe111c0a01f690ae91b02a4629fc6277675fb00bcac5a0bccfb14df699' OR (alias_name='牛' AND target_name='Rhino'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b9f4dcb630504e67b9c53b66fa9cbd27d816768879a78ffb49d76e1dce231bec', '牛 P', 'Rhino Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b9f4dcb630504e67b9c53b66fa9cbd27d816768879a78ffb49d76e1dce231bec' OR (alias_name='牛 P' AND target_name='Rhino Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4a66705fbfd430fbb14832e572a3a31861206e524d36299812da25a36bc2da30', '牛甲', 'Rhino', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4a66705fbfd430fbb14832e572a3a31861206e524d36299812da25a36bc2da30' OR (alias_name='牛甲' AND target_name='Rhino'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:63924561cb0afcce6a773f9c31356642172e4cd5586e907934f9ea5ad2e77e2b', '牛甲 P', 'Rhino Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:63924561cb0afcce6a773f9c31356642172e4cd5586e907934f9ea5ad2e77e2b' OR (alias_name='牛甲 P' AND target_name='Rhino Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:28584285500b440c364e509c4c1c27abc5f5269092c1c9199f553575324e17e5', '牢九', 'Cyte-09', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:28584285500b440c364e509c4c1c27abc5f5269092c1c9199f553575324e17e5' OR (alias_name='牢九' AND target_name='Cyte-09'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:53ac7d30ced8d03fe36e4e8e6ff4731dbeb3cd75b943c308f6bc103ca08f8ac0', '牢九 P', 'Cyte-09 Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:53ac7d30ced8d03fe36e4e8e6ff4731dbeb3cd75b943c308f6bc103ca08f8ac0' OR (alias_name='牢九 P' AND target_name='Cyte-09 Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b10cbf1029fe535d182eccd9b586f6fd73aa10908d9240955ebd352511a7a775', '犀牛', 'Rhino', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b10cbf1029fe535d182eccd9b586f6fd73aa10908d9240955ebd352511a7a775' OR (alias_name='犀牛' AND target_name='Rhino'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:76b0c07fdda5445122ebc3167f217994956e34ffe3895e19328634639e5c8147', '犀牛🐂🐄🐃🦏🐌🐮 PRIME', 'RHINO PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:76b0c07fdda5445122ebc3167f217994956e34ffe3895e19328634639e5c8147' OR (alias_name='犀牛🐂🐄🐃🦏🐌🐮 PRIME' AND target_name='RHINO PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3b20b80f046def557ab771e94a3a107adfd0fe5221d996fb97eb3ee4be13eaf2', '狼妹', 'Voruna', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3b20b80f046def557ab771e94a3a107adfd0fe5221d996fb97eb3ee4be13eaf2' OR (alias_name='狼妹' AND target_name='Voruna'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:aa755a00888098878c4df2105b75c0edd8587751b796b6f113c96b799cd3ceda', '狼妹 P', 'Voruna Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:aa755a00888098878c4df2105b75c0edd8587751b796b6f113c96b799cd3ceda' OR (alias_name='狼妹 P' AND target_name='Voruna Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:bc68598913ff95c12c1d203f6c8191a2e6487d6a9340f64bda53ebe2885a4357', '狼甲', 'Voruna', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:bc68598913ff95c12c1d203f6c8191a2e6487d6a9340f64bda53ebe2885a4357' OR (alias_name='狼甲' AND target_name='Voruna'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:aa18d4096478832e2f71f1ea85c8139ff2e2e67e00768c7c74dc06185e0a046f', '狼甲 P', 'Voruna Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:aa18d4096478832e2f71f1ea85c8139ff2e2e67e00768c7c74dc06185e0a046f' OR (alias_name='狼甲 P' AND target_name='Voruna Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:68d85093e4a40a2a70804455257d8ac35f51564df7fa2f86e7e5a2daf630ab19', '猫甲', 'Khora', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:68d85093e4a40a2a70804455257d8ac35f51564df7fa2f86e7e5a2daf630ab19' OR (alias_name='猫甲' AND target_name='Khora'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ff31615eb781f9a0427630b611aea2242bffdfd9cc9ab9f6c44079320f7eb524', '猫甲 P', 'Khora Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ff31615eb781f9a0427630b611aea2242bffdfd9cc9ab9f6c44079320f7eb524' OR (alias_name='猫甲 P' AND target_name='Khora Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b37f82813678b4c5853256028c8979d7e188ccc907fad3d7cf2a2df271f4ec15', '猴', 'Wukong', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b37f82813678b4c5853256028c8979d7e188ccc907fad3d7cf2a2df271f4ec15' OR (alias_name='猴' AND target_name='Wukong'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6bd8c11b2c9bbf523f17b0d0a01c68cbcf08eafbc951e11d3d6810f558a59e90', '猴子', 'Wukong', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6bd8c11b2c9bbf523f17b0d0a01c68cbcf08eafbc951e11d3d6810f558a59e90' OR (alias_name='猴子' AND target_name='Wukong'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0cee3b8a07bdcc466febaac06ae1ecdb7f9c36e5735446e2b33f599e6ec7e4b4', '猴子 P', 'Wukong Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0cee3b8a07bdcc466febaac06ae1ecdb7f9c36e5735446e2b33f599e6ec7e4b4' OR (alias_name='猴子 P' AND target_name='Wukong Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d6a83e52f7364c8944b247e5f77d31d7cfeac4f4e259f8e68bb4212a595a8555', '玄骸', 'Kuva Lich', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d6a83e52f7364c8944b247e5f77d31d7cfeac4f4e259f8e68bb4212a595a8555' OR (alias_name='玄骸' AND target_name='Kuva Lich'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f72461cb5592b9e8f581ac105a7e0636d7def88de7c1920e63d2c2037d9f7fe1', '玻琉璃 PRIME', 'GARA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f72461cb5592b9e8f581ac105a7e0636d7def88de7c1920e63d2c2037d9f7fe1' OR (alias_name='玻琉璃 PRIME' AND target_name='GARA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fb5bc6d778a1bf298f74f4ac0b40f7de0e7d1e41332e3a81dc99e98223f90bde', '玻璃', 'Gara', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fb5bc6d778a1bf298f74f4ac0b40f7de0e7d1e41332e3a81dc99e98223f90bde' OR (alias_name='玻璃' AND target_name='Gara'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e68982bafff8bbb44557c8a5be170d0653905f675ff03d99d9259a5df088e52a', '玻璃 P', 'Gara Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e68982bafff8bbb44557c8a5be170d0653905f675ff03d99d9259a5df088e52a' OR (alias_name='玻璃 P' AND target_name='Gara Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8f2e35129c771048b1c810b912e11794ff0fc3123e0d0abee051f256f35b48bb', '玻璃喷', '碎裂者', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8f2e35129c771048b1c810b912e11794ff0fc3123e0d0abee051f256f35b48bb' OR (alias_name='玻璃喷' AND target_name='碎裂者'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:200612b3e20e475c1b9a14ee00272cb93fce34eb21ff317e9c959b0ac98b4b69', '玻璃甲', 'Gara', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:200612b3e20e475c1b9a14ee00272cb93fce34eb21ff317e9c959b0ac98b4b69' OR (alias_name='玻璃甲' AND target_name='Gara'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e85955e93cbbd8f330515ee4a6a2c7ef53eed543bb5d15646b6d9516d60f32f1', '琉璃仕女', 'Gara', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e85955e93cbbd8f330515ee4a6a2c7ef53eed543bb5d15646b6d9516d60f32f1' OR (alias_name='琉璃仕女' AND target_name='Gara'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:cb41617bd2fe4a2454a5313dcf9aa45042389ed476ebf8b183c7bb66c9474fc6', '瓦喵', 'Valkyr', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:cb41617bd2fe4a2454a5313dcf9aa45042389ed476ebf8b183c7bb66c9474fc6' OR (alias_name='瓦喵' AND target_name='Valkyr'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5d879548ce513b3a6ef887d1ac2fe7ea08fe1fb921413014eb57e88f60ea1185', '瓦喵 P', 'Valkyr Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5d879548ce513b3a6ef887d1ac2fe7ea08fe1fb921413014eb57e88f60ea1185' OR (alias_name='瓦喵 P' AND target_name='Valkyr Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5712e0e0e3dd15f8182d9edd8f0193b25a04cf4fa54b760f27ab4f2e314e84d2', '瓦姐', 'Valkyr', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5712e0e0e3dd15f8182d9edd8f0193b25a04cf4fa54b760f27ab4f2e314e84d2' OR (alias_name='瓦姐' AND target_name='Valkyr'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:88d87953c7bf2057a1d4e670652b518c7389360fc88d2d4bad8e18884d24480d', '瓦尔基里喵 PRIME', 'VALKYR PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:88d87953c7bf2057a1d4e670652b518c7389360fc88d2d4bad8e18884d24480d' OR (alias_name='瓦尔基里喵 PRIME' AND target_name='VALKYR PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f941d5b7628ba41d73e309adbd2c886deaefc5316025cf91bcab43cada24df85', '电⚡️男队 PRIME', 'VOLT PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f941d5b7628ba41d73e309adbd2c886deaefc5316025cf91bcab43cada24df85' OR (alias_name='电⚡️男队 PRIME' AND target_name='VOLT PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:91ac1e0030a287f255e67def1c329a3806af1a2d251a09f3a3416df0f740890b', '电妹', 'Gyre', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:91ac1e0030a287f255e67def1c329a3806af1a2d251a09f3a3416df0f740890b' OR (alias_name='电妹' AND target_name='Gyre'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:64de2848c86151b929c9395a24e6daf59f0d014a7dc67e1dc7fce1233aff5785', '电妹 P', 'Gyre Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:64de2848c86151b929c9395a24e6daf59f0d014a7dc67e1dc7fce1233aff5785' OR (alias_name='电妹 P' AND target_name='Gyre Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:78e18c5a815b065d11a81af86d389e3696d72ce36771eab0c491447543dbdb99', '电浆炮', 'Arca Plasmor', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:78e18c5a815b065d11a81af86d389e3696d72ce36771eab0c491447543dbdb99' OR (alias_name='电浆炮' AND target_name='Arca Plasmor'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:85980f5dbe4ff9502803b203addc5181e2b747a8e9dbaac1c5a1c25770215236', '电男', 'Volt', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:85980f5dbe4ff9502803b203addc5181e2b747a8e9dbaac1c5a1c25770215236' OR (alias_name='电男' AND target_name='Volt'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:08638e5c17c4664d203d16b752d3edd93732bcd236bb86b6c6117277aef49949', '电男 P', 'Volt Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:08638e5c17c4664d203d16b752d3edd93732bcd236bb86b6c6117277aef49949' OR (alias_name='电男 P' AND target_name='Volt Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7d03574e6f234d8436c13787792f1094c51fc99100720e9adbf82bac8dbfc327', '电队', 'Volt', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7d03574e6f234d8436c13787792f1094c51fc99100720e9adbf82bac8dbfc327' OR (alias_name='电队' AND target_name='Volt'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f62bfe6cb34cb2a4cbfa6ca879a101613abe055ecacb45a1d95d40daef2a8cf5', '白金', 'Platinum', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f62bfe6cb34cb2a4cbfa6ca879a101613abe055ecacb45a1d95d40daef2a8cf5' OR (alias_name='白金' AND target_name='Platinum'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f5e9a6dde1f41e4cc28ca658a3046ee0fd63aefdbaaded4034ddd2a8db3d97c3', '矛兵', 'Styanax', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f5e9a6dde1f41e4cc28ca658a3046ee0fd63aefdbaaded4034ddd2a8db3d97c3' OR (alias_name='矛兵' AND target_name='Styanax'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e50647ff978e329703af2dbe82fff2a399a831e63861b08ab704a92c07462da5', '石头', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e50647ff978e329703af2dbe82fff2a399a831e63861b08ab704a92c07462da5' OR (alias_name='石头' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:42fa0297323dd8083baf09cf926168777891f25bdecadc64b60631e7080d6bff', '石甲', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:42fa0297323dd8083baf09cf926168777891f25bdecadc64b60631e7080d6bff' OR (alias_name='石甲' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c8a5f9a10c6ea8f5ca7767660623941a56154fff405e33f80e0633973b05bfdb', '石甲 P', 'Atlas Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c8a5f9a10c6ea8f5ca7767660623941a56154fff405e33f80e0633973b05bfdb' OR (alias_name='石甲 P' AND target_name='Atlas Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:379adaf74d156d1c6c480c1b754e4e5ef699532f673d99883d705585d65ef606', '石男', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:379adaf74d156d1c6c480c1b754e4e5ef699532f673d99883d705585d65ef606' OR (alias_name='石男' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b36a9a9917466b8be5ff77b7b96109aca9d6e04902d3a117693ebc0dac708377', '磁力', 'Mag', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b36a9a9917466b8be5ff77b7b96109aca9d6e04902d3a117693ebc0dac708377' OR (alias_name='磁力' AND target_name='Mag'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0fa783b99f3afdcdabec69e6f6d20b3bd445be46f3ada625bbb23972e07be191', '磁妹', 'Mag', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0fa783b99f3afdcdabec69e6f6d20b3bd445be46f3ada625bbb23972e07be191' OR (alias_name='磁妹' AND target_name='Mag'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a128e59719d3423de3f3949cceec86d7523277fe8de0c376ec0c4a09a2cf7de0', '磁妹 P', 'Mag Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a128e59719d3423de3f3949cceec86d7523277fe8de0c376ec0c4a09a2cf7de0' OR (alias_name='磁妹 P' AND target_name='Mag Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1056673ac31fb8b6997eec5f98686ba1cb92cec45614bb32d44b39a4fe0cc841', '磁🧲妹 PRIME', 'MAG PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1056673ac31fb8b6997eec5f98686ba1cb92cec45614bb32d44b39a4fe0cc841' OR (alias_name='磁🧲妹 PRIME' AND target_name='MAG PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:26ef076641b6b7df1c56dab568531a7f75105f402e8cad47abd32d6874849cc0', '神弓游侠', 'Ivara', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:26ef076641b6b7df1c56dab568531a7f75105f402e8cad47abd32d6874849cc0' OR (alias_name='神弓游侠' AND target_name='Ivara'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:91c19169f693620928e8ff792548270ed6578bb560acaf57a47970986f6382e0', '福马', 'Forma', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:91c19169f693620928e8ff792548270ed6578bb560acaf57a47970986f6382e0' OR (alias_name='福马' AND target_name='Forma'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f3b9fdc774b58cba18ed6ed3f9fc17f9ae693b44b0486da34e08241278f7dfa4', '紫卡', 'Riven Mod', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f3b9fdc774b58cba18ed6ed3f9fc17f9ae693b44b0486da34e08241278f7dfa4' OR (alias_name='紫卡' AND target_name='Riven Mod'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e0afb990def18374d7b30e273e8141458d72353843f426cd770d315216281c03', '翡翠', 'Jade', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e0afb990def18374d7b30e273e8141458d72353843f426cd770d315216281c03' OR (alias_name='翡翠' AND target_name='Jade'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7e7738fb73d140e081812edd8f7b31e2bd4b4eda82fb89e45c570b11cb6247bc', '翡翠 P', 'Jade Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7e7738fb73d140e081812edd8f7b31e2bd4b4eda82fb89e45c570b11cb6247bc' OR (alias_name='翡翠 P' AND target_name='Jade Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a57b0b14aae11df895301c738ce475bcb54a97c228fa43e770dd7567dd7fe8b4', '肥宅', 'Grendel', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a57b0b14aae11df895301c738ce475bcb54a97c228fa43e770dd7567dd7fe8b4' OR (alias_name='肥宅' AND target_name='Grendel'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3e8faf6f2ca38176f329fc2eb57ef7f0bfdf7d044386cdf355ceac51c9eda463', '肥宅 P', 'Grendel Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3e8faf6f2ca38176f329fc2eb57ef7f0bfdf7d044386cdf355ceac51c9eda463' OR (alias_name='肥宅 P' AND target_name='Grendel Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:150825e36f4be2baf17567916ace9db5611e0673399428c211e2c6301633aa01', '胖哥', 'Grendel', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:150825e36f4be2baf17567916ace9db5611e0673399428c211e2c6301633aa01' OR (alias_name='胖哥' AND target_name='Grendel'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:902b3d1a6e9dd7dbed52fee1b043c526f40d30a2922080877612ef7057c20f77', '脑淤血', 'Nyx', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:902b3d1a6e9dd7dbed52fee1b043c526f40d30a2922080877612ef7057c20f77' OR (alias_name='脑淤血' AND target_name='Nyx'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:eddbe4c370caac2ddc6a74a6620239bc2a8c271e44c49d82b08320be3fc76571', '脑溢血', 'Nyx', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:eddbe4c370caac2ddc6a74a6620239bc2a8c271e44c49d82b08320be3fc76571' OR (alias_name='脑溢血' AND target_name='Nyx'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:22b47a270d728d9bc7b81b2a296d4863efa5f59e63bf81b49bbb6dd20ac4dca9', '脑溢血 PRIME', 'NYX PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:22b47a270d728d9bc7b81b2a296d4863efa5f59e63bf81b49bbb6dd20ac4dca9' OR (alias_name='脑溢血 PRIME' AND target_name='NYX PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:79152c248e0dcb9e2618df214a23e463c2b5c138b63afe65f166f0e48baf4eb5', '脑溢血 p', 'Nyx Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:79152c248e0dcb9e2618df214a23e463c2b5c138b63afe65f166f0e48baf4eb5' OR (alias_name='脑溢血 p' AND target_name='Nyx Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:9205863e81e739b255f93fd9ba27b6669195ec2c067dffde273fd0519d557706', '花女', 'Wisp', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:9205863e81e739b255f93fd9ba27b6669195ec2c067dffde273fd0519d557706' OR (alias_name='花女' AND target_name='Wisp'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:49ad5003ae741b942e26d6d0c9211486c3d87364ce88577fccd1190646a5aa04', '花妈', 'Wisp', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:49ad5003ae741b942e26d6d0c9211486c3d87364ce88577fccd1190646a5aa04' OR (alias_name='花妈' AND target_name='Wisp'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f07bd7d69106de7b95d6e1f7e9874331170a880cec1d7ac6bc4da807189c6e7f', '花甲', 'Wisp', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f07bd7d69106de7b95d6e1f7e9874331170a880cec1d7ac6bc4da807189c6e7f' OR (alias_name='花甲' AND target_name='Wisp'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fc3e7b58e7094510ed52b77b76ea265541d138472aab88703b2b6b9fa6705c81', '花甲 P', 'Wisp Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fc3e7b58e7094510ed52b77b76ea265541d138472aab88703b2b6b9fa6705c81' OR (alias_name='花甲 P' AND target_name='Wisp Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c6600dbd8c2fc08a9aefb2741ca2431285d2b226ea4cd22e6d5e91b0620d59d4', '茶妹', 'Protea', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c6600dbd8c2fc08a9aefb2741ca2431285d2b226ea4cd22e6d5e91b0620d59d4' OR (alias_name='茶妹' AND target_name='Protea'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:58d77712211acedbd89896d1c9ded9db477a45ffb39f194fb5cf6516077ce83a', '茶妹 P', 'Protea Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:58d77712211acedbd89896d1c9ded9db477a45ffb39f194fb5cf6516077ce83a' OR (alias_name='茶妹 P' AND target_name='Protea Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2e0349dffed060ebbfd276bc29a408681e72e3c01dded01513d6eb730936822e', '菊花', '阿耶檀识 Anasa 塑像', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2e0349dffed060ebbfd276bc29a408681e72e3c01dded01513d6eb730936822e' OR (alias_name='菊花' AND target_name='阿耶檀识 Anasa 塑像'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:33b4206d47387632909346da1cb819820f239cce5b58635bc0f0f9e6838b3253', '蓝星', 'Ayatan Cyan Star', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:33b4206d47387632909346da1cb819820f239cce5b58635bc0f0f9e6838b3253' OR (alias_name='蓝星' AND target_name='Ayatan Cyan Star'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a2110bf767bec722337975e57f20e15b49ef46f983f402a775e2566e6e68199b', '蘑菇 p', 'Nokko Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a2110bf767bec722337975e57f20e15b49ef46f983f402a775e2566e6e68199b' OR (alias_name='蘑菇 p' AND target_name='Nokko Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f05e135c448c70986a50ee71ef364b93936873af8e00cde4b0509fc1b67cd1e7', '蘑菇甲', 'Nokko', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f05e135c448c70986a50ee71ef364b93936873af8e00cde4b0509fc1b67cd1e7' OR (alias_name='蘑菇甲' AND target_name='Nokko'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:94c78ddb8a03b2d0dc2d691b258ac9266ce859a44eee8b8029b3ee2cf5f8cb9e', '虚空甲', 'Xaku', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:94c78ddb8a03b2d0dc2d691b258ac9266ce859a44eee8b8029b3ee2cf5f8cb9e' OR (alias_name='虚空甲' AND target_name='Xaku'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:b25c1359e9620b1ac592bdf03189386a93823673f75f17bd8d6a08752146baee', '虚空甲 P', 'Xaku Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:b25c1359e9620b1ac592bdf03189386a93823673f75f17bd8d6a08752146baee' OR (alias_name='虚空甲 P' AND target_name='Xaku Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:51992e7ef319e49a3267c72a6620f39e8ec25630fa645d35c28efa8d2561da1f', '蛆', 'Nidus', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:51992e7ef319e49a3267c72a6620f39e8ec25630fa645d35c28efa8d2561da1f' OR (alias_name='蛆' AND target_name='Nidus'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:36dea77f2b06f6a2113786a8e77362ebd276557c229c18501197d5772f5d3c69', '蛆爹', 'Nidus', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:36dea77f2b06f6a2113786a8e77362ebd276557c229c18501197d5772f5d3c69' OR (alias_name='蛆爹' AND target_name='Nidus'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:721eafe5a3f460903ad60df9cf09a6600cdb5ebcf0a938cf31db5b8430af75b1', '蛆甲', 'Nidus', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:721eafe5a3f460903ad60df9cf09a6600cdb5ebcf0a938cf31db5b8430af75b1' OR (alias_name='蛆甲' AND target_name='Nidus'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:75b72cfa38bff63f6a4cfc09512a98f9426b36d9f5457537584109c9b4dd35bf', '蛆甲 P', 'Nidus Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:75b72cfa38bff63f6a4cfc09512a98f9426b36d9f5457537584109c9b4dd35bf' OR (alias_name='蛆甲 P' AND target_name='Nidus Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f41dc0d88b74aa1c0dbc1559889c9f76377a39a5a05f1a88b45b3ef07480822f', '蛇发女妖', 'Gorgon', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f41dc0d88b74aa1c0dbc1559889c9f76377a39a5a05f1a88b45b3ef07480822f' OR (alias_name='蛇发女妖' AND target_name='Gorgon'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fda3ea44dec98c3d2badbc22b3181c0f67ea34c72a0ef59dc148f92b38738ae5', '蛇甲', 'Lavos', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fda3ea44dec98c3d2badbc22b3181c0f67ea34c72a0ef59dc148f92b38738ae5' OR (alias_name='蛇甲' AND target_name='Lavos'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fa36c96d533559242072cca3e8fa55e775e0986a1d6eb4e87dd7f7c0452fa905', '蛇甲 P', 'Lavos Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fa36c96d533559242072cca3e8fa55e775e0986a1d6eb4e87dd7f7c0452fa905' OR (alias_name='蛇甲 P' AND target_name='Lavos Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6a6778264b660aa2997f306ae86d285cbf509605400d9ab482e78da4b49f09a4', '蜘蛛', 'Khora', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6a6778264b660aa2997f306ae86d285cbf509605400d9ab482e78da4b49f09a4' OR (alias_name='蜘蛛' AND target_name='Khora'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5bf404f33f6fb4ebcf96213687a4746c4446cb213c789b86afb445e87b042714', '蜘蛛 p', 'Oraxia Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5bf404f33f6fb4ebcf96213687a4746c4446cb213c789b86afb445e87b042714' OR (alias_name='蜘蛛 p' AND target_name='Oraxia Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:287f42ff7bbbe889aa541621b3cf4fd24a1637394f71ec6ba408dee0a51b62e8', '蜘蛛甲', 'Oraxia', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:287f42ff7bbbe889aa541621b3cf4fd24a1637394f71ec6ba408dee0a51b62e8' OR (alias_name='蜘蛛甲' AND target_name='Oraxia'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ed3c7eac681aba27e91450e5506b8085d7222e9d53e4224d1aae9ed40b065ffb', '蝶', 'Titania', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ed3c7eac681aba27e91450e5506b8085d7222e9d53e4224d1aae9ed40b065ffb' OR (alias_name='蝶' AND target_name='Titania'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:0e1a6421d8d4c39f89c787205e9da531201a13087236625e8a200634929140f9', '蝶妹', 'Titania', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:0e1a6421d8d4c39f89c787205e9da531201a13087236625e8a200634929140f9' OR (alias_name='蝶妹' AND target_name='Titania'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:58bcb0ba3663b379c34602e74008504bc8677cf53b9655817ca4be6fe4f046e2', '蝶妹 P', 'Titania Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:58bcb0ba3663b379c34602e74008504bc8677cf53b9655817ca4be6fe4f046e2' OR (alias_name='蝶妹 P' AND target_name='Titania Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:aa0a46bc9870e0e8fbb6a853c7b8726b3b1be1c63a2cfc39dc9d47755933b0c9', '蝶甲', 'Titania', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:aa0a46bc9870e0e8fbb6a853c7b8726b3b1be1c63a2cfc39dc9d47755933b0c9' OR (alias_name='蝶甲' AND target_name='Titania'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7320f1cb398f4b10604ac5134101018bdff9a1d523e90be2faa2603c320336e3', '蝶甲 P', 'Titania Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7320f1cb398f4b10604ac5134101018bdff9a1d523e90be2faa2603c320336e3' OR (alias_name='蝶甲 P' AND target_name='Titania Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c42f933b2572453f9057b3556ce96673a1e33d2246858c4f5c1dfdd89ec70147', '血妈', 'Garuda', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c42f933b2572453f9057b3556ce96673a1e33d2246858c4f5c1dfdd89ec70147' OR (alias_name='血妈' AND target_name='Garuda'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2d50be928af1ac2b0645bd86dc0b3a11e83e9b32d5856603c1edf2ca74b313e9', '血妈 P', 'Garuda Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2d50be928af1ac2b0645bd86dc0b3a11e83e9b32d5856603c1edf2ca74b313e9' OR (alias_name='血妈 P' AND target_name='Garuda Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:8a5a0ffa65b614f334295a2e3fbd40633a9c9706dc65be7cd73bcd308623401b', '裂罅', 'Riven Mod', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:8a5a0ffa65b614f334295a2e3fbd40633a9c9706dc65be7cd73bcd308623401b' OR (alias_name='裂罅' AND target_name='Riven Mod'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c9ac971b6160180c3c3463155f093a087e402eac48be8a2d08a94a4a83b78c85', '诺加减速娃 PRIME', 'NOVA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c9ac971b6160180c3c3463155f093a087e402eac48be8a2d08a94a4a83b78c85' OR (alias_name='诺加减速娃 PRIME' AND target_name='NOVA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f0721d9db4005ee2663d5a6f7c1b9408452ffc279482df43f1c12704df370bd7', '诺娃', 'Nova', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f0721d9db4005ee2663d5a6f7c1b9408452ffc279482df43f1c12704df370bd7' OR (alias_name='诺娃' AND target_name='Nova'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6da45057102496d9aa57e888e249b2443b1e2d44472d91875025f1785476c000', '诺娃 P', 'Nova Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6da45057102496d9aa57e888e249b2443b1e2d44472d91875025f1785476c000' OR (alias_name='诺娃 P' AND target_name='Nova Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a2b069014187711d2a480495ec0f4a98bd7c3837b1ab3bfc26accbbb97fdbe50', '豆子', 'Endo', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a2b069014187711d2a480495ec0f4a98bd7c3837b1ab3bfc26accbbb97fdbe50' OR (alias_name='豆子' AND target_name='Endo'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:20809ebf8735e4c54c8d19a2c59aa1ec149be68196c86f4d94a265b830239237', '赤毒', 'Kuva', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:20809ebf8735e4c54c8d19a2c59aa1ec149be68196c86f4d94a265b830239237' OR (alias_name='赤毒' AND target_name='Kuva'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:45616e45da68afa49ebbceb9c7dbd80f5e8f1d5e30a4b743e9e8803eb89a6250', '轮椅', 'Ash', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:45616e45da68afa49ebbceb9c7dbd80f5e8f1d5e30a4b743e9e8803eb89a6250' OR (alias_name='轮椅' AND target_name='Ash'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fe2e83e43441058e884ea5cbbb8089286cb1eaa27ab0bba49e748eca78973cff', '辐射甲', 'Qorvex', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fe2e83e43441058e884ea5cbbb8089286cb1eaa27ab0bba49e748eca78973cff' OR (alias_name='辐射甲' AND target_name='Qorvex'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a68931f9b3fc95997e197f327a1cfd3e696600fd447d91c66c92e41f3d49e94b', '辐射甲 P', 'Qorvex Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a68931f9b3fc95997e197f327a1cfd3e696600fd447d91c66c92e41f3d49e94b' OR (alias_name='辐射甲 P' AND target_name='Qorvex Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4b0f5e83d789f327373b89f7912448ec0ce574ec9e10a857a4360e5895074c2e', '迦楼罗', 'Garuda', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4b0f5e83d789f327373b89f7912448ec0ce574ec9e10a857a4360e5895074c2e' OR (alias_name='迦楼罗' AND target_name='Garuda'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fa990d2ae3b9eca1952db0a822580af2672873b20d8550af3577e0e84fd78ba8', '邪神', 'Loki', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fa990d2ae3b9eca1952db0a822580af2672873b20d8550af3577e0e84fd78ba8' OR (alias_name='邪神' AND target_name='Loki'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c40c073151c3161efbf3306cb71cbec293f3e9a0183e0ea17c745e9c27585fe1', '金土豆', 'Orokin Catalyst', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c40c073151c3161efbf3306cb71cbec293f3e9a0183e0ea17c745e9c27585fe1' OR (alias_name='金土豆' AND target_name='Orokin Catalyst'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3e20da9625b465bc8e56535ae1b9646833fa67d667b2878f34163e5955c177db', '金首发', '膛室 Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3e20da9625b465bc8e56535ae1b9646833fa67d667b2878f34163e5955c177db' OR (alias_name='金首发' AND target_name='膛室 Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6180c88e5958fcaa18313d767d7ee8e04b7ed4c7620980e71a5c4de7de3cdc5a', '银土豆', 'Orokin Reactor', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6180c88e5958fcaa18313d767d7ee8e04b7ed4c7620980e71a5c4de7de3cdc5a' OR (alias_name='银土豆' AND target_name='Orokin Reactor'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4ae4cbb277b4e93bab36ebfd260a4f138f3e6501565969802e9ca153ea5ef426', '阴阳', 'Equinox', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4ae4cbb277b4e93bab36ebfd260a4f138f3e6501565969802e9ca153ea5ef426' OR (alias_name='阴阳' AND target_name='Equinox'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:cb7865411a634f3ce302bbf85ec0b713744d82623dffa716170bf2ad051095c3', '阴阳 P', 'Equinox Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:cb7865411a634f3ce302bbf85ec0b713744d82623dffa716170bf2ad051095c3' OR (alias_name='阴阳 P' AND target_name='Equinox Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3e6bd43a8c31863282ce01c773f7641062a53cb66b1e6580a20d70d4709fb825', '阴阳甲', 'Equinox', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3e6bd43a8c31863282ce01c773f7641062a53cb66b1e6580a20d70d4709fb825' OR (alias_name='阴阳甲' AND target_name='Equinox'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:297548d400d21d4619898730fc6fec26f95e500d773dae64af7627722dade841', '阿什屎💩 PRIME', 'ASH PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:297548d400d21d4619898730fc6fec26f95e500d773dae64af7627722dade841' OR (alias_name='阿什屎💩 PRIME' AND target_name='ASH PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ce69feb0bf8234e4e232da42bb0ab835e931c8ed6691e6bc3af2f6f6e3046e61', '阿屎', 'Ash', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ce69feb0bf8234e4e232da42bb0ab835e931c8ed6691e6bc3af2f6f6e3046e61' OR (alias_name='阿屎' AND target_name='Ash'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1205bcb1d94e66e8de3c25180ebc8622cbc45457db0e0e1415c9a7a727fb41a8', '阿屎', 'Ash Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1205bcb1d94e66e8de3c25180ebc8622cbc45457db0e0e1415c9a7a727fb41a8' OR (alias_name='阿屎' AND target_name='Ash Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e53c64de0b922083bb3d5a74aa8ad14e6be4000d917abdd051270ed28ebfbf5e', '雕像', '阿耶檀识 Anasa 塑像', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e53c64de0b922083bb3d5a74aa8ad14e6be4000d917abdd051270ed28ebfbf5e' OR (alias_name='雕像' AND target_name='阿耶檀识 Anasa 塑像'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:3f5d33ec6fcdbab24e2e3e313cb2015f1630e77eac3f190beba06bb32d1aa403', '音妈', 'Banshee', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:3f5d33ec6fcdbab24e2e3e313cb2015f1630e77eac3f190beba06bb32d1aa403' OR (alias_name='音妈' AND target_name='Banshee'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d9f6567f672e752505f797cfe166732fee4dfadb88438f4f86faef2ef0e19352', '音妈 P', 'Banshee Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d9f6567f672e752505f797cfe166732fee4dfadb88438f4f86faef2ef0e19352' OR (alias_name='音妈 P' AND target_name='Banshee Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2ba112057d73144b6234cd43637be54c62a264240c66a0262763acd86e1f8d2d', '音妈 PRIME', 'BANSHEE PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2ba112057d73144b6234cd43637be54c62a264240c66a0262763acd86e1f8d2d' OR (alias_name='音妈 PRIME' AND target_name='BANSHEE PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:6fac1fd54932cc2bdc0fb408845daf3d01d0376d75d59cd48e8fd84fe9c0af04', '马哥 PRIME', 'MAG PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:6fac1fd54932cc2bdc0fb408845daf3d01d0376d75d59cd48e8fd84fe9c0af04' OR (alias_name='马哥 PRIME' AND target_name='MAG PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e0e018d2f3a00f7f29cc294c8adf4d6f89a63d825fe5712b187a5b104032f2be', '马良', 'Follie', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e0e018d2f3a00f7f29cc294c8adf4d6f89a63d825fe5712b187a5b104032f2be' OR (alias_name='马良' AND target_name='Follie'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:14728e3835dfe39926fb6e0c246a1c9cd93265bf5ed26bcc67b8251b8e78b43b', '马良 P', 'Follie Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:14728e3835dfe39926fb6e0c246a1c9cd93265bf5ed26bcc67b8251b8e78b43b' OR (alias_name='马良 P' AND target_name='Follie Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:319a71a70a93e9bc76f061eebd2d60db81fc741a5519348279fac606f7b4caf4', '驱魔使者', 'Harrow', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:319a71a70a93e9bc76f061eebd2d60db81fc741a5519348279fac606f7b4caf4' OR (alias_name='驱魔使者' AND target_name='Harrow'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:92282c77732de1181b38f6f6aed351623f784cb7bd040b1ce04b31d282b6a5ff', '驴龙王奥博勃 PRIME', 'OBERON PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:92282c77732de1181b38f6f6aed351623f784cb7bd040b1ce04b31d282b6a5ff' OR (alias_name='驴龙王奥博勃 PRIME' AND target_name='OBERON PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:75b24fd672816961638eecedf516e047cb026bcfce29e3d0a22991974cd3a791', '骰子 P', 'Koumei Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:75b24fd672816961638eecedf516e047cb026bcfce29e3d0a22991974cd3a791' OR (alias_name='骰子 P' AND target_name='Koumei Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:413131070002c9e862d8f167db0079947dd9f13fc1b836ea38df6d5fab80ba4f', '骰子甲', 'Koumei', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:413131070002c9e862d8f167db0079947dd9f13fc1b836ea38df6d5fab80ba4f' OR (alias_name='骰子甲' AND target_name='Koumei'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2378eb6eebe093497947664ceacd19b98bdff27d850de2c570b813cbe15a3bf5', '高斯', 'Gauss', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2378eb6eebe093497947664ceacd19b98bdff27d850de2c570b813cbe15a3bf5' OR (alias_name='高斯' AND target_name='Gauss'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d4ee9ec423d4e995f2d793be428fca2b75b3144bfec07842355389b16d570df0', '高斯 P', 'Gauss Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d4ee9ec423d4e995f2d793be428fca2b75b3144bfec07842355389b16d570df0' OR (alias_name='高斯 P' AND target_name='Gauss Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:5f9b34446685a7e31c5dd3c5a15011c34ad96b87ea73ac9da215cbe9e9a92e2a', '鬼妹', 'Wisp', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:5f9b34446685a7e31c5dd3c5a15011c34ad96b87ea73ac9da215cbe9e9a92e2a' OR (alias_name='鬼妹' AND target_name='Wisp'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:cdf0ba276a9288e5083c0cfb40a6c4b28bcd467475e75b94df808554fcb6433a', '鬼妹 P', 'Wisp Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:cdf0ba276a9288e5083c0cfb40a6c4b28bcd467475e75b94df808554fcb6433a' OR (alias_name='鬼妹 P' AND target_name='Wisp Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f1098ce1eda4a900e97241ccecae6422d62cc5058dfb98b89eb16d07b8f7c271', '鬼甲', 'Sevagoth', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f1098ce1eda4a900e97241ccecae6422d62cc5058dfb98b89eb16d07b8f7c271' OR (alias_name='鬼甲' AND target_name='Sevagoth'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f17555c0c1f0a32f6593c42db2bc62691cfd839e9b53726d78417bb8b75819ec', '鬼甲 P', 'Sevagoth Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f17555c0c1f0a32f6593c42db2bc62691cfd839e9b53726d78417bb8b75819ec' OR (alias_name='鬼甲 P' AND target_name='Sevagoth Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:42a2599d56fe04d17557b639bea1b58cb24987b2c4d9f9e4dfc8e345c61dc7a0', '魅影精灵', 'Mirage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:42a2599d56fe04d17557b639bea1b58cb24987b2c4d9f9e4dfc8e345c61dc7a0' OR (alias_name='魅影精灵' AND target_name='Mirage'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d1ee5b0a336d303dfae2b7b41f5a8afc992d31640901f8dc1a4095bd667dbaf9', '鱼刀', '鲮鲤剑', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d1ee5b0a336d303dfae2b7b41f5a8afc992d31640901f8dc1a4095bd667dbaf9' OR (alias_name='鱼刀' AND target_name='鲮鲤剑'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:151af65f23f130353cc64653b036cf3c495478974f71cd012a0da6cdda1d46bd', '鸟', 'Zephyr', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:151af65f23f130353cc64653b036cf3c495478974f71cd012a0da6cdda1d46bd' OR (alias_name='鸟' AND target_name='Zephyr'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:c5337f828f4fc58ebf6ad60d8d4493c0dca6abcbcd81bd46b09f05fdf32532f0', '鸟姐', 'Zephyr', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:c5337f828f4fc58ebf6ad60d8d4493c0dca6abcbcd81bd46b09f05fdf32532f0' OR (alias_name='鸟姐' AND target_name='Zephyr'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f44857d8a65029bbe7cacb9607fecf4882e2a2d8f300c39b84c257a68e2deba7', '鸟姐 P', 'Zephyr Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f44857d8a65029bbe7cacb9607fecf4882e2a2d8f300c39b84c257a68e2deba7' OR (alias_name='鸟姐 P' AND target_name='Zephyr Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:d4089f3a68bbba09aba2f716cdbe792dba275b3b95c2f219e099a15287e17ced', '鸡', 'Ember', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:d4089f3a68bbba09aba2f716cdbe792dba275b3b95c2f219e099a15287e17ced' OR (alias_name='鸡' AND target_name='Ember'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f268bbea300c6ff83c75995c53c23b85fb37fc63f3cf01fe7adf6ba1107f7079', '鸭梨', 'Yareli', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f268bbea300c6ff83c75995c53c23b85fb37fc63f3cf01fe7adf6ba1107f7079' OR (alias_name='鸭梨' AND target_name='Yareli'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:83afbd681ac3916c3ca621f046e8026443edb73cf4cf87241f2b75142008bbe9', '鸭梨 P', 'Yareli Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:83afbd681ac3916c3ca621f046e8026443edb73cf4cf87241f2b75142008bbe9' OR (alias_name='鸭梨 P' AND target_name='Yareli Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:e15e9fca371ff3b9d5adc039a634f002aa5f0771634cbf8d6ec2a541514a662a', '黄星', '阿耶檀识琥珀星', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:e15e9fca371ff3b9d5adc039a634f002aa5f0771634cbf8d6ec2a541514a662a' OR (alias_name='黄星' AND target_name='阿耶檀识琥珀星'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:1476bc17217e883d7fed456e4400f1fd6b1fa6e46ea9bdad07527821a0599967', '黑咖喱', 'Excalibur Umbra', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:1476bc17217e883d7fed456e4400f1fd6b1fa6e46ea9bdad07527821a0599967' OR (alias_name='黑咖喱' AND target_name='Excalibur Umbra'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ffd593bec308090c624e237af02c2b0aa9ea7140e92719681eedde561377d335', '齐天大圣', 'Wukong', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ffd593bec308090c624e237af02c2b0aa9ea7140e92719681eedde561377d335' OR (alias_name='齐天大圣' AND target_name='Wukong'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:f9ef41484b0e85e7619c9846ceae329c73e9b56a9b7c0563d5d9dcf2b6c6fb00', '龙甲', 'Chroma', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:f9ef41484b0e85e7619c9846ceae329c73e9b56a9b7c0563d5d9dcf2b6c6fb00' OR (alias_name='龙甲' AND target_name='Chroma'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:ec714d742e7138e88909c32e6d6bf3db9b749710d6428cb4508f892cc5e544c0', '龙甲 P', 'Chroma Prime', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:ec714d742e7138e88909c32e6d6bf3db9b749710d6428cb4508f892cc5e544c0' OR (alias_name='龙甲 P' AND target_name='Chroma Prime'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:90481a7cc3c40b449b09725611639efabbf4afe68692c33b27fbbf3d77d0fd78', '龙🐲🐉甲 PRIME', 'CHROMA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:90481a7cc3c40b449b09725611639efabbf4afe68692c33b27fbbf3d77d0fd78' OR (alias_name='龙🐲🐉甲 PRIME' AND target_name='CHROMA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:01e1948dde924fcf80b01ab995f3df22e313aa1cdeb0267de13ec0e33a4a44a3', '龟', 'Atlas', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:01e1948dde924fcf80b01ab995f3df22e313aa1cdeb0267de13ec0e33a4a44a3' OR (alias_name='龟' AND target_name='Atlas'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:2bf5ebb22c4df2e296fca7bbf749decf0743333ae50a8b5272ee86cf1d752a98', '🎵🎶🎼音乐甲 PRIME', 'OCTAVIA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:2bf5ebb22c4df2e296fca7bbf749decf0743333ae50a8b5272ee86cf1d752a98' OR (alias_name='🎵🎶🎼音乐甲 PRIME' AND target_name='OCTAVIA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:37f42dc849dcd06d2de2783769f1c47323d7ca6afa4aa672e47e54b418de22a7', '🐒🐵🙈🙉🙊🦍🦧猴子 PRIME', 'WUKONG PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:37f42dc849dcd06d2de2783769f1c47323d7ca6afa4aa672e47e54b418de22a7' OR (alias_name='🐒🐵🙈🙉🙊🦍🦧猴子 PRIME' AND target_name='WUKONG PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:be17084ce9f8e78dfd5dce5b28db3a91dc60f49127cc47bd332c949f1e6879e0', '🐛🪱🪰蛆 PRIME', 'NIDUS PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:be17084ce9f8e78dfd5dce5b28db3a91dc60f49127cc47bd332c949f1e6879e0' OR (alias_name='🐛🪱🪰蛆 PRIME' AND target_name='NIDUS PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fff7056455cfd7a6808fb34ead8ee45e47b63293faacd547684d2cb886b122de', '🐦🦜🕊🦩🦢鸟姐 PRIME', 'ZEPHYR PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fff7056455cfd7a6808fb34ead8ee45e47b63293faacd547684d2cb886b122de' OR (alias_name='🐦🦜🕊🦩🦢鸟姐 PRIME' AND target_name='ZEPHYR PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:a7bcfd830505649cf0190cd06ba8a6f19306da869bc4e20dfebe058192dba36c', '💿DJ PRIME', 'OCTAVIA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:a7bcfd830505649cf0190cd06ba8a6f19306da869bc4e20dfebe058192dba36c' OR (alias_name='💿DJ PRIME' AND target_name='OCTAVIA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:88476d258c7a3d3f4cc617f5342fc83afaab0f3e811defd1ee152b6d99aba855', '🦋蝴蝶妹 PRIME', 'TITANIA PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:88476d258c7a3d3f4cc617f5342fc83afaab0f3e811defd1ee152b6d99aba855' OR (alias_name='🦋蝴蝶妹 PRIME' AND target_name='TITANIA PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:4eef693cb5fd0cf0f7f9fe067cef7567f9689a789174a02dbbb64fc38d1d86a2', '🦠毒妈 PRIME', 'SARYN PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:4eef693cb5fd0cf0f7f9fe067cef7567f9689a789174a02dbbb64fc38d1d86a2' OR (alias_name='🦠毒妈 PRIME' AND target_name='SARYN PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:fb43755336d4e3221f4b41a030aa2d383d6c271a7f2050b75811bb5a76872322', '🦽 🦼 ♿︎♿♿️轮椅 PRIME', 'ASH PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:fb43755336d4e3221f4b41a030aa2d383d6c271a7f2050b75811bb5a76872322' OR (alias_name='🦽 🦼 ♿︎♿♿️轮椅 PRIME' AND target_name='ASH PRIME'));
INSERT INTO nnmbot.wf_community_alias (record_key, alias_name, target_name, updated_at, version)
SELECT 'alias:7fc2aab6fbf86fe70f864a4369f0bd3f3ff1130f76f0e8192dea0666a5046a0b', '🧊冰男队 PRIME', 'FROST PRIME', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_alias WHERE record_key='alias:7fc2aab6fbf86fe70f864a4369f0bd3f3ff1130f76f0e8192dea0666a5046a0b' OR (alias_name='🧊冰男队 PRIME' AND target_name='FROST PRIME'));
-- 社区riven_alias：按业务内容去重，多目标别名分别保留，既有人工记录优先。
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '备弹', '备弹', 'ammo_maximum', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='备弹' OR alias_name='备弹');
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '弹药', '弹药', 'ammo_maximum', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='弹药' OR (alias_name='弹药' AND attribute_code='ammo_maximum'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '基伤', '基伤', 'base_damage_/_melee_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='基伤' OR (alias_name='基伤' AND attribute_code='base_damage_/_melee_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '额外连击效率', '额外连击效率', 'chance_to_gain_extra_combo_count', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='额外连击效率' OR (alias_name='额外连击效率' AND attribute_code='chance_to_gain_extra_combo_count'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '初始连击', '初始连击', 'channeling_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='初始连击' OR (alias_name='初始连击' AND attribute_code='channeling_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '重击效率', '重击效率', 'channeling_efficiency', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='重击效率' OR (alias_name='重击效率' AND attribute_code='channeling_efficiency'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '冰', '冰', 'cold_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='冰' OR (alias_name='冰' AND attribute_code='cold_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '连击持续时间', '连击持续时间', 'combo_duration', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='连击持续时间' OR (alias_name='连击持续时间' AND attribute_code='combo_duration'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '暴率', '暴率', 'critical_chance', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='暴率' OR (alias_name='暴率' AND attribute_code='critical_chance'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '爆率', '爆率', 'critical_chance', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='爆率' OR (alias_name='爆率' AND attribute_code='critical_chance'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '滑爆', '滑爆', 'critical_chance_on_slide_attack', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='滑爆' OR (alias_name='滑爆' AND attribute_code='critical_chance_on_slide_attack'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '暴伤', '暴伤', 'critical_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='暴伤' OR (alias_name='暴伤' AND attribute_code='critical_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '爆伤', '爆伤', 'critical_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='爆伤' OR (alias_name='爆伤' AND attribute_code='critical_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT 'C', 'C', 'damage_vs_corpus', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='C' OR (alias_name='C' AND attribute_code='damage_vs_corpus'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '对C佬', '对C佬', 'damage_vs_corpus', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='对C佬' OR (alias_name='对C佬' AND attribute_code='damage_vs_corpus'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT 'G', 'G', 'damage_vs_grineer', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='G' OR (alias_name='G' AND attribute_code='damage_vs_grineer'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '对G佬', '对G佬', 'damage_vs_grineer', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='对G佬' OR (alias_name='对G佬' AND attribute_code='damage_vs_grineer'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT 'I', 'I', 'damage_vs_infested', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='I' OR (alias_name='I' AND attribute_code='damage_vs_infested'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '对I佬', '对I佬', 'damage_vs_infested', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='对I佬' OR (alias_name='对I佬' AND attribute_code='damage_vs_infested'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '电', '电', 'electric_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='电' OR (alias_name='电' AND attribute_code='electric_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '终结伤害', '终结伤害', 'finisher_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='终结伤害' OR (alias_name='终结伤害' AND attribute_code='finisher_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '射速/攻速', '射速/攻速', 'fire_rate_/_attack_speed', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='射速/攻速' OR (alias_name='射速/攻速' AND attribute_code='fire_rate_/_attack_speed'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '火', '火', 'heat_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='火' OR (alias_name='火' AND attribute_code='heat_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '冲', '冲', 'impact_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='冲' OR (alias_name='冲' AND attribute_code='impact_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '冲击', '冲击', 'impact_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='冲击' OR (alias_name='冲击' AND attribute_code='impact_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '多重', '多重', 'multishot', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='多重' OR (alias_name='多重' AND attribute_code='multishot'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '飞行速度', '飞行速度', 'projectile_speed', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='飞行速度' OR (alias_name='飞行速度' AND attribute_code='projectile_speed'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '穿透', '穿透', 'punch_through', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='穿透' OR (alias_name='穿透' AND attribute_code='punch_through'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '穿刺', '穿刺', 'puncture_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='穿刺' OR (alias_name='穿刺' AND attribute_code='puncture_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '范围', '范围', 'range', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='范围' OR (alias_name='范围' AND attribute_code='range'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '后座', '后座', 'recoil', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='后座' OR (alias_name='后座' AND attribute_code='recoil'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '换弹', '换弹', 'reload_speed', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='换弹' OR (alias_name='换弹' AND attribute_code='reload_speed'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '切割', '切割', 'slash_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='切割' OR (alias_name='切割' AND attribute_code='slash_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '触发', '触发', 'status_chance', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='触发' OR (alias_name='触发' AND attribute_code='status_chance'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '触发几率', '触发几率', 'status_chance', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='触发几率' OR (alias_name='触发几率' AND attribute_code='status_chance'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '触发时间', '触发时间', 'status_duration', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='触发时间' OR (alias_name='触发时间' AND attribute_code='status_duration'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '毒', '毒', 'toxin_damage', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='毒' OR (alias_name='毒' AND attribute_code='toxin_damage'));
INSERT INTO nnmbot.wf_community_riven_alias (record_key, alias_name, attribute_code, updated_at, version)
SELECT '变焦', '变焦', 'zoom', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_riven_alias WHERE record_key='变焦' OR (alias_name='变焦' AND attribute_code='zoom'));
-- 已核实用途的科研整词与赏金短码由解析映射按作用域读取。
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('archimedea:EMP Black Hole', 'archimedea', 'EMP Black Hole', 'EMP 黑洞', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('archimedea:EMPBlackHole', 'archimedea', 'EMPBlackHole', 'EMP 黑洞', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('archimedea:Operator Only', 'archimedea', 'Operator Only', '仅限操作者', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('archimedea:Shieldless', 'archimedea', 'Shieldless', '无护盾', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('archimedea:Vampire Rock', 'archimedea', 'Vampire Rock', '吸血岩', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('archimedea:Warframe Only', 'archimedea', 'Warframe Only', '仅限战甲', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('bounty_stage:ChamberA', 'bounty_stage', 'ChamberA', '隔离库（等级2）', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('bounty_stage:ChamberB', 'bounty_stage', 'ChamberB', '隔离库（等级1）', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('bounty_stage:ChamberC', 'bounty_stage', 'ChamberC', '隔离库（等级3）', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;
-- warframe-info-api 自维护的玩法称呼。用中文玩家用语关联通行英文术语。
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:仲裁', 'gameplay', 'Arbitration', '仲裁', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Arbitration' AND chinese_text='仲裁')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:赏金', 'gameplay', 'Bounty', '赏金', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Bounty' AND chinese_text='赏金')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:中断', 'gameplay', 'Disruption', '中断', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Disruption' AND chinese_text='中断')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:镀层', 'gameplay', 'Galvanized', '镀层', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Galvanized' AND chinese_text='镀层')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:大嘴', 'gameplay', 'Helminth', '大嘴', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Helminth' AND chinese_text='大嘴')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:气球', 'gameplay', 'Lure', '气球', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Lure' AND chinese_text='气球')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:机甲', 'gameplay', 'Necramech', '机甲', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Necramech' AND chinese_text='机甲')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:殁世机甲', 'gameplay', 'Necramech', '殁世机甲', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Necramech' AND chinese_text='殁世机甲')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:电波', 'gameplay', 'Nightwave', '电波', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Nightwave' AND chinese_text='电波')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:钢路', 'gameplay', 'Steel Path', '钢路', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Steel Path' AND chinese_text='钢路')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:钢铁', 'gameplay', 'Steel Path', '钢铁', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Steel Path' AND chinese_text='钢铁')
ON CONFLICT (record_key) DO NOTHING;
INSERT INTO nnmbot.wf_community_term (record_key, term_group, english_text, chinese_text, updated_at, version)
SELECT 'gameplay:信条', 'gameplay', 'Tenet', '信条', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM nnmbot.wf_community_term WHERE english_text='Tenet' AND chinese_text='信条')
ON CONFLICT (record_key) DO NOTHING;
-- END WARFRAME REFERENCE SEED
-- 2026-11-21 从 arbi.wf.wiki 的 tierlist.default.json 与 leaderboard 导入；估值为近 180 天 19,716 条玩家记录 perHour 中位数。
-- 共 88 个节点，80 个有估值、8 个无样本；16 个网站评级原样保留，另外 64 个按 80 个估值节点的六分位推算。
-- 网站 S/A+/A/A-/B/C 映射为本项目 A/B/C/D/E/F；推算边界为 459/563/614/694/781，8 个无估值节点仍为 NULL。
WITH arbitration_nodes(node_key, efficiency_grade, essence_amount) AS (
    VALUES
        ('SolNode450', 'A', 904), ('SolNode106', 'A', 798), ('SolNode25', 'A', 806), ('SolNode64', 'A', 637),
        ('SolNode23', 'B', 764), ('SolNode172', 'B', 725), ('SolNode719', 'B', 719), ('SolNode147', 'B', 624),
        ('SolNode224', 'C', 547), ('SolNode167', 'C', 635), ('SolNode412', 'C', 534), ('SolNode211', 'C', 563),
        ('SolNode43', 'D', 387), ('SolNode85', 'D', 421), ('SolNode185', 'F', 230), ('SolNode18', 'F', 337),
        ('ClanNode14', NULL, 863), ('ClanNode16', NULL, 879), ('ClanNode17', NULL, 876), ('ClanNode19', NULL, 694),
        ('ClanNode2', NULL, 579), ('ClanNode20', NULL, NULL), ('ClanNode21', NULL, 744), ('ClanNode23', NULL, 770),
        ('ClanNode25', NULL, 226), ('ClanNode3', NULL, NULL), ('ClanNode4', NULL, 636), ('ClanNode5', NULL, 734),
        ('ClanNode7', NULL, NULL), ('ClanNode9', NULL, 757), ('SettlementNode11', NULL, 399), ('SettlementNode3', NULL, 609),
        ('SolNode100', NULL, 769), ('SolNode101', NULL, NULL), ('SolNode118', NULL, 850), ('SolNode123', NULL, 632),
        ('SolNode125', NULL, 574), ('SolNode146', NULL, 719), ('SolNode16', NULL, NULL), ('SolNode166', NULL, 735),
        ('SolNode17', NULL, 511), ('SolNode177', NULL, 882), ('SolNode187', NULL, 683), ('SolNode216', NULL, 101),
        ('SolNode22', NULL, 547), ('SolNode230', NULL, 367), ('SolNode233', NULL, NULL), ('SolNode26', NULL, 541),
        ('SolNode30', NULL, 639), ('SolNode302', NULL, 534), ('SolNode308', NULL, 708), ('SolNode309', NULL, 495),
        ('SolNode39', NULL, 208), ('SolNode405', NULL, 909), ('SolNode409', NULL, 829), ('SolNode6', NULL, NULL),
        ('SolNode711', NULL, 900), ('SolNode718', NULL, 459), ('SolNode81', NULL, 761), ('SolNode87', NULL, 790),
        ('SolNode94', NULL, 798), ('SolNode96', NULL, 781), ('SolNode232', NULL, NULL), ('ClanNode12', NULL, 282),
        ('ClanNode11', NULL, 643), ('ClanNode13', NULL, 614), ('ClanNode0', NULL, 526), ('ClanNode1', NULL, 692),
        ('ClanNode10', NULL, 190), ('ClanNode22', NULL, 650), ('SolNode149', NULL, 644), ('SolNode42', NULL, 589),
        ('SolNode195', NULL, 590), ('ClanNode24', NULL, 619), ('SolNode707', NULL, 590), ('SolNode72', NULL, 548),
        ('ClanNode18', NULL, 606), ('SolNode122', NULL, 556), ('SolNode164', NULL, 605), ('SolNode212', NULL, 551),
        ('ClanNode6', NULL, 620), ('ClanNode8', NULL, 603), ('SolNode46', NULL, 588), ('SolNode305', NULL, 443),
        ('SolNode402', NULL, 575), ('SolNode408', NULL, 582), ('ClanNode15', NULL, 460), ('SolNode130', NULL, 454)
), official_nodes AS (
    SELECT bkey,
        COALESCE(MAX(NULLIF(localized_name, '')), MAX(NULLIF(official_name, '')), bkey) AS node_name,
        COALESCE(MAX(NULLIF(zhsystemname, '')), MAX(NULLIF(parent_name, ''))) AS system_name
    FROM nnmbot.wf_official_region_root
    WHERE bkey IN (SELECT node_key FROM arbitration_nodes)
      AND enabled
    GROUP BY bkey
)
INSERT INTO nnmbot.wf_community_arbitration_essence
    (record_key, node_key, node_name, system_name, essence_amount, efficiency_grade, updated_at, version)
SELECT nodes.node_key, nodes.node_key,
    COALESCE(official.node_name, nodes.node_key), official.system_name,
    nodes.essence_amount,
    CASE
        WHEN nodes.efficiency_grade IS NOT NULL THEN nodes.efficiency_grade
        WHEN nodes.essence_amount IS NULL THEN NULL
        WHEN nodes.essence_amount <= 459 THEN 'F'
        WHEN nodes.essence_amount <= 563 THEN 'E'
        WHEN nodes.essence_amount <= 614 THEN 'D'
        WHEN nodes.essence_amount <= 694 THEN 'C'
        WHEN nodes.essence_amount <= 781 THEN 'B'
        ELSE 'A'
    END,
    CURRENT_TIMESTAMP, 0
FROM arbitration_nodes AS nodes
LEFT JOIN official_nodes AS official ON official.bkey = nodes.node_key
ON CONFLICT (node_key) DO NOTHING;

-- BEGIN COMMUNITY WORLDSTATE STATIC SEEDS


-- 回廊简称指向玩家装备，而不是同名的 BaseStaff 模板；只在灵化轮换查这个别名。
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
VALUES ('circuit:Bo', 'circuit', 'Bo', '/Lotus/Weapons/Tenno/Melee/Staff/Staff', CURRENT_TIMESTAMP, 0)
ON CONFLICT (record_key) DO NOTHING;

-- 已核对的原值、奖池及图标关系由解析映射维护。
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
SELECT mapping_key, split_part(mapping_key, ':', 1), substring(mapping_key from position(':' in mapping_key) + 1),
    target_key, CURRENT_TIMESTAMP, 0
FROM (VALUES
    ('mission-type:Assassination', 'MT_ASSASSINATION'),
    ('mission-type:Capture', 'MT_CAPTURE'),
    ('mission-type:Defense', 'MT_DEFENSE'),
    ('mission-type:Exterminate', 'MT_EXTERMINATION'),
    ('mission-type:MobileDefense', 'MT_MOBILE_DEFENSE'),
    ('mission-type:Sabotage', 'MT_SABOTAGE'),
    ('mission-type:Spy', 'MT_INTEL'),
    ('mission-type:Survival', 'MT_SURVIVAL'),
    ('mission-type:Rescue', 'MT_RESCUE'),
    ('mission-type:Territory', 'MT_TERRITORY'),
    ('bounty-pool:HexSyndicate', 'rewardPools/Hex Bounty Rewards'),
    ('bounty-pool:ZarimanSyndicate', 'rewardPools/Zariman Bounty Rewards'),
    ('bounty-pool:EntratiLabSyndicate', 'rewardPools/Albrecht''s Laboratories Bounty Rewards'),
    ('worldstate-image:credit-bundle', '/Lotus/Interface/Icons/StoreIcons/Currency/GenericCreditIcon.png'),
    ('worldstate-image:cetus', '/Lotus/Interface/Icons/StoreIcons/Captura/PlainsOfEidolonLandscape.png'),
    ('worldstate-image:vallis', '/Lotus/Interface/Icons/StoreIcons/Captura/VallisLandscape.png'),
    ('worldstate-image:cambion', '/Lotus/Interface/Icons/StoreIcons/Captura/DeimosCambionDriftLandscape.png'),
    ('worldstate-image:earth', '/Lotus/Interface/Icons/StoreIcons/Captura/TeacherEarth.png'),
    ('worldstate-image:zariman', '/Lotus/Interface/Icons/StoreIcons/Captura/Zariman/ZarParkHub.png'),
    ('worldstate-image:duviri', '/Lotus/Interface/Icons/StoreIcons/Captura/Duviri/DuviriMainlandA.png'),
    ('item-icon:/Lotus/Types/Recipes/Components/UmbraFormaBlueprint', '/Lotus/Types/Items/MiscItems/FormaUmbra'),
    ('item-icon:/Lotus/Types/BoosterPacks/CalendarRivenPack', '/Lotus/Upgrades/Mods/Randomized/RawMeleeRandomMod'),
    ('worldstate-reward:Endo', '/Lotus/Upgrades/Mods/FusionBundles/AlertFusionBundleLarge'),
    ('worldstate-reward:Kuva', '/Lotus/Types/Items/MiscItems/Kuva'),
    ('worldstate-reward:Steel Essence', '/Lotus/Types/Items/MiscItems/SteelEssence'),
    ('worldstate-reward:内融核心', '/Lotus/Upgrades/Mods/FusionBundles/AlertFusionBundleLarge'),
    ('worldstate-reward:赤毒', '/Lotus/Types/Items/MiscItems/Kuva'),
    ('worldstate-reward:钢铁精华', '/Lotus/Types/Items/MiscItems/SteelEssence')
) AS mapping(mapping_key, target_key)
ON CONFLICT (record_key) DO NOTHING;

-- Oracle 友军对象并非官方物品；完整路径只在赏金友军词条分组内解释。
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
SELECT 'bounty_ally:' || source_key, 'bounty_ally', source_key, display_name, CURRENT_TIMESTAMP, 0
FROM (VALUES
    ('Arthur', '亚瑟'),
    ('Lettie', '莱蒂'),
    ('Amir', '阿米尔'),
    ('Aoi', '碧'),
    ('Eleanor', '埃莉诺'),
    ('Quincy', '昆西')
) AS ally(agent, display_name)
CROSS JOIN LATERAL (SELECT '/Lotus/Types/Gameplay/1999Wf/ProtoframeAllies/' || agent || 'AllyAgent') AS identity(source_key)
ON CONFLICT (record_key) DO NOTHING;

-- 普通隔离库的官方 Final Stage 与游戏池下标 2 对应；下标 3 是秘典隔离库的最终阶段字段。
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
SELECT 'vault-clear-pool:' || source_key, 'vault-clear-pool', source_key, '2', CURRENT_TIMESTAMP, 0
FROM (VALUES ('A'), ('B'), ('C')) AS tier(code)
CROSS JOIN (VALUES ('A'), ('B'), ('C')) AS rotation(code)
CROSS JOIN LATERAL (SELECT '/Lotus/Types/Game/MissionDecks/DeimosMissionRewards/VaultBountyTier'
    || tier.code || 'Table' || rotation.code || 'Rewards') AS identity(source_key)
ON CONFLICT (record_key) DO NOTHING;

-- 固定目标用于普通隔离库展示，额外开启储藏室单独标为可选步骤。
INSERT INTO nnmbot.wf_community_reference_mapping (record_key, mapping_scope, source_key, target_key, updated_at, version)
SELECT 'isolation_vault_phase:' || code, 'isolation_vault_phase', code, display_name, CURRENT_TIMESTAMP, 0
FROM (VALUES
    ('BAIT', '收集残留物，制备诱饵并开启洞口'),
    ('TOXICITY', '抵御感染者并控制毒性'),
    ('GUARDIANS', '击败守卫殁世机甲（完成奖励）'),
    ('BONUS', '护送洛德并解锁储藏室（可选）')
) AS phase(code, display_name)
ON CONFLICT (record_key) DO NOTHING;

COMMIT;
