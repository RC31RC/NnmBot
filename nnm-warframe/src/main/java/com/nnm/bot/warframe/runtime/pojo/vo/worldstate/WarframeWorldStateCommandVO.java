package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.UnresolvedReferenceVO;
import java.util.List;
import java.util.Map;

/** 世界状态命令、管理端和订阅监听共用的展示投影。 */
public record WarframeWorldStateCommandVO(
        String text,
        Map<String, Object> data,
        List<UnresolvedReferenceVO> unresolvedReferences) {

    /**
     * 构造世界状态命令展示对象，并保存初始化参数。
     *
     * @param text 待处理的文本。
     * @param data 当前操作的数据。
     */
    public WarframeWorldStateCommandVO(String text, Map<String, Object> data) {
        this(text, data, List.of());
    }

    /**
     * 构造世界状态命令展示对象，并保存初始化参数。
     *
     * @param text 待处理的文本。
     * @param data 当前操作的数据。
     * @param unresolvedReferences 未解析引用集合。
     */
    public WarframeWorldStateCommandVO {
        text = StrUtil.emptyIfNull(text).trim();
        data = MapUtil.isEmpty(data) ? Map.of() : Map.copyOf(data);
        unresolvedReferences = unresolvedReferences == null
                ? List.of() : List.copyOf(unresolvedReferences);
    }
}
