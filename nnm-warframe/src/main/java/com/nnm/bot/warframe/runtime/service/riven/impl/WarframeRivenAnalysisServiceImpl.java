package com.nnm.bot.warframe.runtime.service.riven.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO.AttributeVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO.LineAnalysisVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO.ReferenceRangeVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO.TrendVO;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenAnalysisService;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenAttributeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** 紫卡 OCR 业务解析；正式属性来自官方定义，人工别名与组合来自白名单。 */
@Service
public class WarframeRivenAnalysisServiceImpl implements WarframeRivenAnalysisService {

    /** 卡片升级等级，与使用紫卡所需的段位无关。 */
    private static final Pattern CARD_RANK_PATTERN = Pattern.compile(
            "(?i)(?<![a-z])(?:card\\s*rank|rank|卡片等级|等级)"
                    + "\\s*[:#-]?\\s*(\\d{1,2})(?!\\d)");
    /** 使用要求；先屏蔽此标记，避免 Mastery Rank 被当成卡片 Rank。 */
    private static final Pattern MASTERY_RANK_PATTERN = Pattern.compile(
            "(?i)(?<![a-z])(?:mastery(?:\\s*rank)?|mr|段位(?:需求)?|等级需求)"
                    + "\\s*[:#-]?\\s*(\\d{1,2})(?!\\d)");
    /** OCR 可在符号后插入水平空白；符号、数值及单位仍是同一行内的证据。 */
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "(?i)([x\u00d7]\\s*)?((?:[+-]\\h*)?\\d+(?:[.,]\\d+)?)\\s*"
                    + "(%|seconds?\\b|secs?\\b|s\\b|秒|[x\u00d7])?");
    private static final Pattern UNSUPPORTED_RULE_PATTERN = Pattern.compile(
            "(?i)\\bsplic(?:e|ed|ing)\\b|拼接|融合词条|组合词条|锁定词条");
    /** 筛选用的任意负面条件，不代表已识别出某个具体负词条。 */
    private static final Pattern NEGATIVE_ANY_PATTERN = Pattern.compile(
            "(?i)负面?任意|\\bnegative\\s*any\\b");
    /** 语义负面标签与属性数学符号分开处理。 */
    private static final Pattern NEGATIVE_LABEL_PATTERN = Pattern.compile("(?i)(?:negative|负面?)");
    /** 仅紧邻当前词条的标签能表达负面，不能沿用前一个词条的负号。 */
    private static final Pattern NEGATIVE_PREFIX_PATTERN = Pattern.compile(
            "(?i)(?:negative|负面?)\\s*(?:(?:[+-]\\h*)?\\d+(?:[.,]\\d+)?\\s*%?)?\\s*$");

    /** 从已生效物品投影识别武器，歧义结果只作为候选。 */
    @Resource
    private WarframeItemResolverService itemResolver;
    @Resource
    private WarframeCommunityCatalogService communityCatalog;

    @Resource
    private WarframeRivenAttributeService attributeService;

    /**
     * 同时返回结构化结果和原始证据；缺失等级不推断补齐。
     *
     * @param ocr 文字识别。
     * @return 紫卡识别、属性分析与参考数值的展示结果。
     */
    @Override
    public WarframeRivenAnalysisVO analyze(OcrTextResult ocr) {
        String rawText = ocr == null ? "" : StrUtil.nullToEmpty(ocr.text());
        List<String> lines = cleanLines(ocr == null ? List.of() : ocr.lines(), rawText);
        AttributeIndex attributes = attributeIndex();
        WeaponMatch weapon = findWeapon(lines, attributes);
        Integer rank = findNumber(lines, CARD_RANK_PATTERN, true);
        Integer masteryRank = findNumber(lines, MASTERY_RANK_PATTERN, false);
        ParsedAttributes parsed = parseAttributes(lines, attributes);
        boolean negativeAny = containsAnyNegative(lines);
        List<TrendVO> trends = findTrends(
                parsed.attributes(), weapon, attributes);
        List<ReferenceRangeVO> ranges =
                referenceRanges(weapon, rank, parsed, attributes, negativeAny, lines);
        List<String> missing = new ArrayList<>();
        if (StrUtil.isBlank(weapon.name())) {
            missing.add("weapon");
            if (CollUtil.isEmpty(weapon.candidates())) {
                missing.add("weaponCandidate");
            }
        }
        if (rank == null) {
            missing.add("cardRank");
            missing.add("rank");
        }
        if (masteryRank == null) {
            missing.add("masteryRank");
        }
        if (CollUtil.isEmpty(parsed.attributes()) && !negativeAny) {
            missing.add("attributes");
        }
        if (parsed.ambiguous() && !missing.contains("attributesAmbiguous")) {
            missing.add("attributesAmbiguous");
        }
        return new WarframeRivenAnalysisVO(
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
                missing,
                ranges);
    }

    /**
     * 最长精确名称优先；模糊命中只给候选，多个武器或变体冲突不能静默选一个。
     *
     * @param lines 行集合。
     * @param attributes 属性。
     * @return 武器匹配对象。
     */
    private WeaponMatch findWeapon(List<String> lines, AttributeIndex attributes) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        Map<String, WarframeItemDTO> confirmed = new LinkedHashMap<>();
        boolean ambiguous = false;
        for (String source : lines == null ? List.<String>of() : lines) {
            List<String> prefixes = weaponPrefixes(source, attributes);
            boolean matched = false;
            for (String candidate : prefixes) {
                WarframeItemResolutionDTO resolution = itemResolver.resolveExact(candidate);
                if (resolution.found() && isWeapon(resolution.item())) {
                    WarframeItemDTO item = resolution.item();
                    confirmed.putIfAbsent(StrUtil.blankToDefault(
                            item.getSourceKey(), item.getOfficialName()), item);
                    matched = true;
                    break;
                } else if (resolution.ambiguous()
                        && resolution.candidates().stream().anyMatch(
                                WarframeRivenAnalysisServiceImpl::isWeapon)) {
                    addWeaponCandidates(candidates, resolution.candidates());
                    ambiguous = true;
                    matched = true;
                    // 较长变体存在歧义时不能回退成较短基础武器。
                    break;
                }
            }
            if (!matched) {
                for (String candidate : prefixes) {
                    WarframeItemResolutionDTO resolution = itemResolver.resolve(candidate);
                    addWeaponCandidates(candidates, resolution.found()
                            ? List.of(resolution.item()) : resolution.candidates());
                }
            }
        }
        if (confirmed.size() == 1 && !ambiguous) {
            WarframeItemDTO best = confirmed.values().iterator().next();
            String name = StrUtil.blankToDefault(
                    best.getLocalizedName(), best.getOfficialName());
            String type = best.getProductCategory();
            return new WeaponMatch(name, StrUtil.nullToEmpty(type), best.getSourceKey(),
                    List.of(name), true);
        }
        addWeaponCandidates(candidates, List.copyOf(confirmed.values()));
        return new WeaponMatch("", "", "", List.copyOf(candidates), false);
    }

    /**
     * 以已采纳词条和同词条数值切出连写武器前缀；原行保留给精确名称匹配。
     *
     * @param line 行。
     * @param attributes 属性。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> weaponPrefixes(String line, AttributeIndex attributes) {
        String text = stripMetadata(Normalizer.normalize(
                StrUtil.nullToEmpty(line), Normalizer.Form.NFKC).replace('\u2212', '-'))
                .replaceAll("\\s+", " ").trim();
        LinkedHashSet<String> prefixes = new LinkedHashSet<>();
        addWeaponPrefixes(prefixes, text);
        List<Occurrence> matches = occurrences(text.toLowerCase(Locale.ROOT), attributes);
        if (CollUtil.isNotEmpty(matches)) {
            int end = matches.getFirst().start();
            ValueToken firstValue = bindValues(text, matches).getFirst();
            if (firstValue != null && firstValue.end() <= end) {
                end = firstValue.start();
            }
            String prefix = text.substring(0, end);
            Matcher negative = NEGATIVE_PREFIX_PATTERN.matcher(prefix);
            if (negative.find()) {
                prefix = prefix.substring(0, negative.start());
            }
            addWeaponPrefixes(prefixes, prefix.replaceFirst("[+-]\\s*$", "").trim());
        }
        return prefixes.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
    }

    /**
     * 英文复合名称保留空格；最多展开六个词前缀，完整原文仍可精确匹配。
     *
     * @param prefixes 前缀集合。
     * @param text 待处理的文本。
     */
    private static void addWeaponPrefixes(Set<String> prefixes, String text) {
        if (StrUtil.isBlank(text)) return;
        prefixes.add(text);
        String[] words = text.split("\\s+");
        for (int end = 1; end <= Math.min(words.length, 6); end++) {
            prefixes.add(String.join(" ", Arrays.copyOf(words, end)));
        }
    }

    /**
     * 只把已采纳的武器类型用于确认，MOD、资源和纯翻译词条不能成为武器。
     *
     * @param item 当前物品或集合成员。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean isWeapon(WarframeItemDTO item) {
        return Set.of("exportweapons", "exportrailjackweapons")
                .contains(normalize(item.getDomainCode()))
                || Set.of("weapon", "primary", "secondary", "sentinelweapons")
                .contains(normalize(item.getProductCategory()))
                || StrUtil.isNotBlank(normalizeType(item.getProductCategory()));
    }

    /**
     * 候选名称保留来源译文或英文名，不把单一模糊结果自动升级为确认。
     *
     * @param names 名称集合。
     * @param items 当前物品或成员列表。
     */
    private static void addWeaponCandidates(Set<String> names, List<WarframeItemDTO> items) {
        items.stream().filter(WarframeRivenAnalysisServiceImpl::isWeapon)
                .map(item -> StrUtil.blankToDefault(item.getLocalizedName(), item.getOfficialName()))
                .filter(StrUtil::isNotBlank).forEach(names::add);
    }

    /**
     * 卡片等级查询先排除段位标签，防止 Mastery Rank 误匹配。
     *
     * @param lines 行集合。
     * @param pattern 匹配规则。
     * @param cardRank 卡片等级。
     * @return 当前操作计算或读取的数值。
     */
    private static Integer findNumber(List<String> lines, Pattern pattern, boolean cardRank) {
        Set<Integer> values = new LinkedHashSet<>();
        for (String line : lines) {
            String value = normalize(line);
            if (cardRank) {
                value = MASTERY_RANK_PATTERN.matcher(value).replaceAll(" ");
            }
            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) values.add(Integer.valueOf(matcher.group(1)));
        }
        return values.size() == 1 ? values.iterator().next() : null;
    }

    /**
     * 每行独立解析，禁止从相邻 OCR 行借用数值。
     *
     * @param lines 行集合。
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @return 解析后属性对象。
     */
    private ParsedAttributes parseAttributes(List<String> lines, AttributeIndex index) {
        List<AttributeVO> result = new ArrayList<>();
        List<LineAnalysisVO> evidence = new ArrayList<>();
        boolean ambiguous = false;
        for (String line : lines) {
            ambiguous |= parseAttributeLine(line, index, result, evidence);
        }
        return new ParsedAttributes(result, evidence, ambiguous);
    }

    /**
     * 收集行内全部词条，保留无歧义属性并返回本行是否含冲突别名。
     *
     * @param line 行。
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param result 当前正在组装的业务结果。
     * @param evidence 依据集合。
     * @return 满足解析属性行时为真，否则为假。
     */
    private boolean parseAttributeLine(
            String line,
            AttributeIndex index,
            List<AttributeVO> result,
            List<LineAnalysisVO> evidence) {
        String text = stripMetadata(normalize(line));
        List<Occurrence> occurrences = occurrences(text, index);
        List<ValueToken> values = bindValues(text, occurrences);
        List<String> codes = new ArrayList<>();
        boolean partial = false;
        boolean ambiguous = false;
        for (int position = 0; position < occurrences.size(); position++) {
            Occurrence occurrence = occurrences.get(position);
            AttributeMapping mapping = occurrence.mapping();
            ValueToken token = values.get(position);
            if (mapping == null) {
                ambiguous = true;
                continue;
            }
            Double value = token == null ? null : token.value();
            int previousEnd = position == 0 ? 0 : occurrences.get(position - 1).end();
            String prefix = text.substring(previousEnd, occurrence.start());
            result.add(new WarframeRivenAnalysisVO.AttributeVO(
                    String.join(",", mapping.codes()), mapping.displayName(), value,
                    isNegative(mapping, index, prefix, value), mapping.codes().size() > 1,
                    line, value == null ? "UNKNOWN" : "OCR_ONLY",
                    token == null ? "unknown" : token.unit()));
            codes.addAll(mapping.codes());
            partial |= value == null;
        }
        if (CollUtil.isNotEmpty(codes) || ambiguous) {
            evidence.add(new WarframeRivenAnalysisVO.LineAnalysisVO(
                    line, ambiguous ? "ATTRIBUTE_AMBIGUOUS" : "ATTRIBUTE", codes,
                    ambiguous ? "AMBIGUOUS" : partial ? "PARTIAL" : "OCR_ONLY"));
        }
        return ambiguous;
    }

    /**
     * 长别名优先；歧义长别名也占用原文范围，防止短别名或数值跨越歧义证据。
     *
     * @param text 待处理的文本。
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<Occurrence> occurrences(String text, AttributeIndex index) {
        List<Occurrence> matches = new ArrayList<>();
        index.byAlias().forEach((alias, mappings) -> {
            String start = asciiLetter(alias.charAt(0)) ? "(?<![a-z_])" : "";
            String end = asciiLetter(alias.charAt(alias.length() - 1)) ? "(?![a-z_])" : "";
            String expression = Arrays.stream(alias.split("\\s+"))
                    .map(Pattern::quote).collect(Collectors.joining("\\s*"));
            Matcher matcher = Pattern.compile(start + expression + end).matcher(text);
            while (matcher.find()) {
                matches.add(new Occurrence(matcher.start(), matcher.end(),
                        mappings.size() == 1 ? mappings.getFirst() : null));
            }
        });
        matches.sort(Comparator.comparingInt(Occurrence::start)
                .thenComparingInt(value -> -(value.end() - value.start())));
        List<Occurrence> accepted = new ArrayList<>();
        int consumedEnd = -1;
        for (Occurrence match : matches) {
            if (match.start() >= consumedEnd) {
                accepted.add(match);
                consumedEnd = match.end();
            }
        }
        return accepted;
    }

    /**
     * 一个数值只绑定一个词条；前后缀方向冲突或多值竞争时保留未知。
     *
     * @param text 待处理的文本。
     * @param occurrences 出现位置集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<ValueToken> bindValues(String text, List<Occurrence> occurrences) {
        List<ValueToken> tokens = new ArrayList<>();
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            double value = Double.parseDouble(matcher.group(2).replaceAll("\\h", "").replace(',', '.'));
            String suffix = normalize(matcher.group(3));
            String unit = matcher.group(1) != null || Set.of("x", "\u00d7").contains(suffix)
                    ? "multiplier" : "%".equals(suffix) ? "percent"
                    : StrUtil.isBlank(suffix) ? "scalar" : "seconds";
            tokens.add(new ValueToken(matcher.start(), matcher.end(), value, unit));
        }
        ValueToken[] assigned = new ValueToken[occurrences.size()];
        int[] counts = new int[occurrences.size()];
        boolean prefixStyle = tokens.stream().anyMatch(token -> CollUtil.isNotEmpty(occurrences)
                && token.end() <= occurrences.getFirst().start()
                && bridge(text.substring(token.end(), occurrences.getFirst().start())));
        boolean suffixStyle = tokens.stream().anyMatch(token -> CollUtil.isNotEmpty(occurrences)
                && token.start() >= occurrences.getLast().end()
                && bridge(text.substring(occurrences.getLast().end(), token.start())));
        for (ValueToken token : tokens) {
            int before = -1;
            int after = -1;
            for (int position = 0; position < occurrences.size(); position++) {
                Occurrence occurrence = occurrences.get(position);
                if (occurrence.end() <= token.start()
                        && bridge(text.substring(occurrence.end(), token.start()))) {
                    before = position;
                }
                if (after < 0 && occurrence.start() >= token.end()
                        && bridge(text.substring(token.end(), occurrence.start()))) {
                    after = position;
                }
            }
            int target = before < 0 ? after : after < 0 ? before
                    : prefixStyle != suffixStyle ? (prefixStyle ? after : before) : -1;
            if (target >= 0) {
                assigned[target] = token;
                counts[target]++;
            }
        }
        List<ValueToken> values = new ArrayList<>();
        for (int position = 0; position < assigned.length; position++) {
            values.add(counts[position] == 1 ? assigned[position] : null);
        }
        return values;
    }

    /**
     * 词条与数值之间只接受标签和简单分隔，不能跨过其他词条或正文。
     *
     * @param value 待校验或规范化的文本。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean bridge(String value) {
        return NEGATIVE_LABEL_PATTERN.matcher(value).replaceAll("")
                .matches("[\\s:=()\\[\\]]*");
    }

    /**
     * 正负效果按定义解释；减少后坐力的数学负数可以是正面属性。
     *
     * @param mapping 映射。
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param prefix 前缀。
     * @param value 待处理的字段值。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean isNegative(
            AttributeMapping mapping, AttributeIndex index, String prefix, Double value) {
        List<WarframeRivenAttributeDTO> definitions = mapping.codes().stream()
                .map(index.byCode()::get).filter(Objects::nonNull).toList();
        boolean allDefined = definitions.size() == mapping.codes().size();
        if (allDefined && definitions.stream().allMatch(
                definition -> Boolean.TRUE.equals(definition.negativeOnly()))) {
            return true;
        }
        if (NEGATIVE_PREFIX_PATTERN.matcher(prefix).find()) {
            return true;
        }
        boolean reversed = allDefined && definitions.stream().allMatch(
                definition -> Boolean.TRUE.equals(definition.positiveIsNegative()));
        if (value != null && value != 0) {
            return (value < 0) != reversed;
        }
        if (prefix.matches("(?s).*[-+]\\s*$")) {
            return prefix.trim().endsWith("-") != reversed;
        }
        return false;
    }

    /**
     * 任意负面只是筛选条件，允许英文包含空格。
     *
     * @param lines 行集合。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean containsAnyNegative(List<String> lines) {
        return NEGATIVE_ANY_PATTERN.matcher(normalize(String.join(" ", lines))).find();
    }

    /**
     * 输出官方原始参数及其语义，未经确认的系数不转换成满级百分比。
     *
     * @param parsed 解析后集合。
     * @param weapon 武器。
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<TrendVO> findTrends(List<AttributeVO> parsed, WeaponMatch weapon, AttributeIndex index) {
        return parsed.stream().flatMap(attribute -> codes(attribute.code()).stream())
                .distinct().map(index.byCode()::get).filter(Objects::nonNull)
                .map(definition -> new TrendVO(definition.effect(), weapon.type(), definition.parameters()))
                .toList();
    }

    /**
     * 当前源参数没有确定的卡面范围换算契约，明确返回未确认，保留全部 OCR 证据。
     *
     * @param weapon 武器。
     * @param rank 等级。
     * @param parsed 解析后。
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param negativeAny 负面任意。
     * @param lines 行集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<ReferenceRangeVO> referenceRanges(WeaponMatch weapon, Integer rank,
            ParsedAttributes parsed, AttributeIndex index, boolean negativeAny, List<String> lines) {
        return parsed.attributes().stream().map(attribute -> {
            WarframeRivenAttributeDTO definition = index.byCode().get(attribute.code());
            return new ReferenceRangeVO(attribute.code(), attribute.displayName(),
                    definition == null ? "" : StrUtil.nullToEmpty(definition.units()),
                    null, null, null, "UNAVAILABLE", "officialParameterConversionUnconfirmed",
                    "OFFICIAL_PARAMETERS_V1", StrUtil.nullToEmpty(weapon.key()),
                    weapon.confirmed() ? OFFICIAL_SOURCE_CODE : "", OFFICIAL_SOURCE_CODE);
        }).toList();
    }

    /**
     * 每次分析从生效投影建立索引；搜索占位项不能成为实际词条。
     *
     * @return 属性索引对象。
     */
    private AttributeIndex attributeIndex() {
        Map<String, List<AttributeMapping>> result = new LinkedHashMap<>();
        Map<String, WarframeRivenAttributeDTO> byCode = new LinkedHashMap<>();
        for (WarframeRivenAttributeDTO details : attributeService.definitions()) {
            if (StrUtil.isBlank(details.code()) || Boolean.TRUE.equals(details.searchOnly())
                    || Set.of("has", "none").contains(details.code())) {
                continue;
            }
            byCode.putIfAbsent(details.code(), details);
            addMapping(result, details.code(), details.code(), details.effect());
            addMapping(result, details.effect(), details.code(), details.effect());
            addMapping(result, details.effectEn(), details.code(), details.effect());
        }
        for (WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.RivenAliasDTO> record
                : safeAliases()) {
            WarframeCommunityRecordDTO.RivenAliasDTO details = record.details();
            List<String> codes = codes(details.attributeCode());
            if (CollUtil.isEmpty(codes) || !byCode.keySet().containsAll(codes)) {
                continue;
            }
            String display = codes.size() == 1 && byCode.containsKey(codes.getFirst())
                    ? byCode.get(codes.getFirst()).effect() : details.aliasName();
            addMapping(result, details.aliasName(), details.attributeCode(), display);
        }
        return new AttributeIndex(result, byCode);
    }

    /**
     * 别名和 OCR 使用相同的全角、大小写及空白规范。
     *
     * @param result 当前正在组装的业务结果。
     * @param alias 别名。
     * @param code 当前数据集或业务对象的稳定代码。
     * @param displayName 展示名称。
     */
    private static void addMapping(
            Map<String, List<AttributeMapping>> result,
            String alias,
            String code,
            String displayName) {
        String normalized = normalize(alias);
        if (StrUtil.isBlank(normalized) || StrUtil.isBlank(code)) {
            return;
        }
        List<String> codes = codes(code);
        if (CollUtil.isEmpty(codes)) return;
        AttributeMapping mapping = new AttributeMapping(codes,
                StrUtil.blankToDefault(displayName, alias));
        List<AttributeMapping> values = result.computeIfAbsent(normalized,
                ignored -> new ArrayList<>());
        if (values.stream().noneMatch(value -> sameCodeSet(value.codes(), mapping.codes()))) {
            values.add(mapping);
        }
    }

    /**
     * 组合别名的编码顺序不影响语义，但保留首个来源顺序作为展示顺序。
     *
     * @param left 参与比较的左侧数据。
     * @param right 参与比较的右侧数据。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean sameCodeSet(List<String> left, List<String> right) {
        return left.size() == right.size() && left.stream().allMatch(right::contains)
                && right.stream().allMatch(left::contains);
    }

    /**
     * 组合关系只来自已采纳编码列表，不从别名文字推断。
     *
     * @param value 待校验或规范化的文本。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> codes(String value) {
        return Arrays.stream(StrUtil.nullToEmpty(value).split("[,+]"))
                .map(String::trim).filter(code -> StrUtil.isNotBlank(code)).toList();
    }

    /**
     * 读取可用于本次词条解析的社区自定义别名，空缓存按空集合处理。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.RivenAliasDTO>> safeAliases() {
        return communityCatalog.rivenAliases();
    }

    /**
     * 统一全角与 OCR 数学负号，但保留英文词边界。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return Normalizer.normalize(StrUtil.nullToEmpty(value), Normalizer.Form.NFKC)
                .replace('\u2212', '-')
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    /**
     * 兼容 Items 类型与 Plus 类别；SpaceMelee 仅沿用当前参考规则的 archwing 基准。
     *    LongGuns、SentinelWeapons 均包含霰弹枪，不能仅凭这两个类别默认使用步枪系数。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化外部类型标识，便于同一业务规则比较对应的文本。
     */
    private static String normalizeType(String value) {
        return switch (normalize(value)) {
            case "melee", "近战", "近战武器" -> "melee";
            case "pistol", "pistols", "手枪" -> "pistol";
            case "shotgun", "shotguns", "霰弹枪" -> "shotgun";
            case "rifle", "步枪" -> "rifle";
            case "archwing", "arch-gun", "archgun", "spaceguns", "spacemelee" -> "archwing";
            default -> "";
        };
    }

    /**
     * 保留 OCR 行切分，仅在没有行数据时从原文恢复。
     *
     * @param lines 行集合。
     * @param rawText 原始文本。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> cleanLines(List<String> lines, String rawText) {
        List<String> result = new ArrayList<>();
        if (lines != null) {
            lines.stream().filter(Objects::nonNull).map(String::trim)
                    .filter(value -> StrUtil.isNotBlank(value)).forEach(result::add);
        }
        if (CollUtil.isEmpty(result)) {
            for (String line : StrUtil.nullToEmpty(rawText).split("\\R")) {
                if (StrUtil.isNotBlank(line)) result.add(line.trim());
            }
        }
        return List.copyOf(result);
    }

    /**
     * 只去掉元数据片段，保留同一行上的武器和词条。
     *
     * @param value 待校验或规范化的文本。
     * @return 去除元数据对应的文本。
     */
    private static String stripMetadata(String value) {
        String stripped = MASTERY_RANK_PATTERN.matcher(value).replaceAll(" ");
        stripped = CARD_RANK_PATTERN.matcher(stripped).replaceAll(" ");
        return NEGATIVE_ANY_PATTERN.matcher(stripped).replaceAll(" ");
    }

    /**
     * ASCII 词边界避免 C / G / I 等简称命中普通英文单词。
     *
     * @param value 待处理的字段值。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean asciiLetter(char value) {
        return value >= 'a' && value <= 'z';
    }

    /** 武器识别结果和未确认候选。 */
    private record WeaponMatch(
            String name, String type, String key, List<String> candidates, boolean confirmed) {
    }

    /** 属性列表及行证据，不包含持久化实体。 */
    private record ParsedAttributes(
            List<AttributeVO> attributes,
            List<LineAnalysisVO> lines,
            boolean ambiguous) {
    }

    /** 一个名称对应的明确属性编码列表，支持来源定义的组合别名。 */
    private record AttributeMapping(List<String> codes, String displayName) {
    }

    /** 别名用于匹配，正式编码用于读取效果方向和显示名称。 */
    private record AttributeIndex(
            Map<String, List<AttributeMapping>> byAlias,
            Map<String, WarframeRivenAttributeDTO> byCode) {
    }

    /** 行内半开字符范围，用于排除重叠和数值串位。 */
    private record Occurrence(int start, int end, AttributeMapping mapping) {
    }

    /** OCR 数值证据及其位置，未经过合法范围评级。 */
    private record ValueToken(int start, int end, double value, String unit) {
    }
}
