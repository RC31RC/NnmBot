package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import java.util.List;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeViewKeys;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateDisplayMapper;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** 四个仲裁命令共用的展示入口；仅消费已采纳排期，不触发上游刷新。 */
@Component
public class ArbitrationCommandHandler {
    /** 按准确时间窗口查询数据库投影，并复用有界本地缓存。 */
    @Resource
    private WarframeArbitrationService arbitrationService;
    /** 为展示补充已采纳名称及精华参考，不生成或补造排期。 */
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;

    /**
     * 格式化查询视图，存档时间缺失时不伪造抓取或刷新时间。
     *
     * @param query 当前业务的查询条件。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    public CommandResult handle(QueryType query) {
        ArbitrationScheduleDTO result = arbitrationService.query(query);
        String title = switch (query) {
            case CURRENT -> "仲裁";
            case TODAY -> "今日仲裁";
            case EFFICIENT -> "高效仲裁";
            case UPCOMING -> "仲裁表";
        };
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone(result.zone());
        List<Map<String,Object>> rows = result.entries().stream().map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("node", entry.node());
            row.put("missionType", entry.missionType());
            row.put("faction", entry.enemy());
            row.put("activation", format.format(entry.activation()));
            row.put("expiry", format.format(entry.expiry()));
            return row;
        }).toList();
        String notice = result.notice() + (result.fetchedAt() == null ? ""
                : " 数据存档：" + format.format(result.fetchedAt()) + "。");
        WarframeWorldStateCommandVO output = projectionEnricher.enrich(
                new WarframeWorldStateCommandVO(title,
                        Map.of("items", rows, "notice", notice)), "arbitration");
        return CommandResult.view(WarframeViewKeys.WORLD_STATE, title, WarframeWorldStateDisplayMapper.from(
                "arbitration", output.data(), output.unresolvedReferences()));
    }
}
