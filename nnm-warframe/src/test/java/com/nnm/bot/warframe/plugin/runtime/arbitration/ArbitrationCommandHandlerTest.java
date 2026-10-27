package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ArbitrationMissionVO;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ArbitrationCommandHandlerTest {
    @Test
    void allViewsUseSharedProjectionAndPreserveSourceFreshnessNotice() {
        WarframeArbitrationService service = mock(WarframeArbitrationService.class);
        WarframeReferenceProjectionEnrichmentService enricher = mock(WarframeReferenceProjectionEnrichmentService.class);
        ArbitrationCommandHandler handler = new ArbitrationCommandHandler();
        ReflectionTestUtils.setField(handler, "arbitrationService", service);
        ReflectionTestUtils.setField(handler, "projectionEnricher", enricher);
        Instant now = Instant.parse("2026-09-12T04:00:00Z");
        when(service.query(any())).thenReturn(new ArbitrationScheduleDTO(
                List.of(new ArbitrationScheduleEntryDTO("id", now, now.plusSeconds(3600),
                        "Seimeni (谷神星)", "防御", "Infestation", "谷神星", "{}")),
                "第三方预测；刷新失败，本地存档。", now, ZoneId.of("Asia/Shanghai")));
        when(enricher.enrich(any(), eq("arbitration"))).thenAnswer(invocation -> invocation.getArgument(0));
        for (QueryType query : QueryType.values()) {
            CommandResult result = handler.handle(query);
            WarframeWorldStateVO display = (WarframeWorldStateVO) CommandReplyTestData.data(result);
            assertThat(display.notice()).contains("第三方预测", "本地存档", "数据存档：09-12 12:00")
                    .doesNotContain("数据抓取");
            assertThat(display.items()).hasSize(1);
            ArbitrationMissionVO row = (WarframeWorldStateVO.ArbitrationMissionVO) display.items().getFirst();
            assertThat(row.activation()).isEqualTo("09-12 12:00");
            assertThat(row.expiry()).isEqualTo("09-12 13:00");
            verify(service).query(query);
        }
    }

    @Test
    void missingArchiveTimestampDoesNotInventFreshness() {
        WarframeArbitrationService service = mock(WarframeArbitrationService.class);
        WarframeReferenceProjectionEnrichmentService enricher = mock(WarframeReferenceProjectionEnrichmentService.class);
        ArbitrationCommandHandler handler = new ArbitrationCommandHandler();
        ReflectionTestUtils.setField(handler, "arbitrationService", service);
        ReflectionTestUtils.setField(handler, "projectionEnricher", enricher);
        when(service.query(QueryType.CURRENT)).thenReturn(new ArbitrationScheduleDTO(
                List.of(), "暂无已采纳排期。", null, ZoneId.of("Asia/Shanghai")));
        when(enricher.enrich(any(), eq("arbitration"))).thenAnswer(invocation -> invocation.getArgument(0));

        WarframeWorldStateVO display = (WarframeWorldStateVO) CommandReplyTestData.data(handler.handle(QueryType.CURRENT));

        assertThat(display.notice()).isEqualTo("暂无已采纳排期。");
        assertThat(display.items()).isEmpty();
    }
}
