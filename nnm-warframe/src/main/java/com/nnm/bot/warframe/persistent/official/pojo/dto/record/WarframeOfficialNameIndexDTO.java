package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

import com.nnm.bot.warframe.config.cache.DatasetSnapshotMetrics;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 单个官方主表的轻量不可变索引，不包含社区别名或业务详情。 */
public record WarframeOfficialNameIndexDTO(Map<String, WarframeOfficialNameDTO> items,
    Map<String, List<String>> names) implements DatasetSnapshotMetrics {
    /** @return 本数据集快照拥有的业务记录数。 */
    @Override
    public long entryCount() {
        return items.size();
    }

    /**
     * 各领域独立保留同名对象的身份，键比较忽略大小写。
     *
     * @param domainCode 独立领域代码。
     * @param bkey 稳定业务键。
     * @return 名称索引内的确定身份。
     */
    public static String identity(String domainCode, String bkey) {
        return domainCode + ":" + bkey.toLowerCase(Locale.ROOT);
    }
}
