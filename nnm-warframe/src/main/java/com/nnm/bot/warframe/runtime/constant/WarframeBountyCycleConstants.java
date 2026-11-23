package com.nnm.bot.warframe.runtime.constant;

/** bounty-cycle 动态补充协议支持的集团与对应管理分区。 */
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

        /** @return 官方动态数据中的集团代码。 */
        public String code() {
            return code;
        }

        /** @return 管理端独立赏金分区的稳定编码。 */
        public String sectionKey() {
            return sectionKey;
        }

        /** @param sectionKey 管理端分区键；@return 对应集团或空。 */
        public static Syndicate fromSectionKey(String sectionKey) {
            for (Syndicate value : values())
                if (value.sectionKey.equals(sectionKey))
                    return value;
            return null;
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

}
