package com.nnm.bot.warframe.runtime.constant;

/** 均由类型化快照提供的周期视图标识；标签只用于展示。 */
public enum WarframeWorldCycle {
    EARTH("earth", "地球"), CETUS("cetus", "夜灵平原"), VALLIS("vallis", "福尔图娜"), CAMBION("cambion", "魔胎之境"),
    ZARIMAN("zariman", "扎里曼"), DUVIRI("duviri", "双衍王境");

    private final String code;
    private final String label;

    WarframeWorldCycle(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /** @return 用于资源查找的稳定周期身份。 */
    public String code() {
        return code;
    }

    /** @return 当前展示标签。 */
    public String label() {
        return label;
    }
}
