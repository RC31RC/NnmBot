package com.nnm.bot.warframe.persistent.curated.constant;

/** 限定用途的展示词条，不参与物品名称跳板或全局子串替换。 */
public enum WarframeCommunityTermGroup {
    ARCHIMEDEA("archimedea"), BOUNTY_STAGE("bounty_stage"), SORTIE_MODIFIER("sortie_modifier");

    private final String code;

    /**
     * 保存社区资料中的分组编码。
     *
     * @param code 稳定分组编码。
     */
    WarframeCommunityTermGroup(String code) {
        this.code = code;
    }

    /** @return 社区术语存储使用的稳定编码。 */
    public String code() {
        return code;
    }

    /**
     * 判断术语是否只能在限定展示场景使用。
     *
     * @param code 现有人工资料的分组编码。
     * @return 内置展示分组时为真，普通人工分组不受影响。
     */
    public static boolean displayOnly(String code) {
        for (var group : values()) {
            if (group.code.equals(code))
                return true;
        }
        return false;
    }
}
