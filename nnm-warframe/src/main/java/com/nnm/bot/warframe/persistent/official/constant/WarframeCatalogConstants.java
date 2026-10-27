package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class WarframeCatalogConstants {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeCatalogConstants() {
    }

    public enum EnemySection {
        AVATARS(Category.REFERENCE_ENEMY_AVATAR),
        AGENTS(Category.REFERENCE_ENEMY_AGENT),
        DAMAGE_CONTROLLERS(Category.REFERENCE_ENEMY_DAMAGE_CONTROLLER),
        DROPTABLES(Category.REFERENCE_ENEMY_DROPTABLE),
        HIT_PROXIES(Category.REFERENCE_ENEMY_HIT_PROXY),
        AI_WEAPONS(Category.REFERENCE_ENEMY_AI_WEAPON);

        private final Category dataset;

        /**
         * 构造敌人分区，并保存初始化参数。
         *
         * @param dataset 本次操作所属的数据文件或社区资料类型。
         */
        EnemySection(Category dataset) {
            this.dataset = dataset;
        }

        /**
         * 取得当前独立数据文件的领域标识。
         *
         * @return 当前独立 JSON 的领域标识。
         */
        public Category domain() {
            return dataset;
        }
    }

    public enum NightwaveSection {
        CHALLENGES, REWARDS
    }

    public enum VendorSection {
        ITEMS, RANDOM_PRICES
    }
}
