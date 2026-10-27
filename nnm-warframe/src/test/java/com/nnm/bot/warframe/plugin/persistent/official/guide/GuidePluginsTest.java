package com.nnm.bot.warframe.plugin.persistent.official.guide;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FeatureVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FishVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FishTimeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.TimeConditionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.SizeRewardVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.ResourceAmountVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.GeometryVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FeaturePropertiesVO;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO;
import com.nnm.bot.warframe.persistent.official.service.guide.WarframeGuideService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GuidePluginsTest {
    private final WarframeGuideService service = mock(WarframeGuideService.class);
    private final FishGuidePlugin fish = new FishGuidePlugin();
    private final MapFeaturesPlugin map = new MapFeaturesPlugin();

    GuidePluginsTest() {
        ReflectionTestUtils.setField(fish, "guideService", service);
        ReflectionTestUtils.setField(map, "guideService", service);
    }

    @Test
    void rejectsUnknownRepeatedNonTailAndInvalidOptionsBeforeQuerying() {
        for (String query : List.of("--page 0", "--page -1", "--page=x", "--world",
                "--page 1 --page 2", "--world mars", "--world plains --world vallis",
                "--page=1 trailing", "--unknown value", "x".repeat(201))) {
            assertThat(fish.handle(invocation(query)).successful()).isFalse();
            assertThat(map.handle(invocation(query)).successful()).isFalse();
        }
        assertThat(fish.handle(invocation("--layer cave")).successful()).isFalse();
        assertThat(map.handle(invocation("--layer ../cave")).successful()).isFalse();
        verifyNoInteractions(service);
    }

    @Test
    void tailFiltersNumericKeywordsAndHugePagesRemainDistinct() {
        when(service.fish("123", "deimos", 2)).thenReturn(
                WarframeGuideVO.page("123", "deimos", null, List.of(), 2));
        assertThat(CommandReplyTestData.text(fish.handle(invocation("123 --page=2 --world deimos")))).contains("没有找到");
        verify(service).fish("123", "deimos", 2);
        when(service.map("", "plains", "cave", Integer.MAX_VALUE)).thenReturn(
                WarframeGuideVO.page("", "plains", "cave", List.of(), Integer.MAX_VALUE));
        assertThat(map.handle(invocation("--world=plains --layer cave --page 999999999999999")).successful()).isTrue();
        verify(service).map("", "plains", "cave", Integer.MAX_VALUE);
    }

    @Test
    void fishTextPreservesZeroUnknownMaterialsToolsAndSeparateUnits() {
        FishVO item = new WarframeGuideVO.FishVO("Test FishVO", "deimos", "Cave", null, null,
                new FishTimeVO("Vome", List.of(new TimeConditionVO("vome", true, true))), List.of(),
                List.of("Material A", "Material B"), List.of(
                new SizeRewardVO("small", List.of(new ResourceAmountVO("A", BigDecimal.ZERO)), BigDecimal.ZERO),
                new SizeRewardVO("large", List.of(new ResourceAmountVO("A", BigDecimal.valueOf(3))), null)),
                null, "9.0", "source", "SOURCE_SELECTED");
        when(service.fish("", null, 1)).thenReturn(WarframeGuideVO.page("", null, null, List.of(item), 1));
        CommandResult result = fish.handle(invocation(""));
        assertThat(CommandReplyTestData.data(result)).isInstanceOf(WarframeGuideVO.class);
        assertThat(CommandReplyTestData.text(result)).contains("魔胎之境", "偏好", "Material A、Material B", "A × 0", "声望：0",
                "声望：未提供", "鱼饵：未提供", "鱼叉：未提供", "最大评分：9.0")
                .doesNotContain("最大重量", "null", "SOURCE_SELECTED");
    }

    @Test
    void mapTextKeepsImageCoordinatesNullGeometryAndPolygon() {
        FeatureVO point = new WarframeGuideVO.FeatureVO("Marker", "plains", "cave", null,
                new GeometryVO("Point", List.of(new BigDecimal("1024.25"), new BigDecimal("-640.125")), 0),
                new FeaturePropertiesVO("Point description"), "source", "SOURCE_SELECTED");
        FeatureVO missing = new WarframeGuideVO.FeatureVO(null, "vallis", "labels", null, null,
                new FeaturePropertiesVO(null), "source", "SOURCE_SELECTED");
        FeatureVO polygon = new WarframeGuideVO.FeatureVO("Area", "deimos", "cave", null,
                new GeometryVO("Polygon", List.of(), 1), new FeaturePropertiesVO(null), "source", "SOURCE_SELECTED");
        when(service.map("", null, null, 1)).thenReturn(
                WarframeGuideVO.page("", null, null, List.of(point, missing, polygon), 1));
        assertThat(CommandReplyTestData.text(map.handle(invocation("")))).contains("不是经纬度", "1024.25 / -640.125",
                "Point description", "未命名标注", "位置未提供", "1 个边界环")
                .doesNotContain("null", "SOURCE_SELECTED");
    }

    private CommandInvocation invocation(String query) {
        return new CommandInvocation(mock(BotMessageContext.class), "guide", "guide", query);
    }
}
