package com.nnm.bot.warframe.plugin.runtime.worldstate;

import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import java.util.Arrays;
import java.util.Locale;

/** 索拉里斯联合赏金命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SOLARIS, name = "索拉里斯", gameCode = "warframe", order = 55,
        allowRelatedAction = true)
public class SolarisPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造索拉里斯插件，并保存初始化参数。
     */
    public SolarisPlugin() {
        super(state -> WarframeWorldStateProjector.syndicate(state, "索拉里斯",
                        value -> contains(value, "solaris", "vallis", "索拉里斯")));
    }

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param value 待校验或规范化的文本。
     * @param candidates 候选。
     * @return 满足文本或集合是否包含指定内容时为真，否则为假。
     */
    private static boolean contains(String value, String... candidates) {
        String normalized = ObjectUtil.isNull(value) ? "" : value.toLowerCase(Locale.ROOT);
        return Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
