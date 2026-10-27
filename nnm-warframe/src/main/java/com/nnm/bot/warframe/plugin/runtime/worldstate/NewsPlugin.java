package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.NEWS, name = "新闻",
        gameCode = "warframe", order = 65,
        allowRelatedAction = true)
// 官方快照 Events/News 的新闻正文与链接，不落表；参考 warframe-info-api 的 news 入口。
public class NewsPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造新闻插件，并保存初始化参数。
     */
    public NewsPlugin() {
        super(WarframeWorldStateProjector::news);
    }
}
