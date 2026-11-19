package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import java.util.List;

/** 管理端世界状态展示的官方／自维护静态详情与图片补全。 */
public interface WarframeWorldStateRecordEnrichmentService {
    /**
     * 按原始身份批量读取物品、星图和科研定义，只补展示字段，不修改运行时或原始快照。
     *
     * @param originals 引用解析前的业务投影。
     * @param displays 已进行名称翻译的投影，顺序与原始投影一致。
     * @return 补充静态上下文、图片与说明后的展示投影。
     */
    List<WarframeWorldStateCommandVO> enrich(List<WarframeWorldStateCommandVO> originals,
        List<WarframeWorldStateCommandVO> displays);
}
