package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeArbitrationService;
import com.nnm.bot.warframe.service.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.model.ArbitrationQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ArbitrationCommandHandler {
    @Resource
    private WarframeArbitrationService arbitrationService;
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;

    public CommandResult handle(ArbitrationQuery query) {
        var result = arbitrationService.query(query);
        String title = switch (query) {
            case CURRENT -> "仲裁";
            case TODAY -> "今日仲裁";
            case EFFICIENT -> "高效仲裁";
            case UPCOMING -> "仲裁表";
        };
        var format = DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone(result.zone());
        var rows = result.entries().stream().map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("node", entry.node());
            row.put("missionType", entry.missionType());
            row.put("faction", entry.enemy());
            row.put("activation", format.format(entry.activation()));
            row.put("expiry", format.format(entry.expiry()));
            return row;
        }).toList();
        String notice = result.notice() + (result.fetchedAt() == null ? ""
                : " 数据抓取：" + format.format(result.fetchedAt()) + "。");
        var output = projectionEnricher.enrich(
                new AbstractWorldStateCommandPlugin.WorldStateCommandOutput(title,
                        Map.of("items", rows, "notice", notice)), "arbitration");
        return CommandResult.of(title, WarframeWorldStateDisplayMapper.from(
                "arbitration", output.data(), output.unresolvedReferences()));
    }
}
