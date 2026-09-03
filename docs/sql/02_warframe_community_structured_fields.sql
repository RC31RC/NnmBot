\set ON_ERROR_STOP on

BEGIN;

-- 本次增量只为 10 个社区 JSON 数据集增加结构化投影列。
-- 动态世界状态继续仅保存在应用内存中，本脚本不会创建任何 WorldState 表。

-- 旧数据理论上均为合法 JSON；临时解析函数避免单条历史脏数据阻断整次结构升级。
CREATE OR REPLACE FUNCTION pg_temp.try_jsonb(value TEXT)
RETURNS JSONB
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    RETURN value::JSONB;
EXCEPTION WHEN OTHERS THEN
    RETURN '{}'::JSONB;
END;
$$;

-- translation.json：中英文名称及物品类别标记。
ALTER TABLE nnmbot.wf_community_dataset_translation
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS chinese_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS english_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS prime_item BOOLEAN,
    ADD COLUMN IF NOT EXISTS set_item BOOLEAN;

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.chinese_name IS '社区中文名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.english_name IS '社区英文名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.prime_item IS '是否为 Prime 物品';
COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.set_item IS '是否为套装物品';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_translation
)
UPDATE nnmbot.wf_community_dataset_translation record
SET source_id = CASE
        WHEN parsed.value ->> 'id' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'id')::BIGINT
        ELSE NULL
    END,
    chinese_name = btrim(parsed.value ->> 'cn'),
    english_name = btrim(parsed.value ->> 'en'),
    prime_item = CASE lower(parsed.value ->> 'is_prime')
        WHEN 'true' THEN TRUE WHEN '1' THEN TRUE
        WHEN 'false' THEN FALSE WHEN '0' THEN FALSE
        ELSE NULL
    END,
    set_item = CASE lower(parsed.value ->> 'is_set')
        WHEN 'true' THEN TRUE WHEN '1' THEN TRUE
        WHEN 'false' THEN FALSE WHEN '0' THEN FALSE
        ELSE NULL
    END
FROM parsed
WHERE parsed.id = record.id;

-- alias.json：社区别名及其英文目标名称。
ALTER TABLE nnmbot.wf_community_dataset_alias
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS alias_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS target_name VARCHAR(500);

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.alias_name IS '社区中文别名';
COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.target_name IS '别名对应的英文目标名称';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_alias
)
UPDATE nnmbot.wf_community_dataset_alias record
SET source_id = CASE
        WHEN parsed.value ->> 'id' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'id')::BIGINT
        ELSE NULL
    END,
    alias_name = btrim(parsed.value ->> 'cn'),
    target_name = btrim(parsed.value ->> 'en')
FROM parsed
WHERE parsed.id = record.id;

-- nodes.json：星图节点及任务结构。
ALTER TABLE nnmbot.wf_community_dataset_nodes
    ADD COLUMN IF NOT EXISTS unique_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS system_index INTEGER,
    ADD COLUMN IF NOT EXISTS system_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS node_type INTEGER,
    ADD COLUMN IF NOT EXISTS mastery_req INTEGER,
    ADD COLUMN IF NOT EXISTS mission_index INTEGER,
    ADD COLUMN IF NOT EXISTS faction_index INTEGER,
    ADD COLUMN IF NOT EXISTS min_enemy_level INTEGER,
    ADD COLUMN IF NOT EXISTS max_enemy_level INTEGER;

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.unique_name IS '游戏内部节点 uniqueName';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.display_name IS '社区节点显示名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.system_index IS '节点所属星系索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.system_name IS '节点所属星系名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.node_type IS '节点类型索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.mastery_req IS '节点段位要求';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.mission_index IS '节点任务类型索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.faction_index IS '节点敌对派系索引';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.min_enemy_level IS '节点最低敌人等级';
COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.max_enemy_level IS '节点最高敌人等级';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_nodes
)
UPDATE nnmbot.wf_community_dataset_nodes record
SET unique_name = btrim(parsed.value ->> 'uniqueName'),
    display_name = btrim(parsed.value ->> 'name'),
    system_index = CASE
        WHEN parsed.value ->> 'systemIndex' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'systemIndex')::INTEGER
        ELSE NULL
    END,
    system_name = btrim(parsed.value ->> 'systemName'),
    node_type = CASE
        WHEN parsed.value ->> 'nodeType' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'nodeType')::INTEGER
        ELSE NULL
    END,
    mastery_req = CASE
        WHEN parsed.value ->> 'masteryReq' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'masteryReq')::INTEGER
        ELSE NULL
    END,
    mission_index = CASE
        WHEN parsed.value ->> 'missionIndex' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'missionIndex')::INTEGER
        ELSE NULL
    END,
    faction_index = CASE
        WHEN parsed.value ->> 'factionIndex' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'factionIndex')::INTEGER
        ELSE NULL
    END,
    min_enemy_level = CASE
        WHEN parsed.value ->> 'minEnemyLevel' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'minEnemyLevel')::INTEGER
        ELSE NULL
    END,
    max_enemy_level = CASE
        WHEN parsed.value ->> 'maxEnemyLevel' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'maxEnemyLevel')::INTEGER
        ELSE NULL
    END
FROM parsed
WHERE parsed.id = record.id;

-- reward_pool.json：奖励池标识、奖励数量及完整奖励数组。
ALTER TABLE nnmbot.wf_community_dataset_reward_pool
    ADD COLUMN IF NOT EXISTS unique_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS reward_count INTEGER,
    ADD COLUMN IF NOT EXISTS rewards_json TEXT;

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.unique_name IS '游戏内部奖励池 uniqueName';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.reward_count IS '奖励数组元素数量';
COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.rewards_json IS '完整奖励数组 JSON';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_reward_pool
)
UPDATE nnmbot.wf_community_dataset_reward_pool record
SET unique_name = btrim(parsed.value ->> 'uniqueName'),
    reward_count = CASE
        WHEN jsonb_typeof(parsed.value -> 'rewards') = 'array'
            THEN jsonb_array_length(parsed.value -> 'rewards')
        ELSE 0
    END,
    rewards_json = CASE
        WHEN parsed.value ? 'rewards' AND parsed.value -> 'rewards' <> 'null'::JSONB
            THEN (parsed.value -> 'rewards')::TEXT
        ELSE '[]'
    END
FROM parsed
WHERE parsed.id = record.id;

-- state_translation.json：动态世界状态的名称映射，本表只存社区翻译元数据。
ALTER TABLE nnmbot.wf_community_dataset_state_translation
    ADD COLUMN IF NOT EXISTS unique_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS description TEXT;

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.unique_name IS '动态世界状态内部 uniqueName';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.display_name IS '动态世界状态显示名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.description IS '动态世界状态说明';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_state_translation
)
UPDATE nnmbot.wf_community_dataset_state_translation record
SET unique_name = btrim(parsed.value ->> 'uniqueName'),
    display_name = btrim(parsed.value ->> 'name'),
    description = btrim(parsed.value ->> 'description')
FROM parsed
WHERE parsed.id = record.id;

-- market_element.json：Market 元素代码与社区显示名称。
ALTER TABLE nnmbot.wf_community_dataset_market_element
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS element_code VARCHAR(200),
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(200);

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.element_code IS 'Warframe Market 元素代码';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.display_name IS '社区中文显示名称';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_market_element
)
UPDATE nnmbot.wf_community_dataset_market_element record
SET source_id = CASE
        WHEN parsed.value ->> 'id' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'id')::BIGINT
        ELSE NULL
    END,
    element_code = btrim(parsed.value ->> 'en'),
    display_name = btrim(parsed.value ->> 'cn')
FROM parsed
WHERE parsed.id = record.id;

-- market_riven_tion.json：Riven 属性及其适用范围。
ALTER TABLE nnmbot.wf_community_dataset_market_riven_tion
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS effect VARCHAR(500),
    ADD COLUMN IF NOT EXISTS attribute_group VARCHAR(100),
    ADD COLUMN IF NOT EXISTS negative_only BOOLEAN,
    ADD COLUMN IF NOT EXISTS positive_is_negative BOOLEAN,
    ADD COLUMN IF NOT EXISTS prefix VARCHAR(100),
    ADD COLUMN IF NOT EXISTS search_only BOOLEAN,
    ADD COLUMN IF NOT EXISTS suffix VARCHAR(100),
    ADD COLUMN IF NOT EXISTS units VARCHAR(100),
    ADD COLUMN IF NOT EXISTS url_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS exclusive_to_json TEXT;

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.source_id IS '社区源记录 ids';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.effect IS 'Riven 词条效果说明';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.attribute_group IS 'Riven 属性分组';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.negative_only IS '是否仅用于负面词条';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.positive_is_negative IS '正值是否按负面效果解释';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.prefix IS 'Riven 词条前缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.search_only IS '是否仅用于搜索匹配';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.suffix IS 'Riven 词条后缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.units IS 'Riven 词条数值单位';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.url_name IS 'Warframe Market URL 属性名';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.exclusive_to_json IS '词条适用武器类型 JSON';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_market_riven_tion
)
UPDATE nnmbot.wf_community_dataset_market_riven_tion record
SET source_id = CASE
        WHEN parsed.value ->> 'ids' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'ids')::BIGINT
        ELSE NULL
    END,
    effect = btrim(parsed.value ->> 'effect'),
    attribute_group = btrim(parsed.value ->> 'group'),
    negative_only = CASE lower(parsed.value ->> 'negative_only')
        WHEN 'true' THEN TRUE WHEN '1' THEN TRUE
        WHEN 'false' THEN FALSE WHEN '0' THEN FALSE
        ELSE NULL
    END,
    positive_is_negative = CASE lower(parsed.value ->> 'positive_is_negative')
        WHEN 'true' THEN TRUE WHEN '1' THEN TRUE
        WHEN 'false' THEN FALSE WHEN '0' THEN FALSE
        ELSE NULL
    END,
    prefix = btrim(parsed.value ->> 'prefix'),
    search_only = CASE lower(parsed.value ->> 'search_only')
        WHEN 'true' THEN TRUE WHEN '1' THEN TRUE
        WHEN 'false' THEN FALSE WHEN '0' THEN FALSE
        ELSE NULL
    END,
    suffix = btrim(parsed.value ->> 'suffix'),
    units = btrim(parsed.value ->> 'units'),
    url_name = btrim(parsed.value ->> 'url_name'),
    exclusive_to_json = CASE jsonb_typeof(parsed.value -> 'exclusive_to')
        WHEN 'string' THEN btrim(parsed.value ->> 'exclusive_to')
        WHEN NULL THEN NULL
        ELSE (parsed.value -> 'exclusive_to')::TEXT
    END
FROM parsed
WHERE parsed.id = record.id;

-- market_riven_tion_alias.json：Riven 属性代码与社区中文别名。
ALTER TABLE nnmbot.wf_community_dataset_market_riven_tion_alias
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS attribute_code VARCHAR(200),
    ADD COLUMN IF NOT EXISTS alias_name VARCHAR(200);

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.attribute_code IS 'Riven 属性代码';
COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.alias_name IS 'Riven 属性中文别名';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_market_riven_tion_alias
)
UPDATE nnmbot.wf_community_dataset_market_riven_tion_alias record
SET source_id = CASE
        WHEN parsed.value ->> 'id' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'id')::BIGINT
        ELSE NULL
    END,
    attribute_code = btrim(parsed.value ->> 'en'),
    alias_name = btrim(parsed.value ->> 'cn')
FROM parsed
WHERE parsed.id = record.id;

-- riven_analyse_trend.json：各武器类别的 Riven 分析系数。
ALTER TABLE nnmbot.wf_community_dataset_riven_analyse_trend
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS prefix VARCHAR(100),
    ADD COLUMN IF NOT EXISTS suffix VARCHAR(100),
    ADD COLUMN IF NOT EXISTS archwing DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS melee_value DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS pistol DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS rifle DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS shotgun DOUBLE PRECISION;

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.display_name IS 'Riven 分析趋势名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.prefix IS 'Riven 分析词条前缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.suffix IS 'Riven 分析词条后缀';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.archwing IS 'Archwing 武器趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.melee_value IS '近战武器趋势系数，对应上游 melle 字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.pistol IS '手枪趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.rifle IS '步枪趋势系数';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.shotgun IS '霰弹枪趋势系数';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_riven_analyse_trend
)
UPDATE nnmbot.wf_community_dataset_riven_analyse_trend record
SET source_id = CASE
        WHEN parsed.value ->> 'id' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'id')::BIGINT
        ELSE NULL
    END,
    display_name = btrim(parsed.value ->> 'name'),
    prefix = btrim(parsed.value ->> 'prefix'),
    suffix = btrim(parsed.value ->> 'suffix'),
    archwing = CASE
        WHEN parsed.value ->> 'archwing'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'archwing')::DOUBLE PRECISION
        ELSE NULL
    END,
    melee_value = CASE
        WHEN parsed.value ->> 'melle'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'melle')::DOUBLE PRECISION
        ELSE NULL
    END,
    pistol = CASE
        WHEN parsed.value ->> 'pistol'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'pistol')::DOUBLE PRECISION
        ELSE NULL
    END,
    rifle = CASE
        WHEN parsed.value ->> 'rifle'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'rifle')::DOUBLE PRECISION
        ELSE NULL
    END,
    shotgun = CASE
        WHEN parsed.value ->> 'shotgun'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'shotgun')::DOUBLE PRECISION
        ELSE NULL
    END
FROM parsed
WHERE parsed.id = record.id;

-- riven_trend.json：倾向变化前后数据及武器类型。
ALTER TABLE nnmbot.wf_community_dataset_riven_trend
    ADD COLUMN IF NOT EXISTS source_id BIGINT,
    ADD COLUMN IF NOT EXISTS trend_time VARCHAR(32),
    ADD COLUMN IF NOT EXISTS new_dot VARCHAR(32),
    ADD COLUMN IF NOT EXISTS new_num DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS old_dot VARCHAR(32),
    ADD COLUMN IF NOT EXISTS old_num DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS trend_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS weapon_type VARCHAR(100);

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.source_id IS '社区源记录 id';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.trend_time IS 'Riven 趋势时间，对应上游 isDate 字段';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.new_dot IS '变化后的倾向点位';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.new_num IS '变化后的倾向数值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.old_dot IS '变化前的倾向点位';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.old_num IS '变化前的倾向数值';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.trend_name IS 'Riven 趋势名称';
COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.weapon_type IS 'Riven 趋势武器类型';

WITH parsed AS (
    SELECT id, pg_temp.try_jsonb(current_json) AS value
    FROM nnmbot.wf_community_dataset_riven_trend
)
UPDATE nnmbot.wf_community_dataset_riven_trend record
SET source_id = CASE
        WHEN parsed.value ->> 'id' ~ '^-?[0-9]+$'
            THEN (parsed.value ->> 'id')::BIGINT
        ELSE NULL
    END,
    trend_time = btrim(parsed.value ->> 'isDate'),
    new_dot = btrim(parsed.value ->> 'new_dot'),
    new_num = CASE
        WHEN parsed.value ->> 'new_num'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'new_num')::DOUBLE PRECISION
        ELSE NULL
    END,
    old_dot = btrim(parsed.value ->> 'old_dot'),
    old_num = CASE
        WHEN parsed.value ->> 'old_num'
                ~ '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([eE][+-]?[0-9]+)?$'
            THEN (parsed.value ->> 'old_num')::DOUBLE PRECISION
        ELSE NULL
    END,
    trend_name = btrim(parsed.value ->> 'trend_name'),
    weapon_type = btrim(parsed.value ->> 'type')
FROM parsed
WHERE parsed.id = record.id;

COMMIT;
