package com.nnm.bot.warframe.plugin.runtime.worldstate;

import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import java.util.Arrays;
import java.util.Locale;

/** 希图斯赏金命令。 */
@BotPluginCommand(code = WarframeCommandCodes.OSTRONS, name = "希图斯", gameCode = "warframe", order = 53,
        allowRelatedAction = true)
public class OstronsPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造Ostron集团插件，并保存初始化参数。
     */
    public OstronsPlugin() {
        super(state -> WarframeWorldStateProjector.syndicate(state, "希图斯",
                        value -> contains(value, "ostrons", "cetus", "希图斯")));
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
