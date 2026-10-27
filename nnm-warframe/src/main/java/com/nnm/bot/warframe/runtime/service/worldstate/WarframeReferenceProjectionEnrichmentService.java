package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import java.util.Collection;
import java.util.List;

public interface WarframeReferenceProjectionEnrichmentService {
    /**
     * 结合已采纳静态资料补全运行时数据。
     *
     * @param output 需要补全或转换的输出数据。
     * @return 当前命令的标题、状态及展示条目。
     */
    WarframeWorldStateCommandVO enrich(WarframeWorldStateCommandVO output);

    /**
     * 结合已采纳静态资料补全运行时数据。
     *
     * @param output 需要补全或转换的输出数据。
     * @param commandCode 命令代码。
     * @return 当前命令的标题、状态及展示条目。
     */
    WarframeWorldStateCommandVO enrich(
            WarframeWorldStateCommandVO output, String commandCode);

    /**
     * 批量收集展示引用后统一解析，并将本地化结果补入各个命令投影。
     * @param outputs 输出集合。
     * @return 当前命令的标题、状态及展示条目组成的列表。
     */
    List<WarframeWorldStateCommandVO> enrichAll(
            Collection<WarframeWorldStateCommandVO> outputs);

    /**
     * 批量收集展示引用后统一解析，并将本地化结果补入各个命令投影。
     * @param outputs 输出集合。
     * @param commandCode 命令代码。
     * @return 当前命令的标题、状态及展示条目组成的列表。
     */
    List<WarframeWorldStateCommandVO> enrichAll(
            Collection<WarframeWorldStateCommandVO> outputs, String commandCode);
}
