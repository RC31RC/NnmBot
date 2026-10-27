package com.nnm.bot.warframe.persistent.official.parser.input;

import cn.hutool.core.util.StrUtil;

/** 官方输入的名称回退顺序；真正的联合类型仅接受其中的文本分支。 */
public final class OfficialInputText {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private OfficialInputText() {
    }

    /**
     * 按名称回退顺序取得首个非空白文本，仅接受文本或明确的数值分支。
     *
     * @param values 按优先顺序排列的候选名称或业务键。
     * @return 首个有效文本；全部缺省或类型不符时返回空值。
     */
    public static String first(Object... values) {
        for (Object value : values) {
            String text = value instanceof String string ? string : value instanceof Number number ? number.toString() : null;
            if (StrUtil.isNotBlank(text)) return text;
        }
        return null;
    }
}
