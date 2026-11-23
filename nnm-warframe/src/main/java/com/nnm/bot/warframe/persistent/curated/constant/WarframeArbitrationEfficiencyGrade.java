package com.nnm.bot.warframe.persistent.curated.constant;

/** 仲裁效率等级；优先保留 arbi.wf.wiki 原评级，缺失等级可按每小时精华估值的六分位推算。 */
public enum WarframeArbitrationEfficiencyGrade {
    F("F", "F 级", 1), E("E", "E 级", 2), D("D", "D 级", 3), C("C", "C 级", 4), B("B", "B 级", 5), A("A", "A 级", 6);

    private final String code;
    private final String label;
    private final int stars;

    /**
     * 定义稳定评级代码及其离散星级。
     *
     * @param code 数据库与接口使用的评级代码。
     * @param label 面向维护者的评级名称。
     * @param stars 从低到高排列的整颗星数量。
     */
    WarframeArbitrationEfficiencyGrade(String code, String label, int stars) {
        this.code = code;
        this.label = label;
        this.stars = stars;
    }

    /** @return 数据库存储和 API 使用的稳定评级代码。 */
    public String code() {
        return code;
    }

    /** @return 面向维护者的评级名称。 */
    public String label() {
        return label;
    }

    /** @return 当前评级对应的整颗星数量。 */
    public int stars() {
        return stars;
    }
}
