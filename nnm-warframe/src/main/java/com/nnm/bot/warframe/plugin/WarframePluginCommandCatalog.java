package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.CommandCatalog;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 将 constant 包的命令定义注册给 Core；本类不维护命令元数据。 */
@Component
public class WarframePluginCommandCatalog implements CommandCatalog {
    /**
     * 返回本模块的业务域编码。
     *
     * @return 稳定业务域编码
     */
    @Override
    public String gameCode() {
        return WarframeCommandCodes.GAME_CODE;
    }

    /**
     * 返回本模块命令定义的不可变编码索引。
     *
     * @return 编码到强类型命令定义的映射
     */
    @Override
    public Map<String, WarframeCommandCodes.Definition> entries() {
        return WarframeCommandCodes.DEFINITIONS;
    }
}
