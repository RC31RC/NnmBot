package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 时光科研命令。 */
@BotPluginCommand(code = WarframeCommandCodes.HEX_ARCHIMEDEAS, name = "时光科研", gameCode = "warframe", order = 60,
        allowRelatedAction = true)
// 数据源：官方 WorldStateDTO.Conquests，按 CT_HEX 过滤；当前不落业务表，原始科研条件保留在 JVM 快照。
// WorldStateDTO 代码簿/补充数据用于解析节点、任务类型和未知术语；参考 warframe-worldstate-parser、warframe-info-api。
public class HexArchimedeasPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造六人组科研插件，并保存初始化参数。
     */
    public HexArchimedeasPlugin() {
        super(state -> WarframeWorldStateProjector.archimedeas(state, "时光科研",
                conquest -> "CT_HEX".equalsIgnoreCase(normalize(conquest.typeKey()))));
    }

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "");
    }
}
