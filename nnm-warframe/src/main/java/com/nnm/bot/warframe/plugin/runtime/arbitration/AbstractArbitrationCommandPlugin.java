package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeViewKeys;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateDisplayMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import jakarta.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 四种仲裁排期视图共用命令处理、数据来源与展示合同。 */
public abstract class AbstractArbitrationCommandPlugin extends BotPlugin<Void, Void> {

    private final QueryType query;
    @Resource
    private WarframeArbitrationService arbitrationService;
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;

    /**
     * 构造基础仲裁命令插件，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     */
    protected AbstractArbitrationCommandPlugin(QueryType query) {
        this.query = query;
    }

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, Void parsed) {
        return view();
    }

    /**
     * 生成当前仲裁命令的业务视图，供命令处理与图片预热共用。
     *
     * @return 仲裁排期的展示结果。
     */
    public CommandResult view() {
        ArbitrationScheduleDTO result = arbitrationService.query(query);
        String title = switch (query) {
            case CURRENT -> "仲裁";
            case TODAY -> "今日仲裁";
            case EFFICIENT -> "高效仲裁";
            case UPCOMING -> "仲裁表";
        };
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone(result.zone());
        List<Map<String, Object>> rows = result.entries().stream().map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("node", entry.node());
            row.put("missionType", entry.missionType());
            row.put("faction", entry.enemy());
            row.put("activation", format.format(entry.activation()));
            row.put("expiry", format.format(entry.expiry()));
            return row;
        }).toList();
        String notice =
            result.notice() + (result.fetchedAt() == null ? "" : " 数据存档：" + format.format(result.fetchedAt()) + "。");
        WarframeWorldStateCommandVO output = projectionEnricher
            .enrich(new WarframeWorldStateCommandVO(title, Map.of("items", rows, "notice", notice)), "arbitration");
        return CommandResult.html(title,
            WarframeWorldStateDisplayMapper.from("arbitration", output.data(), output.unresolvedReferences()));
    }
}
