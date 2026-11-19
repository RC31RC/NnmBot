package com.nnm.bot.warframe.runtime.constant;

/** 仅包含 bounty-cycle 动态补充协议已确认的集团与友军身份。 */
public final class WarframeBountyCycleConstants {
    private WarframeBountyCycleConstants() {}

    /** oracle 当前提供任务明细的集团，不把其他集团伪装成已补充。 */
    public enum Syndicate {
        HEX("HexSyndicate", "bounty-cycle-hex", "1999赏金"), ZARIMAN("ZarimanSyndicate", "bounty-cycle-zariman", "扎里曼赏金"),
        ENTRATI_LAB("EntratiLabSyndicate", "bounty-cycle-cavia", "科维兽赏金");

        private final String code;
        private final String sectionKey;
        private final String title;

        Syndicate(String code, String sectionKey, String title) {
            this.code = code;
            this.sectionKey = sectionKey;
            this.title = title;
        }

        /** @param code 官方集团代码；@return 属于当前集团时为真。 */
        public boolean matches(String code) {
            return this.code.equals(code);
        }

        /** @return 管理端独立赏金分区的稳定编码。 */
        public String sectionKey() {
            return sectionKey;
        }

        /** @return 当前地区赏金的中文标题。 */
        public String title() {
            return title;
        }

        /** @param code 官方集团代码；@return 已支持的集团或空。 */
        public static Syndicate fromCode(String code) {
            for (Syndicate value : values())
                if (value.matches(code))
                    return value;
            return null;
        }

        /** @param code 官方集团代码；@return oracle 已支持时为真。 */
        public static boolean supports(String code) {
            return fromCode(code) != null;
        }
    }

    /** 25 号参考解析中可核实的 Hex 友军；未知路径保留原值。 */
    public enum Ally {
        ARTHUR("ArthurAllyAgent", "亚瑟"), LETTIE("LettieAllyAgent", "莱蒂"), AMIR("AmirAllyAgent", "阿米尔"),
        AOI("AoiAllyAgent", "碧"), ELEANOR("EleanorAllyAgent", "埃莉诺"), QUINCY("QuincyAllyAgent", "昆西");

        private static final String PREFIX = "/Lotus/Types/Gameplay/1999Wf/ProtoframeAllies/";
        private final String key;
        private final String displayName;

        Ally(String key, String displayName) {
            this.key = PREFIX + key;
            this.displayName = displayName;
        }

        /** @param key 上游完整友军路径；@return 唯一已知身份或空。 */
        public static Ally fromKey(String key) {
            for (Ally value : values())
                if (value.key.equals(key))
                    return value;
            return null;
        }

        /** @return 已确认的中文名称。 */
        public String displayName() {
            return displayName;
        }
    }
}
