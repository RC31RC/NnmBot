package com.nnm.bot.warframe.runtime.pojo.dto.market;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

/**
 * Warframe Market 套装接口的结构化快照。
 *
 * <p>套装接口会同时返回根物品和所有部件；保留完整响应便于后续命令使用
 * 尚未建模的上游字段。</p>
 */
public record WarframeMarketItemSetDTO(
        String id,
        List<WarframeMarketItemDTO> items,
        String apiVersion,
        String error,
        String rawJson) {

    /**
     * 构造市场物品集合传输对象，并保存初始化参数。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param items 当前物品或成员列表。
     * @param apiVersion 接口版本。
     * @param error 错误。
     * @param rawJson 用于审核或排障的原始响应文本。
     */
    public WarframeMarketItemSetDTO {
        id = StrUtil.emptyIfNull(id).trim();
        items = CollUtil.isEmpty(items) ? List.of() : List.copyOf(items);
        apiVersion = StrUtil.emptyIfNull(apiVersion).trim();
        error = StrUtil.emptyIfNull(error).trim();
        rawJson = StrUtil.emptyIfNull(rawJson);
    }
}
