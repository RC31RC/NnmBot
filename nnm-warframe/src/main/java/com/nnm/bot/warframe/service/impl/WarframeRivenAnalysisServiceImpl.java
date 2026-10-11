package com.nnm.bot.warframe.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.warframe.application.WarframeItemResolution;
import com.nnm.bot.warframe.application.WarframeRivenAnalysisService;
import com.nnm.bot.warframe.model.entity.WarframeItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityDataRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenAnalyseTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeRivenAnalysisResult;
import com.nnm.bot.warframe.service.WarframeItemResolverService;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 紫卡 OCR 业务解析；词条名称和组合规则始终来自社区数据集。 */
@Service
public class WarframeRivenAnalysisServiceImpl implements WarframeRivenAnalysisService {

    private static final Pattern CARD_RANK_PATTERN = Pattern.compile(
            "(?i)(?:rank|等级)\\s*[:：#-]?\\s*(\\d{1,2})");
    private static final Pattern MASTERY_RANK_PATTERN = Pattern.compile(
            "(?i)(?:mr|mastery|段位|等级需求)\\s*[:：#-]?\\s*(\\d{1,2})");
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "[+-]?\\d+(?:[.,]\\d+)?\\s*%?");
    private static final Pattern NEGATIVE_PATTERN = Pattern.compile("(?i)(?:负面?|negative|-)\\s*");

    @Resource
    private WarframeItemResolverService itemResolver;
    @Resource
    private WarframeCommunityCatalogService communityCatalog;

    @Override
    public WarframeRivenAnalysisResult analyze(OcrTextResult ocr) {
        String rawText = ocr == null ? "" : StrUtil.nullToEmpty(ocr.text());
        List<String> lines = cleanLines(ocr == null ? List.of() : ocr.lines(), rawText);
        WeaponMatch weapon = findWeapon(lines);
        Integer rank = findNumber(lines, CARD_RANK_PATTERN);
        Integer masteryRank = findNumber(lines, MASTERY_RANK_PATTERN);
        if (rank == null) rank = findNumber(lines, Pattern.compile(
                "(?i)(?:rank|等级)\\s*[:：#-]?\\s*(\\d{1,2})"));
        if (masteryRank == null && rank != null) masteryRank = rank;
        AttributeIndex attributes = attributeIndex();
        ParsedAttributes parsed = parseAttributes(lines, attributes);
        boolean negativeAny = containsAnyNegative(lines);
        List<WarframeRivenAnalysisResult.Trend> trends = findTrends(
                parsed.attributes(), weapon, attributes);
        List<String> missing = new ArrayList<>();
        if (weapon.name().isBlank()) {
            missing.add("weapon");
            if (weapon.candidates().isEmpty()) {
                missing.add("weaponCandidate");
            }
        }
        if (rank == null) {
            missing.add("cardRank");
            missing.add("rank");
        }
        if (parsed.attributes().isEmpty() && !negativeAny) {
            missing.add("attributes");
        }
        return new WarframeRivenAnalysisResult(
                rawText.trim(),
                lines,
                weapon.name(),
                weapon.type(),
                weapon.candidates(),
                weapon.confirmed(),
                rank,
                masteryRank,
                parsed.attributes(),
                negativeAny,
                trends,
                parsed.lines(),
                missing);
    }

    private WeaponMatch findWeapon(List<String> lines) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        WarframeItem best = null;
        for (String source : lines == null ? List.<String>of() : lines) {
            if (looksLikeMetadata(source)) continue;
            String[] words = source.trim().split("\\s+");
            int max = Math.min(words.length, 6);
            for (int end = 1; end <= max; end++) {
                String candidate = String.join(" ", java.util.Arrays.copyOf(words, end)).trim();
                if (candidate.isBlank()) {
                    continue;
                }
                WarframeItemResolution resolution = itemResolver.resolve(candidate);
                if (resolution.found()) {
                    WarframeItem item = resolution.item();
                    int bestLength = best == null
                            ? -1
                            : StrUtil.blankToDefault(
                                    best.getLocalizedName(), best.getOfficialName()).length();
                    if (best == null || candidate.length() > bestLength) {
                        best = item;
                    }
                } else if (resolution.ambiguous()) {
                    resolution.candidates().stream()
                            .map(WarframeItem::getLocalizedName)
                            .filter(StrUtil::isNotBlank)
                            .forEach(candidates::add);
                }
            }
        }
        if (best != null) {
            String name = StrUtil.blankToDefault(
                    best.getLocalizedName(), best.getOfficialName());
            return new WeaponMatch(name, StrUtil.nullToEmpty(best.getProductCategory()),
                    List.of(name), true);
        }
        // OCR 无法直接命中时，保留模糊候选而不是输出错误武器。
        return new WeaponMatch("", "", List.copyOf(candidates), false);
    }

    private static Integer findNumber(List<String> lines, Pattern pattern) {
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) return Integer.valueOf(matcher.group(1));
        }
        return null;
    }

    private ParsedAttributes parseAttributes(List<String> lines, AttributeIndex index) {
        List<WarframeRivenAnalysisResult.Attribute> result = new ArrayList<>();
        List<WarframeRivenAnalysisResult.LineAnalysis> evidence = new ArrayList<>();
        for (String line : lines) {
            parseAttributeLine(line, index, result, evidence);
        }
        return new ParsedAttributes(result, evidence);
    }

    private void parseAttributeLine(
            String line,
            AttributeIndex index,
            List<WarframeRivenAnalysisResult.Attribute> result,
            List<WarframeRivenAnalysisResult.LineAnalysis> evidence) {
            String attributeText = line.replaceAll(
                    "(?i)负面?任意|negative\\s*any", "");
            String normalized = compact(attributeText);
            if (normalized.isBlank() || looksLikeMetadata(attributeText)) return;
            List<Occurrence> occurrences = new ArrayList<>();
            for (Map.Entry<String, List<AttributeMapping>> entry : index.byAlias().entrySet()) {
                Matcher matcher = Pattern.compile(Pattern.quote(entry.getKey()),
                        Pattern.CASE_INSENSITIVE).matcher(normalized);
                if (matcher.find()) {
                    AttributeMapping mapping = entry.getValue().getFirst();
                    for (AttributeMapping candidate : entry.getValue()) {
                        if (candidate.codes().size() > mapping.codes().size()
                                && normalized.equals(entry.getKey())) {
                            mapping = candidate;
                        }
                    }
                    occurrences.add(new Occurrence(matcher.start(), matcher.end(), mapping,
                            isNegative(normalized, matcher.start()),
                            nearbyNumber(normalized, matcher.start(), matcher.end())));
                }
            }
            occurrences.sort(Comparator.comparingInt(Occurrence::start)
                    .thenComparingInt(value -> -(value.end() - value.start())));
            List<String> codes = new ArrayList<>();
            int consumedEnd = -1;
            for (Occurrence occurrence : occurrences) {
                if (occurrence.start() < consumedEnd) continue;
                AttributeMapping mapping = occurrence.mapping();
                codes.addAll(mapping.codes());
                result.add(new WarframeRivenAnalysisResult.Attribute(
                        String.join(",", mapping.codes()), mapping.displayName(),
                        occurrence.value(), occurrence.negative(), mapping.codes().size() > 1,
                        line, occurrence.value() == null ? "UNKNOWN" : "OCR_ONLY"));
                consumedEnd = occurrence.end();
            }
            if (!occurrences.isEmpty()) {
                evidence.add(new WarframeRivenAnalysisResult.LineAnalysis(line, "ATTRIBUTE",
                        codes, occurrences.stream().anyMatch(value -> value.value() == null)
                                ? "PARTIAL" : "OCR_ONLY"));
            }
    }

    private boolean containsAnyNegative(List<String> lines) {
        String value = normalize(String.join(" ", lines));
        return value.contains("负任意") || value.contains("负面任意")
                || value.contains("negativeany");
    }

    private List<WarframeRivenAnalysisResult.Trend> findTrends(
            List<WarframeRivenAnalysisResult.Attribute> parsed,
            WeaponMatch weapon,
            AttributeIndex index) {
        if (parsed.isEmpty()) {
            return List.of();
        }
        String weaponType = normalizeType(weapon.type());
        List<WarframeRivenAnalysisResult.Trend> result = new ArrayList<>();
        for (WarframeCommunityDataRecord<WarframeCommunityRivenAnalyseTrendDetails> record
                : safeTrends()) {
            WarframeCommunityRivenAnalyseTrendDetails details = record.details();
            String trendType = normalizeType(details.prefix() + " " + details.suffix());
            if (!weaponType.isBlank() && !trendType.isBlank()
                    && !trendType.contains(weaponType) && !weaponType.contains(trendType)) {
                continue;
            }
            for (WarframeRivenAnalysisResult.Attribute attribute : parsed) {
                if (normalize(attribute.displayName()).contains(normalize(details.displayName()))
                        || normalize(details.displayName()).contains(
                        normalize(attribute.displayName()))) {
                    result.add(new WarframeRivenAnalysisResult.Trend(
                            details.displayName(),
                            StrUtil.nullToEmpty(details.prefix()) + StrUtil.nullToEmpty(details.suffix()),
                            trendValue(details, weaponType)));
                    break;
                }
            }
        }
        return result.stream().distinct().toList();
    }

    private static Double trendValue(
            WarframeCommunityRivenAnalyseTrendDetails details, String type) {
        if (type.contains("melee")) return details.meleeValue();
        if (type.contains("pistol")) return details.pistol();
        if (type.contains("shotgun")) return details.shotgun();
        return details.rifle();
    }

    private AttributeIndex attributeIndex() {
        Map<String, List<AttributeMapping>> result = new LinkedHashMap<>();
        for (WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionDetails> record
                : safeTions()) {
            WarframeCommunityMarketRivenTionDetails details = record.details();
            if (Boolean.TRUE.equals(details.searchOnly())
                    || StrUtil.isBlank(details.urlName())) {
                continue;
            }
            addMapping(result, details.urlName(), details.urlName(), details.effect());
            addMapping(result, details.effect(), details.urlName(), details.effect());
        }
        for (WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionAliasDetails> record
                : safeAliases()) {
            var details = record.details();
            addMapping(result, details.aliasName(), details.attributeCode(), details.aliasName());
        }
        result.values().forEach(values -> values.sort(
                Comparator.comparingInt((AttributeMapping value) -> value.codes().size())
                        .reversed()));
        return new AttributeIndex(result);
    }

    private static void addMapping(
            Map<String, List<AttributeMapping>> result,
            String alias,
            String code,
            String displayName) {
        String normalized = normalize(alias);
        if (normalized.isBlank() || StrUtil.isBlank(code)) {
            return;
        }
        List<String> codes = java.util.Arrays.stream(code.split(","))
                .map(String::trim).filter(value -> !value.isBlank()).toList();
        if (codes.isEmpty()) return;
        AttributeMapping mapping = new AttributeMapping(codes,
                StrUtil.blankToDefault(displayName, alias));
        List<AttributeMapping> values = result.computeIfAbsent(normalized,
                ignored -> new ArrayList<>());
        if (!values.contains(mapping)) values.add(mapping);
    }

    private static boolean isNegative(String text, int start) {
        int from = Math.max(0, start - 16);
        return NEGATIVE_PATTERN.matcher(text.substring(from, start)).find();
    }

    private static Double nearbyNumber(String text, int start, int end) {
        int from = Math.max(0, start - 24);
        int to = Math.min(text.length(), end + 24);
        Matcher matcher = NUMBER_PATTERN.matcher(text.substring(from, to));
        Double closest = null;
        int distance = Integer.MAX_VALUE;
        while (matcher.find()) {
            int position = from + matcher.start();
            int currentDistance = position < start ? start - position : position - end;
            if (currentDistance < distance) {
                distance = currentDistance;
                closest = Double.valueOf(matcher.group().replace("%", "").replace(',', '.').trim());
            }
        }
        return closest;
    }

    private List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionDetails>>
            safeTions() {
        List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionDetails>> values =
                communityCatalog.marketRivenTions();
        return values == null ? List.of() : values;
    }

    private List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionAliasDetails>>
            safeAliases() {
        List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionAliasDetails>> values =
                communityCatalog.marketRivenTionAliases();
        return values == null ? List.of() : values;
    }

    private List<WarframeCommunityDataRecord<WarframeCommunityRivenAnalyseTrendDetails>>
            safeTrends() {
        List<WarframeCommunityDataRecord<WarframeCommunityRivenAnalyseTrendDetails>> values =
                communityCatalog.rivenAnalyseTrends();
        return values == null ? List.of() : values;
    }

    private static String compact(String value) {
        return Normalizer.normalize(StrUtil.nullToEmpty(value), Normalizer.Form.NFKC)
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\s+", "")
                .toLowerCase(Locale.ROOT);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(StrUtil.nullToEmpty(value), Normalizer.Form.NFKC)
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static String normalizeType(String value) {
        return normalize(value).replace("武器", "").replace("weapon", "");
    }

    private static List<String> cleanLines(List<String> lines, String rawText) {
        List<String> result = new ArrayList<>();
        if (lines != null) {
            lines.stream().map(String::trim).filter(value -> !value.isBlank()).forEach(result::add);
        }
        if (result.isEmpty()) {
            for (String line : StrUtil.nullToEmpty(rawText).split("\\R")) {
                if (!line.isBlank()) result.add(line.trim());
            }
        }
        return List.copyOf(result);
    }

    private static boolean looksLikeMetadata(String line) {
        String normalized = normalize(line);
        return CARD_RANK_PATTERN.matcher(line).find()
                || MASTERY_RANK_PATTERN.matcher(line).find()
                || normalized.contains("负任意")
                || normalized.contains("negativeany");
    }

    private record WeaponMatch(
            String name, String type, List<String> candidates, boolean confirmed) {
    }

    private record ParsedAttributes(
            List<WarframeRivenAnalysisResult.Attribute> attributes,
            List<WarframeRivenAnalysisResult.LineAnalysis> lines) {
    }

    private record AttributeMapping(List<String> codes, String displayName) {
    }

    private record AttributeIndex(Map<String, List<AttributeMapping>> byAlias) {
    }

    private record Occurrence(
            int start,
            int end,
            AttributeMapping mapping,
            boolean negative,
            Double value) {
    }
}
