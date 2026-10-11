package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.NEWS, name = "新闻",
        gameCode = "warframe", order = 65)
// 官方快照 Events/News 的新闻正文与链接，不落表；参考 warframe-info-api 的 news 入口。
public class NewsPlugin extends AbstractWorldStateCommandPlugin {
    public NewsPlugin() {
        super(WarframeCommandData::news);
    }
}
