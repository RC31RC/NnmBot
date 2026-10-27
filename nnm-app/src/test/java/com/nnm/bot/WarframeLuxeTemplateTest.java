package com.nnm.bot;

import java.util.Map.Entry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.nnm.bot.common.concurrent.BoundedTaskExecutor;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.render.config.RenderProperties;
import com.nnm.bot.render.engine.PlaywrightEngine;
import com.nnm.bot.render.impl.DefaultImageService;
import com.nnm.bot.render.impl.ThymeleafTemplateRenderer;
import com.nnm.bot.warframe.persistent.official.constant.WarframeTranslationKey;
import com.nnm.bot.warframe.runtime.parser.riven.WarframeRivenWeeklyParser;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO.AbilityVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO.MetricVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO.RewardVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WarframeLuxeTemplateTest {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void rendersActualDtosWithZeroAbsentFieldsAndEscapedText() throws Exception {
        String relic = render("wf-relics-luxe-v1", relic("<script>&遗物", null, null));
        assertThat(relic).contains("&lt;script&gt;&amp;遗物", "入库状态未提供", "0.00%", "未提供")
                .doesNotContain("<script>", "${", "th:each", ">null<");
        String equipment = render("wf-warframes-luxe-v1", equipment("<script>&战甲", null, null));
        assertThat(equipment).contains("&lt;script&gt;&amp;战甲", ">1</span>", "描述未提供", ">0<")
                .doesNotContain("<script>", " 能量</span>", "${", ">null<");
        String weekly = render("wf-riven-weekly-luxe-v1", weekly("<script>&武器"));
        assertThat(weekly).contains("&lt;script&gt;&amp;武器", "0.0", "未提供", "相对交易热度")
                .doesNotContain("<script>", "样本量", "${", ">null<");
        assertThat(weekly).contains("2026-10-13 12:55 UTC").doesNotContain("179189");
    }

    @Test
    void rendersEmptyResultsWithoutInventingItems() throws Exception {
        assertThat(render("wf-relics-luxe-v1", new WarframeRelicVO("", List.of(), List.of(),
                "1/1 页，共 0 个遗物", "没有找到匹配的遗物")))
                .contains("没有找到匹配的遗物").doesNotContain("class=\"reward\"");
        assertThat(render("wf-warframes-luxe-v1",
                WarframeEquipmentVO.page("", "战甲", List.of(), 1)))
                .contains("没有找到匹配的装备").doesNotContain("class=\"ability\"");
        assertThat(render("wf-riven-weekly-luxe-v1", new WarframeRivenWeeklyVO("", "pc", "1/1 页",
                "WF_RUNTIME_RIVEN_WEEKLY_OFFICIAL_PC", null, true, List.of(), null)))
                .contains("历史成功快照", "没有找到匹配的周成交记录").doesNotContain("class=\"quote\"");
    }

    @Test
    void bindsEachEquipmentAndAbilityImageToItsOwnEntry() throws Exception {
        List<WarframeEquipmentVO.EntryVO> entries = List.of(
                new WarframeEquipmentVO.EntryVO("/Lotus/First", "First", null, null,
                        "https://browse.wf/first.png", null, "SOURCE_SELECTED", List.of(),
                        List.of(new WarframeEquipmentVO.AbilityVO(1, "/Lotus/First/Skill", "First skill",
                                null, null, null, "https://browse.wf/first-skill.png", null))),
                new WarframeEquipmentVO.EntryVO("/Lotus/Second", "Second", null, null,
                        "https://browse.wf/second.png", null, "SOURCE_SELECTED", List.of(),
                        List.of(new WarframeEquipmentVO.AbilityVO(1, "/Lotus/Second/Skill", "Second skill",
                                null, null, null, "https://browse.wf/second-skill.png", null))));
        String html = render("wf-warframes-luxe-v1",
                WarframeEquipmentVO.page("", "战甲", entries, 1));
        int second = html.indexOf("<article", html.indexOf("</article>"));
        assertThat(second).isPositive();
        assertThat(html.substring(0, second)).contains("https://browse.wf/first.png",
                "https://browse.wf/first-skill.png", "First skill")
                .doesNotContain("https://browse.wf/second");
        assertThat(html.substring(second)).contains("https://browse.wf/second.png",
                "https://browse.wf/second-skill.png", "Second skill")
                .doesNotContain("https://browse.wf/first");
        assertThat(html).doesNotContain("templateAssets", "wispScene", "scene-hero");
    }

    @Test
    void usesDtoIconForRelicBackgroundAndOmitsMissingImages() throws Exception {
        String html = render("wf-relics-luxe-v1",
                relic("遗物", "https://browse.wf/relic.png", null));
        assertThat(html).containsPattern("class=\"scene\"[^>]*src=\"https://browse.wf/relic.png\"")
                .containsPattern("class=\"backdrop\"[^>]*src=\"https://browse.wf/relic.png\"")
                .containsPattern("class=\"art\"[^>]*src=\"https://browse.wf/relic.png\"");
        assertThat(render("wf-relics-luxe-v1", relic("遗物", null, null))).doesNotContain("<img");
        assertThat(render("wf-warframes-luxe-v1", equipment("战甲", null, null)))
                .doesNotContain("<img");
        assertThat(render("wf-riven-weekly-luxe-v1", weekly("武器")))
                .doesNotContain("<img", "templateAssets");
    }

    @Test
    void checksDtoImagesAndLongTextAcrossBrowserWidths() throws Exception {
        assumeTrue(Boolean.getBoolean("nnm.template.browser-check"),
                "Browser rendering is opt-in");
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        String icon = "data:image/png;base64," + Base64.getEncoder().encodeToString(output.toByteArray());
        String longText = "非常长的名称和文本".repeat(10) + "UnbrokenText".repeat(15);
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
            for (Sample sample : List.of(
                    new Sample("wf-relics-luxe-v1", relic(longText, icon, longText)),
                    new Sample("wf-warframes-luxe-v1", equipment(longText, icon, longText)),
                    new Sample("wf-riven-weekly-luxe-v1", weekly(longText)))) {
                for (int width : List.of(360, 540, 840)) {
                    assertLayout(browser, render(sample.label(), sample.display()), width, true);
                }
            }
        }
    }

    @Test
    void importsCompleteReferenceTranslationsIncludingSharedWispAbilities() throws Exception {
        String directory = System.getProperty("nnm.warframe.reference-directory");
        assumeTrue(directory != null, "Local reference corpus is opt-in");
        Map<String,String> translations = translations(Path.of(directory));
        assertThat(translations.get("item-name:/Lotus/Powersuits/Wisp/Abilities/WispReservoirAbility"))
                .isEqualTo("储能池");
        assertThat(translations.get("item-description:/Lotus/Powersuits/Wisp/Abilities/WispSunAbility"))
                .contains("传送门");
    }

    @Test
    void generatesThreeOfflineSamplesThroughExistingRenderChainAndChecksBrowserLayout() throws Exception {
        String directory = System.getProperty("nnm.template.luxe-preview-directory");
        String references = System.getProperty("nnm.warframe.reference-directory");
        String weeklySnapshot = System.getProperty("nnm.warframe.weekly-reference");
        assumeTrue(directory != null && references != null && weeklySnapshot != null,
                "Browser previews and offline reference data are opt-in");
        Path target = Files.createDirectories(Path.of(directory));
        Path corpus = Path.of(references);
        Function<String, String> asset = name -> {
            try {
                Path file = target.resolve("assets").resolve(name);
                String type = name.endsWith(".jpg") ? "image/jpeg" : "image/png";
                return "data:" + type + ";base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(file));
            } catch (Exception exception) {
                throw new IllegalStateException("Cannot load preview asset: " + name, exception);
            }
        };
        Map<String, String> translations = translations(corpus);
        Map<String, Sample> samples = new LinkedHashMap<>();
        samples.put("01-relic", new Sample("wf-relics-luxe-v1", referenceRelic(corpus, translations, asset)));
        samples.put("02-warframe", new Sample("wf-warframes-luxe-v1",
                referenceEquipment(corpus, translations, asset)));
        WarframeRivenWeeklyParser parser = new WarframeRivenWeeklyParser();
        ReflectionTestUtils.setField(parser, "objectMapper", mapper);
        ReflectionTestUtils.invokeMethod(parser, "initialize");
        Path snapshot = Path.of(weeklySnapshot);
        List<WarframeRivenWeeklyVO.EntryVO> quotes = parser.parse(Files.readString(snapshot), 20_000).stream()
                .filter(entry -> entry.compatibility() != null).limit(4).map(value -> new WarframeRivenWeeklyVO.EntryVO(value.compatibility() == null ? value.itemType() : value.compatibility(),
                        value.compatibility(), value.itemType(), value.rerolled(), value.avg(), value.median(), value.min(), value.max(),
                        value.stddev(), value.pop(), null)).toList();
        assertThat(quotes).hasSize(4);
        samples.put("03-riven-weekly", new Sample("wf-riven-weekly-luxe-v1", new WarframeRivenWeeklyVO(
                "", "pc", "1/1 页，预览 4 条", "WF_RUNTIME_RIVEN_WEEKLY_OFFICIAL_PC",
                Files.getLastModifiedTime(snapshot).toInstant(), false, quotes, null)));

        DefaultImageService imageService = new DefaultImageService();
        ReflectionTestUtils.setField(imageService, "properties", new RenderProperties());
        ReflectionTestUtils.setField(imageService, "objectMapper", new ObjectMapper().findAndRegisterModules());
        ReflectionTestUtils.setField(imageService, "taskExecutor", new BoundedTaskExecutor(
                new TaskExecutorAdapter(Runnable::run), 6,
                Duration.ofSeconds(5), Duration.ofSeconds(30)));
        ReflectionTestUtils.setField(imageService, "templateRenderer", renderer());
        ReflectionTestUtils.setField(imageService, "renderEngine", new PlaywrightEngine(List.of()));
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
            for (Entry<String,Sample> entry : samples.entrySet()) {
                String html = render(entry.getValue().label(), entry.getValue().display());
                Files.writeString(target.resolve(entry.getKey() + ".html"), html);
                byte[] png = imageService.generateHtml(template(entry.getValue().label()),
                        variables(entry.getValue().display()), RenderOptions.builder().width(840).build());
                assertNonblank(png);
                Files.write(target.resolve(entry.getKey() + ".png"), png);
                for (int width : List.of(360, 540, 840)) {
                    assertLayout(browser, html, width, true);
                }
            }
            String longText = "非常长的名称和文本".repeat(25) + "UnbrokenText".repeat(30);
            for (Sample sample : List.of(
                    new Sample("wf-relics-luxe-v1", relic(longText, asset.apply("lith-relic.png"), longText)),
                    new Sample("wf-warframes-luxe-v1",
                            equipment(longText, asset.apply("wisp-prime.png"), longText)),
                    new Sample("wf-riven-weekly-luxe-v1", weekly(longText)))) {
                for (int width : List.of(360, 540, 840)) {
                    assertLayout(browser, render(sample.label(), sample.display()), width, true);
                }
            }
        }
    }

    private Map<String, String> translations(Path corpus) throws Exception {
        JsonNode records = mapper.readTree(Files.readString(corpus.resolve("warframe-items/data/json/i18n.json")));
        Map<String, String> result = new java.util.LinkedHashMap<>();
        records.fields().forEachRemaining(entry -> {
            JsonNode zh = entry.getValue().path("zh");
            if (zh.hasNonNull("name")) result.put("item-name:" + entry.getKey(), zh.get("name").asText());
            if (zh.hasNonNull("description")) result.put("item-description:" + entry.getKey(), zh.get("description").asText());
        });
        return result;
    }

    private WarframeRelicVO referenceRelic(Path corpus, Map<String, String> translations,
            Function<String, String> asset) throws Exception {
        JsonNode item = findItem(corpus, "Relics.json",
                "/Lotus/Types/Game/Projections/T1VoidProjectionGPlatinum");
        List<RewardVO> rewards = new ArrayList<>();
        for (JsonNode reward : item.path("rewards")) {
            String name = translated(translations, "item-name:", reward.path("item"));
            String rarity = switch (reward.path("rarity").asText()) {
                case "Rare" -> "稀有";
                case "Uncommon" -> "罕见";
                default -> "常见";
            };
            rewards.add(new WarframeRelicVO.RewardVO(name, rarity,
                    reward.path("chance").decimalValue(), null));
        }
        assertThat(item.path("locations").size()).isZero();
        return new WarframeRelicVO("", List.of(new WarframeRelicVO.RelicVO(item.path("name").asText(),
                item.path("vaulted").booleanValue(), rewards, List.of(), "Radiant",
                "1/1 页，6 奖励 / 0 地点", item.path("imageName").asText(), asset.apply("lith-relic.png"),
                "TEST_FIXTURE", "SOURCE_SELECTED")),
                List.of(), "1/1 页，共 1 个遗物", null);
    }

    private WarframeEquipmentVO referenceEquipment(Path corpus, Map<String, String> translations,
            Function<String, String> asset) throws Exception {
        JsonNode item = findItem(corpus, "Warframes.json", "/Lotus/Powersuits/Wisp/WispPrime");
        List<MetricVO> metrics = new ArrayList<>();
        String[] labels = {"生命", "护盾", "护甲", "能量", "冲刺"};
        String[] fields = {"health", "shield", "armor", "power", "sprint"};
        for (int index = 0; index < fields.length; index++) {
            metrics.add(new WarframeEquipmentVO.MetricVO(labels[index], item.path(fields[index]).decimalValue()));
        }
        String[] images = {"wisp-reservoir.png", "wisp-light.png", "wisp-breach.png", "wisp-sol.png"};
        List<AbilityVO> abilities = new ArrayList<>();
        for (JsonNode ability : item.path("abilities")) {
            abilities.add(new WarframeEquipmentVO.AbilityVO(abilities.size() + 1,
                    ability.path("uniqueName").asText(), translated(translations, "item-name:", ability),
                    translated(translations, "item-description:", ability), null,
                    ability.path("imageName").asText(), asset.apply(images[abilities.size()]),
                    "TEST_FIXTURE"));
        }
        return WarframeEquipmentVO.page("", "战甲", List.of(new WarframeEquipmentVO.EntryVO(
                item.path("uniqueName").asText(), translated(translations, "item-name:", item),
                translated(translations, "item-description:", item), item.path("imageName").asText(),
                asset.apply("wisp-prime.png"), "TEST_FIXTURE",
                "SOURCE_SELECTED", metrics, abilities)), 1);
    }

    private String translated(Map<String, String> translations, String role, JsonNode item) {
        String raw = item.path(role.equals("item-name:") ? "name" : "description").asText();
        String fallback = role.equals("item-name:")
                ? translations.getOrDefault(WarframeTranslationKey.name(raw), raw) : raw;
        return translations.getOrDefault(role + item.path("uniqueName").asText(), fallback);
    }

    private JsonNode findItem(Path corpus, String file, String key) throws Exception {
        for (JsonNode item : mapper.readTree(Files.readString(corpus.resolve("warframe-items/data/json/" + file)))) {
            if (item.path("uniqueName").asText().equals(key)) return item;
        }
        throw new IllegalArgumentException("Missing reference item: " + key);
    }

    private WarframeRelicVO relic(String name, String icon, String detail) {
        return new WarframeRelicVO("", List.of(new WarframeRelicVO.RelicVO(
                name, null, List.of(new WarframeRelicVO.RewardVO(name, "稀有", BigDecimal.ZERO, null),
                new WarframeRelicVO.RewardVO(name, null, null, null)),
                List.of(new WarframeRelicVO.DropVO(detail, null, null)), null, "1/1 页", null, icon,
                null, "SOURCE_SELECTED")), List.of(), "1/1 页", null);
    }

    private WarframeEquipmentVO equipment(String name, String icon, String detail) {
        return WarframeEquipmentVO.page("", "战甲", List.of(new WarframeEquipmentVO.EntryVO(
                "/Lotus/Test", name, detail, null, icon, null, "SOURCE_SELECTED",
                List.of(new WarframeEquipmentVO.MetricVO("生命", BigDecimal.ZERO)),
                List.of(new WarframeEquipmentVO.AbilityVO(1, "/Lotus/Skill", name, detail,
                        null, null, null, null)))), 1);
    }

    private WarframeRivenWeeklyVO weekly(String name) {
        return new WarframeRivenWeeklyVO(name, "pc", "1/1 页", "WF_RUNTIME_RIVEN_WEEKLY_OFFICIAL_PC",
                Instant.parse("2026-10-13T12:55:00Z"), false, List.of(new WarframeRivenWeeklyVO.EntryVO(
                name, name, "Rifle Riven Mod", false, BigDecimal.ZERO, null, null, null, null, null,
                "/Lotus/TestWeapon")), null);
    }

    private void assertLayout(Browser browser, String html, int width, boolean images) {
        try (Page page = browser.newPage()) {
            page.setViewportSize(width, 800);
            page.setContent(html);
            page.evaluate("() => document.fonts.ready");
            assertThat(page.evaluate("() => document.documentElement.scrollWidth <= innerWidth"))
                    .as("document width %s", width).isEqualTo(true);
            assertThat(page.evaluate("""
                    () => Array.from(document.querySelectorAll('body *')).filter(e =>
                      getComputedStyle(e).display !== 'none' && e.getClientRects().length &&
                      e.clientWidth > 0 && e.scrollWidth > e.clientWidth + 1 &&
                      !['MAIN','IMG'].includes(e.tagName)).map(e => e.className)
                    """)).as("text overflow at %s", width).isEqualTo(List.of());
            assertThat(page.evaluate("""
                    () => Array.from(document.querySelectorAll('.art')).every(art => {
                      const content = art.parentElement.querySelector('.hero-content');
                      if (!content) return true;
                      const a = art.getBoundingClientRect();
                      return Array.from(content.querySelectorAll('*')).every(e => {
                        const range = document.createRange(); range.selectNodeContents(e);
                        return Array.from(range.getClientRects()).every(r =>
                          r.right <= a.left + 1 || r.left >= a.right || r.bottom <= a.top || r.top >= a.bottom);
                      });
                    })
                    """)).as("art/text overlap at %s", width).isEqualTo(true);
            if (images) {
                assertThat(page.evaluate("() => Array.from(document.images).every(i => i.complete && i.naturalWidth > 0)"))
                        .as("loaded images at %s", width).isEqualTo(true);
            }
            try {
                assertNonblank(page.screenshot(new Page.ScreenshotOptions().setFullPage(true)));
            } catch (Exception exception) {
                throw new IllegalStateException("Cannot inspect screenshot", exception);
            }
        }
    }

    private void assertNonblank(byte[] png) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
        assertThat(image).isNotNull();
        assertThat(image.getHeight()).isGreaterThan(200);
        HashSet<Integer> colors = new HashSet<Integer>();
        for (int y = 0; y < image.getHeight(); y += 8) {
            for (int x = 0; x < image.getWidth(); x += 8) colors.add(image.getRGB(x, y));
        }
        assertThat(colors.size()).isGreaterThan(200);
    }

    private String render(String label, Object display) throws Exception {
        return renderer().renderHtml(template(label), variables(display));
    }

    private ThymeleafTemplateRenderer renderer() {
        ThymeleafTemplateRenderer renderer = new ThymeleafTemplateRenderer();
        ReflectionTestUtils.setField(renderer, "templateEngine", new SpringTemplateEngine());
        ReflectionTestUtils.invokeMethod(renderer, "initialize");
        return renderer;
    }

    private Map<String, Object> variables(Object display) {
        return mapper.convertValue(display, new TypeReference<>() {});
    }

    private String template(String templateCode) throws Exception {
        return SqlTemplateFixtures.template(templateCode);
    }

    private record Sample(String label, Object display) {}
}
