package com.nnm.bot.warframe.constant;

import java.util.List;
import java.util.Locale;

/** 静态命令目录分类选择器；官方目录与人工白名单使用各自的数据模型。 */
public final class WarframeCommandCatalog {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeCommandCatalog() {
    }

    public enum Category {
        RULE_CONCLAVE("rule_conclave", "武形秘仪规则"),
        RULE_ARCHON_SHARD("rule_archon_shard", "源力石"),
        RULE_STEEL_PATH("rule_steel_path", "钢铁兑换目录"),
        RULE_EVENT("rule_event", "活动静态规则"),
        RULE_TUTORIAL("rule_tutorial", "教程资料"),
        RULE_ARCANE_GUIDE("rule_arcane_guide", "历史奥术攻略"),
        REFERENCE_LEGACY_ITEM("reference_legacy_item", "历史物品目录"),
        REFERENCE_RIVEN_ATTRIBUTE("reference_riven_attribute", "历史紫卡属性编码"),
        REFERENCE_MARKET_CATALOG("reference_market_catalog", "历史 Market 商品目录"),
        REFERENCE_ENEMY_AGENT("reference_enemy_agent", "敌人生成定义"),
        REFERENCE_ENEMY_AVATAR("reference_enemy_avatar", "敌人资料"),
        REFERENCE_ENEMY_DAMAGE_CONTROLLER("reference_enemy_damage_controller", "敌人伤害控制"),
        REFERENCE_ENEMY_DROPTABLE("reference_enemy_droptable", "敌人掉落表"),
        REFERENCE_ENEMY_HIT_PROXY("reference_enemy_hit_proxy", "敌人命中定义"),
        REFERENCE_ENEMY_AI_WEAPON("reference_enemy_ai_weapon", "敌人 AI 武器"),
        REFERENCE_CODEX_OBJECT("reference_codex_object", "图鉴物件"),
        REFERENCE_CODEX_LORE_FRAGMENT("reference_codex_lore_fragment", "图鉴故事碎片"),
        REFERENCE_CODEX_SONG("reference_codex_song", "图鉴歌曲"),
        REFERENCE_CODEX_FIGHTER_FRAME("reference_codex_fighter_frame", "Frame Fighter 碎片"),
        RULE_INTRINSIC("rule_intrinsic", "内源之力"),
        RULE_INTRINSIC_RANK("rule_intrinsic_rank", "内源之力等级"),
        REFERENCE_ACHIEVEMENT("reference_achievement", "成就定义"),
        REFERENCE_VIRTUAL("reference_virtual", "虚拟实体"),
        RULE_CHALLENGE("rule_challenge", "挑战定义"),
        RULE_SYSTEM("rule_system", "星系定义"),
        RULE_TILESET("rule_tileset", "场景定义"),
        KEY("key", "任务钥匙"),
        BUNDLE("bundle", "礼包"),
        BOOSTER_PACK("booster_pack", "随机礼包"),
        RESOURCE("resource", "资源资料"),
        FISH("fish", "鱼类物品"),
        GEAR("gear", "装备工具"),
        MISC("misc", "杂项物品"),
        QUEST("quest", "任务资料"),
        CUSTOM("custom", "外观资料"),
        ANIMAL("animal", "捕获动物"),
        DRONE("drone", "无人机资料"),
        FLAVOUR("flavour", "展示风格"),
        FUSION_BUNDLE("fusion_bundle", "融合包"),
        BOOSTER("booster", "增幅器"),
        CREDIT_BUNDLE("credit_bundle", "信用点礼包"),
        COMPANION("companion", "同伴资料"),
        WARFRAME("warframe", "战甲资料"),
        ARCHWING("archwing", "Archwing 资料"),
        WEAPON("weapon", "武器资料"),
        ABILITY("ability", "技能定义"),
        MOD("mod", "MOD 与升级资料"),
        ARCANE("arcane", "奥术资料"),
        MOD_SET("mod_set", "MOD 套装"),
        TRANSLATION("translation", "语言字典"),
        ALIAS("alias", "物品别名"),
        REWARD_POOL("reward_pool", "奖励池"),
        RECIPE("recipe", "制作与道场配方"),
        STATE_TRANSLATION("state_translation", "状态翻译"),
        TERM("term", "术语与黑话"),
        ARBITRATION_ESSENCE("arbitration_essence", "仲裁精华规则"),
        FISH_GUIDE("fish_guide", "鱼类攻略"),
        RELIC("relic", "遗物主数据");

        private final String code;
        private final String label;

        /**
         * 构造数据文件，并保存初始化参数。
         *
         * @param code 当前数据集或业务对象的稳定代码。
         * @param label 标签标题。
         */
        Category(String code, String label) {
            this.code = code;
            this.label = label;
        }

        /**
         * 取得当前业务使用的稳定代码。
         *
         * @return 当前业务使用的稳定代码对应的文本。
         */
        public String code() {
            return code;
        }

        /**
         * 读取当前代码对应的展示名称。
         *
         * @return 当前代码对应的展示名称对应的文本。
         */
        public String label() {
            return label;
        }

        /**
         * 将外部输入解析为明确的业务数据对象。
         *
         * @param value 待校验或规范化的文本。
         * @return 数据文件对象。
         */
        public static Category parse(String value) {
            if (value != null) {
                String normalized = value.trim().toLowerCase(Locale.ROOT);
                for (Category dataset : values()) {
                    if (dataset.code.equals(normalized)) return dataset;
                }
            }
            throw new IllegalArgumentException("不支持的资料分类：" + value);
        }
    }
    /**
     * 列出允许查询组件和掉落关系的官方资料类型。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<Category> relationParents() {
        return List.of(Category.WEAPON, Category.WARFRAME, Category.ARCHWING, Category.COMPANION,
                Category.RESOURCE, Category.BUNDLE, Category.RECIPE);
    }

}
