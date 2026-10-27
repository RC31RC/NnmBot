package com.nnm.bot;

import java.awt.image.BufferedImage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.concurrent.BoundedTaskExecutor;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.render.config.RenderProperties;
import com.nnm.bot.render.engine.OpenHtmlToPdfEngine;
import com.nnm.bot.render.impl.DefaultImageService;
import com.nnm.bot.render.impl.ThymeleafTemplateRenderer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class RelicTemplateTest {
    @Test
    void rendersSerializedRelicIncludingLocationsAndAbsentMetadata() throws Exception {
        WarframeRelicDetailsDTO detail = detail(6, 4);
        WarframeRelicVO display = WarframeRelicVO.relics("Lith A1", List.of(detail), 1);
        String html = render("wf-relics-compact-v1", display);
        assertThat(html).contains("Lith A1", "入库状态未提供", "Hepit 3", "0.00%", "未提供", "1/1")
                .doesNotContain("${", "th:each", "null", "/Lotus/", "currentJson", "parentBkey");
        assertSinglePdfPage(html, "Hepit 3", display.pagination());
        preview("relic", html);
    }

    @Test
    void rendersEmptyAndMetadataOnlyPagesWithoutInventingChildren() throws Exception {
        WarframeRelicVO metadata = WarframeRelicVO.relics("", List.of(detail(0, 0)), 1);
        String html = render("wf-relics-compact-v1", metadata);
        assertThat(html).contains("0 奖励 / 0 地点", "入库状态未提供")
                .doesNotContain("<table", "0.00%", "null");
        String empty = render("wf-relics-compact-v1", WarframeRelicVO.relics("", List.of(), 1));
        assertThat(empty).contains("没有找到匹配的遗物", "共 0 个遗物").doesNotContain("null", "${");
        String emptyReward = render("wf-gold-trash-compact-v1",
                WarframeRelicVO.rewards("", List.of(), "Rare", 1));
        assertThat(emptyReward).contains("没有找到匹配的遗物奖励").doesNotContain("null", "${");
        preview("relic-empty", empty);
    }

    @Test
    void rendersGoldAndSilverWithParentNamesAndWithoutInventedPrices() throws Exception {
        List<WarframeRelicDetailsDTO> records = IntStream.range(0, 8).mapToObj(index -> {
            return new WarframeRelicDetailsDTO(relic("Lith A" + index, null),
                    List.of(new WarframeRelicDetailsDTO.RewardDTO("Forma Blueprint", index == 0 ? "UNCOMMON" : "RARE", BigDecimal.ZERO)), List.of());
        }).toList();
        WarframeRelicVO gold = WarframeRelicVO.rewards("", records, "Rare", 1);
        String html = render("wf-gold-trash-compact-v1", gold);
        assertThat(html).contains("稀有奖励", "Lith A1", "Lith A7", "Forma Blueprint", "0.00%")
                .doesNotContain("Lith A0", "白金", "价格", "null", "${");
        assertSinglePdfPage(html, "Lith A7", gold.pagination());
        preview("gold", html);
        String silver = render("wf-gold-trash-compact-v1", WarframeRelicVO.rewards("", records, "Uncommon", 1));
        assertThat(silver).contains("罕见奖励", "Lith A0").doesNotContain("Lith A1");
        preview("silver", silver);
    }

    @Test
    void rendersMissingRewardFieldsWithoutTreatingThemAsZero() throws Exception {
        WarframeRelicDetailsDTO detail = new WarframeRelicDetailsDTO(relic("Lith A1", false),
                List.of(new WarframeRelicDetailsDTO.RewardDTO("Prime Blueprint 0", null, null)),
                List.of(new WarframeRelicDetailsDTO.DropDTO(null, null, new BigDecimal("2.5"))));
        String html = render("wf-relics-compact-v1", WarframeRelicVO.relics("", List.of(detail), 1));
        assertThat(html).contains("未入库", "未提供", "地点未提供", "2.50%")
                .doesNotContain("0.00%", "null", "${");

        detail = new WarframeRelicDetailsDTO(detail.relic(),
                List.of(new WarframeRelicDetailsDTO.RewardDTO("Prime Blueprint 0", "Rare", null)), detail.drops());
        WarframeRelicVO gold = WarframeRelicVO.rewards("", List.of(detail), "Rare", 1);
        String rewardHtml = render("wf-gold-trash-compact-v1", gold);
        assertThat(rewardHtml).contains("稀有奖励", "未提供").doesNotContain("0.00%", "null", "${");
        assertSinglePdfPage(rewardHtml, "Prime Blueprint 0", gold.pagination());
    }

    @Test
    void allLongRewardPagesRetainParentNamesAndFitThePdfCanvas() throws Exception {
        WarframeRelicDetailsDTO detail = new WarframeRelicDetailsDTO(relic("长遗物名称".repeat(35) + " Lith A1", null),
                IntStream.range(0, 8).mapToObj(index -> new WarframeRelicDetailsDTO.RewardDTO(
                        "长物品名称".repeat(65) + " Blueprint", "Rare", BigDecimal.ZERO)).toList(), List.of());
        int count = 0;
        for (int page = 1; ; page++) {
            WarframeRelicVO display = WarframeRelicVO.rewards("长物品名称".repeat(35), List.of(detail), "Rare", page);
            if (display.rewards().isEmpty()) break;
            String html = render("wf-gold-trash-compact-v1", display);
            assertThat(html).contains(detail.relic().relicName());
            assertSinglePdfPage(html, "Lith A1", display.pagination());
            count += display.rewards().size();
            if (page == 1) preview("gold-long", html);
        }
        assertThat(count).isEqualTo(detail.rewards().size());
    }

    @Test
    void allLongTextPagesKeepTheLastRowAndFooterInsideOnePdfPage() throws Exception {
        WarframeRelicDetailsDTO detail = new WarframeRelicDetailsDTO(relic("长名称".repeat(60) + "Lith A1", null),
                IntStream.range(0, 6).mapToObj(index -> new WarframeRelicDetailsDTO.RewardDTO(
                        "长物品名称".repeat(50) + " Blueprint", index == 0 ? "Rare" : "Common", BigDecimal.ZERO)).toList(),
                IntStream.range(0, 12).mapToObj(index -> new WarframeRelicDetailsDTO.DropDTO(
                        "长任务地点".repeat(130) + " SentinelEnd" + index, null, null)).toList());
        List<String> locations = new ArrayList<>();
        boolean locationPreviewWritten = false;
        for (int page = 1; ; page++) {
            WarframeRelicVO display = WarframeRelicVO.relics("查询名称".repeat(45), List.of(detail), page);
            if (display.items().isEmpty()) break;
            String html = render("wf-relics-compact-v1", display);
            String sentinel = display.items().getFirst().drops().isEmpty() ? "Blueprint"
                    : display.items().getFirst().drops().getLast().location().replaceAll(".* (SentinelEnd\\d+)$", "$1");
            assertSinglePdfPage(html, sentinel, display.pagination());
            locations.addAll(display.items().getFirst().drops().stream()
                    .map(WarframeRelicVO.DropVO::location).toList());
            if (page == 4) preview("relic-long", html);
            if (!locationPreviewWritten && !display.items().getFirst().drops().isEmpty()) {
                preview("relic-long-location", html);
                locationPreviewWritten = true;
            }
        }
        assertThat(locations).containsExactlyElementsOf(detail.drops().stream()
                .map(WarframeRelicDetailsDTO.DropDTO::locationText).toList());
    }

    @Test
    void usesExistingImageServiceAndEscapesUntrustedNames() throws Exception {
        WarframeRelicDetailsDTO detail = new WarframeRelicDetailsDTO(relic("Lith A1", null), List.of(
                new WarframeRelicDetailsDTO.RewardDTO("<script>alert(1)</script> & Blueprint", "Rare", BigDecimal.ZERO)), List.of());
        WarframeRelicVO display = WarframeRelicVO.relics("", List.of(detail), 1);
        String html = render("wf-relics-compact-v1", display);
        assertThat(html).contains("&lt;script&gt;", "&amp;").doesNotContain("<script>");
        DefaultImageService service = new DefaultImageService();
        configureImageService(service);
        ReflectionTestUtils.setField(service, "templateRenderer", renderer());
        ReflectionTestUtils.setField(service, "renderEngine",
                new OpenHtmlToPdfEngine(null, fontPaths()));
        byte[] png = service.generateHtml(template("wf-relics-compact-v1"), variables(display),
                RenderOptions.builder().dpi(72).build());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
        assertThat(image.getWidth()).isGreaterThan(300);
        assertThat(image.getHeight()).isGreaterThan(300);
        boolean ink = false;
        for (int y = 0; y < image.getHeight() && !ink; y += 4) {
            for (int x = 0; x < image.getWidth(); x += 4) {
                if ((image.getRGB(x, y) & 0xFFFFFF) != 0xFFFFFF) {
                    ink = true;
                    break;
                }
            }
        }
        assertThat(ink).isTrue();
    }

    private void configureImageService(DefaultImageService service) {
        ReflectionTestUtils.setField(service, "properties", new RenderProperties());
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper().findAndRegisterModules());
        ReflectionTestUtils.setField(service, "taskExecutor", new BoundedTaskExecutor(
                new TaskExecutorAdapter(Runnable::run), 6,
                Duration.ofSeconds(5), Duration.ofSeconds(30)));
    }

    private void assertSinglePdfPage(String html, String sentinel, String pagination) throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            for (Path font : fontPaths()) builder.useFont(font.toFile(), "Noto Sans SC");
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            try (PDDocument document = PDDocument.load(output.toByteArray())) {
                assertThat(document.getNumberOfPages()).isEqualTo(1);
                assertThat(new PDFTextStripper().getText(document))
                        .contains(sentinel, pagination.substring(0, pagination.indexOf(' ')));
            }
        }
    }

    private String render(String label, WarframeRelicVO display) throws Exception {
        return renderer().renderHtml(template(label), variables(display));
    }

    private ThymeleafTemplateRenderer renderer() {
        ThymeleafTemplateRenderer renderer = new ThymeleafTemplateRenderer();
        ReflectionTestUtils.setField(renderer, "templateEngine", new SpringTemplateEngine());
        ReflectionTestUtils.invokeMethod(renderer, "initialize");
        return renderer;
    }

    private Map<String, Object> variables(WarframeRelicVO display) {
        return new ObjectMapper().convertValue(display, new TypeReference<>() {});
    }

    private String template(String templateCode) throws Exception {
        return SqlTemplateFixtures.template(templateCode);
    }

    private List<Path> fontPaths() {
        String font = System.getProperty("nnm.template.font", "");
        return font.isBlank() ? List.of() : List.of(Path.of(font));
    }

    private void preview(String name, String html) throws Exception {
        String directory = System.getProperty("nnm.template.preview-directory", "");
        if (directory.isBlank()) return;
        Path target = Files.createDirectories(Path.of(directory));
        Files.writeString(target.resolve(name + ".html"), html);
        Files.write(target.resolve(name + "-pdf.png"),
                new OpenHtmlToPdfEngine(null, fontPaths()).renderHtmlToImage(html,
                        RenderOptions.builder().dpi(96).build()));
    }

    private WarframeRelicDetailsDTO detail(int rewards, int drops) {
        return new WarframeRelicDetailsDTO(relic("Lith A1", null),
                IntStream.range(0, rewards).mapToObj(index -> new WarframeRelicDetailsDTO.RewardDTO(
                        "Prime Blueprint " + index, index == 0 ? "Rare" : "Common", BigDecimal.ZERO)).toList(),
                IntStream.range(0, drops).mapToObj(index -> new WarframeRelicDetailsDTO.DropDTO("Hepit " + index, null, null)).toList());
    }

    private WarframeRelicDetailsDTO.RelicDTO relic(String name, Boolean vaulted) {
        return new WarframeRelicDetailsDTO.RelicDTO("/Lotus/Types/Relics/LithA1", name, vaulted, null, null, null, "NNM_DATASET");
    }
}
