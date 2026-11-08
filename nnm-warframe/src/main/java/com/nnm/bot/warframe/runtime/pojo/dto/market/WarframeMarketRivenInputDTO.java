package com.nnm.bot.warframe.runtime.pojo.dto.market;

import java.util.List;

/** 紫卡市场命令的词段和分页参数；具体词条匹配依赖实时目录。 */
public record WarframeMarketRivenInputDTO(List<String> tokens, int end, int page, String error) {
    public WarframeMarketRivenInputDTO {
        tokens = List.copyOf(tokens);
    }
}
