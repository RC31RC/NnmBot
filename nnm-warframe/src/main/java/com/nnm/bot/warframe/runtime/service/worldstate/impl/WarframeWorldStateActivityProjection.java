package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import static com.nnm.bot.warframe.normalization.WarframeNameNormalizer.hasChinese;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.first;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.normalize;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.putIfAbsent;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.text;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.toStringKeys;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialActivityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.runtime.constant.WarframeConquestType;
import com.nnm.bot.warframe.runtime.constant.WarframeDescentMatchPolicy;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateResolutionStatus;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/** 单次世界状态投影的活动解析阶段：收集精确引用、查库、标注降级结果。 */
final class WarframeWorldStateActivityProjection {
    private static final Set<String> RESEARCH_TITLES = Set.of("科研", "深层科研", "时光科研", "其他科研");
    private static final String DESCENT_TITLE = "沉沦之地";
    private static final String CONQUEST_TYPES = "conquests/types/";
    private static final String CONQUEST_CONDITIONS = "conquests/conditions/";
    private static final String CONQUEST_CONDITION_NAME = "/Lotus/Language/Conquest/Condition_";
    private static final String CONQUEST_CONDITION_DESCRIPTION_SUFFIX = "_Desc";
    private static final Pattern CONQUEST_CONDITION_CODE = Pattern.compile("[A-Za-z0-9]+(?:_[A-Za-z0-9]+)*");
    private static final String CONQUEST_MODIFIERS = "conquests/personalModifiers/";
    private static final String CONQUEST_MODIFIER_NAME = "/Lotus/Language/Conquest/PersonalMod_";
    private static final String CONQUEST_VALUE_TOKEN = "|val|";
    private static final String CONQUEST_VARIANTS = "conquests/missionVariants/";
    private static final String DESCENT_CHALLENGES = "descents/challenges/";
    private static final String DESCENT_MODES = "descents/gameModes/";
    private static final String DESCENT_PROTOFRAMES = "descents/protoframes/";
    private static final String DESCENT_PROTOFRAME_TYPE = "DT_PROTOFRAME";
    private static final String DESCENT_FALLBACK = "descents/fallback/";
    private static final List<String> DESCENT_GROUPS = List.of(DESCENT_CHALLENGES, DESCENT_MODES, DESCENT_PROTOFRAMES);

    private final WarframeOfficialLookupService officialLookup;
    private final WarframeWorldStateCodebookService codebookService;
    private final boolean requested;
    private final Map<String, ActivityPresentation> activities;

    /**
     * 同一批投影只读取一次活动定义；缺失时仍保留待核对的原始引用。
     *
     * @param originals 尚未翻译的动态业务分区。
     * @param officialLookup 已采纳活动记录的查询入口。
     * @param codebookService 按语言键补查官方文本的入口。
     */
    WarframeWorldStateActivityProjection(List<WarframeWorldStateCommandVO> originals,
        WarframeOfficialLookupService officialLookup, WarframeWorldStateCodebookService codebookService) {
        this.officialLookup = officialLookup;
        this.codebookService = codebookService;
        Set<String> keys = new LinkedHashSet<>();
        originals.forEach(output -> {
            String title = String.valueOf(output.data().get("title"));
            if (RESEARCH_TITLES.contains(title) || DESCENT_TITLE.equals(title))
                collectActivities(output.data(), title, null, keys);
        });
        requested = !keys.isEmpty();
        if (requested) {
            var records = officialLookup.records(WarframeOfficialDataDomain.ACTIVITY, keys);
            var resolved = new LinkedHashMap<>(activityPresentations(records));
            addMissingConquestTexts(keys, resolved);
            addDescentFallbacks(keys, resolved);
            activities = Map.copyOf(resolved);
        } else {
            activities = Map.of();
        }
    }

    /** @return 当前批次是否含需核对的活动引用。 */
    boolean requested() {
        return requested;
    }

    /**
     * 只有原始下降代码在三个确定命名空间均未直接命中时，才按 25 号规则标记降级匹配。 模糊结果不写回官方别名，仅用于本次展示，并暴露实际命中的静态键。
     *
     * @param requested 当前快照请求的下降定义。
     * @param presentations 已确定的静态解释，可补入隔离的降级条目。
     */
    private void addDescentFallbacks(Set<String> requested, Map<String, ActivityPresentation> presentations) {
        Set<String> unresolved = new LinkedHashSet<>();
        for (String reference : requested) {
            if (reference.startsWith(DESCENT_MODES) && !presentations.containsKey(normalize(reference))) {
                unresolved.add(reference);
            } else if (reference.startsWith(DESCENT_CHALLENGES)) {
                String challenge = reference.substring(DESCENT_CHALLENGES.length());
                if (DESCENT_GROUPS.stream().noneMatch(group -> presentations.containsKey(normalize(group + challenge))))
                    unresolved.add(reference);
            }
        }
        if (unresolved.isEmpty())
            return;
        var index = officialLookup.nameIndex(WarframeOfficialDataDomain.ACTIVITY);
        if (index == null || index.items() == null)
            return;
        List<String> available =
            index.items().values().stream().map(WarframeOfficialNameDTO::bkey).filter(Objects::nonNull)
                .filter(key -> DESCENT_GROUPS.stream().anyMatch(key::startsWith)).distinct().toList();
        Map<String, String> fallback = new LinkedHashMap<>();
        for (String reference : unresolved) {
            boolean mode = reference.startsWith(DESCENT_MODES);
            String token = reference.substring(reference.lastIndexOf('/') + 1);
            List<FuzzyCandidate> candidates = available.stream().filter(key -> !mode || key.startsWith(DESCENT_MODES))
                .map(key -> new FuzzyCandidate(key, similarity(token, key.substring(key.lastIndexOf('/') + 1))))
                .filter(candidate -> candidate.score() >= WarframeDescentMatchPolicy.MIN_SCORE)
                .sorted(Comparator.comparingDouble(FuzzyCandidate::score).reversed().thenComparing(FuzzyCandidate::key))
                .toList();
            if (candidates.size() > 1 && candidates.getFirst().score()
                - candidates.get(1).score() < WarframeDescentMatchPolicy.MIN_WINNING_MARGIN) {
                // 无法选定唯一候选时只记歧义，不能借用其中任一名称。
                presentations.put(normalize(DESCENT_FALLBACK + reference),
                    new ActivityPresentation(null, null, null, candidates.stream().map(FuzzyCandidate::key).toList()));
            } else if (!candidates.isEmpty()) {
                fallback.put(DESCENT_FALLBACK + reference, candidates.getFirst().key());
            }
        }
        if (fallback.isEmpty())
            return;
        var matched = activityPresentations(
            officialLookup.records(WarframeOfficialDataDomain.ACTIVITY, new LinkedHashSet<>(fallback.values())));
        fallback.forEach((key, resolved) -> {
            ActivityPresentation row = matched.get(normalize(resolved));
            if (row != null)
                presentations.put(normalize(key),
                    new ActivityPresentation(row.name(), row.description(), resolved, List.of(resolved)));
        });
    }

    /**
     * 与数据源一致：规范化后完全相同 0.98、明确后缀 0.88，否则按编辑距离评分。
     *
     * @param original 上游挑战或模式代码。
     * @param candidate 官方活动定义键。
     * @return 保留三位小数的可解释相似度，不代表统计概率。
     */
    private static double similarity(String original, String candidate) {
        String left = Normalizer.normalize(original, Normalizer.Form.NFKC).replaceAll("[^a-zA-Z0-9]", "")
            .toLowerCase(Locale.ROOT);
        String right = Normalizer.normalize(candidate, Normalizer.Form.NFKC).replaceAll("[^a-zA-Z0-9]", "")
            .toLowerCase(Locale.ROOT);
        if (left.length() < WarframeDescentMatchPolicy.MIN_TOKEN_LENGTH
            || right.length() < WarframeDescentMatchPolicy.MIN_TOKEN_LENGTH)
            return 0;
        if (left.equals(right))
            return WarframeDescentMatchPolicy.EXACT_NORMALIZED_SCORE;
        int shortLength = Math.min(left.length(), right.length());
        int longLength = Math.max(left.length(), right.length());
        if (shortLength >= WarframeDescentMatchPolicy.MIN_SUFFIX_LENGTH
            && (double)shortLength / longLength >= WarframeDescentMatchPolicy.MIN_SUFFIX_RATIO
            && (left.endsWith(right) || right.endsWith(left)))
            return WarframeDescentMatchPolicy.UNIQUE_SUFFIX_SCORE;
        return Math.round((1 - (double)editDistance(left, right) / longLength) * WarframeDescentMatchPolicy.SCORE_SCALE)
            / (double)WarframeDescentMatchPolicy.SCORE_SCALE;
    }

    /** @param left 左键；@param right 右键；@return Levenshtein 编辑距离。 */
    private static int editDistance(String left, String right) {
        int[] previous = new int[right.length() + 1];
        for (int i = 0; i < previous.length; i++)
            previous[i] = i;
        for (int i = 0; i < left.length(); i++) {
            int[] next = new int[right.length() + 1];
            next[0] = i + 1;
            for (int j = 0; j < right.length(); j++) {
                next[j + 1] = Math.min(Math.min(previous[j + 1] + 1, next[j] + 1),
                    previous[j] + (left.charAt(i) == right.charAt(j) ? 0 : 1));
            }
            previous = next;
        }
        return previous[right.length()];
    }

    /**
     * 对缺少直接中文的活动定义，按本次命中的语言键批量读取官方语言表。
     *
     * @param records 已按分组路径定位的活动定义。
     * @return 按规范业务键组织的可读名称和说明。
     */
    private Map<String, ActivityPresentation>
        activityPresentations(Map<String, WarframeOfficialRecordReadDTO> records) {
        if (records.isEmpty())
            return Map.of();
        Set<String> languageKeys = new LinkedHashSet<>();
        records.values().forEach(row -> {
            if (row.data() instanceof OfficialActivityDTO activity) {
                if (!hasChinese(first(activity.getNameZh(), row.localizedName()))
                    && text(activity.getNameKey()) != null)
                    languageKeys.add(activity.getNameKey());
                if (!hasChinese(first(activity.getDescriptionZh(), row.description()))
                    && text(activity.getDescriptionKey()) != null)
                    languageKeys.add(activity.getDescriptionKey());
            }
        });
        Map<String, WarframeWorldStateCodebookEntryDTO> languages =
            languageKeys.isEmpty() ? Map.of() : codebookService.findLanguages(languageKeys);
        Map<String, ActivityPresentation> result = new LinkedHashMap<>();
        records.forEach((key, row) -> {
            if (!(row.data() instanceof OfficialActivityDTO activity))
                return;
            String name = first(activity.getNameZh(), row.localizedName(), activity.getNameEn(), row.officialName());
            String description = first(activity.getDescriptionZh(), row.description(), activity.getDescriptionEn());
            String translatedName = languageName(languages, activity.getNameKey());
            String translatedDescription = languageName(languages, activity.getDescriptionKey());
            if (!hasChinese(name))
                name = first(translatedName, name);
            if (!hasChinese(description))
                description = first(translatedDescription, description);
            result.put(normalize(key), new ActivityPresentation(name, description, null, List.of()));
        });
        return Map.copyOf(result);
    }

    /**
     * 活动表缺条件时，按官方语言键格式批量查已入库文本，不按简称跨分组搜索。
     */
    private void addMissingConquestTexts(Set<String> activityKeys, Map<String, ActivityPresentation> activities) {
        Map<String, String> missing = new LinkedHashMap<>();
        Set<String> languageKeys = new LinkedHashSet<>();
        for (String key : activityKeys) {
            String group = key.startsWith(CONQUEST_CONDITIONS) ? CONQUEST_CONDITIONS
                : key.startsWith(CONQUEST_MODIFIERS) ? CONQUEST_MODIFIERS : null;
            var adopted = activities.get(normalize(key));
            if (group == null || (adopted != null && adopted.name() != null && adopted.description() != null))
                continue;
            String code = key.substring(group.length());
            if (!CONQUEST_CONDITION_CODE.matcher(code).matches())
                continue;
            String languageKey =
                (CONQUEST_CONDITIONS.equals(group) ? CONQUEST_CONDITION_NAME : CONQUEST_MODIFIER_NAME) + code;
            missing.put(normalize(key), languageKey);
            languageKeys.add(languageKey);
            languageKeys.add(languageKey + CONQUEST_CONDITION_DESCRIPTION_SUFFIX);
        }
        if (languageKeys.isEmpty())
            return;
        var languages = codebookService.findLanguages(languageKeys);
        missing.forEach((key, languageKey) -> {
            var adopted = activities.get(key);
            String name = first(adopted == null ? null : adopted.name(), languageName(languages, languageKey));
            if (name != null) {
                String description = first(adopted == null ? null : adopted.description(),
                    languageName(languages, languageKey + CONQUEST_CONDITION_DESCRIPTION_SUFFIX));
                activities.put(key, new ActivityPresentation(name, description, languageKey, List.of()));
            }
        });
    }

    /**
     * 安全取得已确认存在的语言文本。
     *
     * @param languages 当前快照已补查的语言条目。
     * @param key 原始语言引用键。
     * @return 找到的名称；未找到时为空。
     */
    private static String languageName(Map<String, WarframeWorldStateCodebookEntryDTO> languages, String key) {
        if (key == null)
            return null;
        WarframeWorldStateCodebookEntryDTO entry = languages.get(key);
        return entry != null && entry.resolved() ? text(entry.displayName()) : null;
    }

    /**
     * 按科研类型和沉沦之地分组收集可证实的活动记录业务键。
     *
     * @param value 原始投影中的嵌套值。
     * @param section 当前世界状态分区。
     * @param inheritedType 上层科研类型。
     * @param keys 同次查询所需业务键。
     */
    private static void collectActivities(Object value, String section, String inheritedType, Set<String> keys) {
        if (value instanceof Map<?, ?> map) {
            String type = conquestTypeKey(map);
            String researchType = type == null ? inheritedType : type;
            if (RESEARCH_TITLES.contains(section)) {
                if (type != null)
                    keys.add(CONQUEST_TYPES + type);
                add(keys, CONQUEST_CONDITIONS, map.get("risks"));
                add(keys, CONQUEST_MODIFIERS, map.get("personalModifiers"));
                String variant = variantKey(researchType, map.get("deviation"));
                if (variant != null)
                    keys.add(variant);
            } else if (DESCENT_TITLE.equals(section)) {
                String mode = descentModeKey(map.get("type"));
                if (mode != null)
                    keys.add(mode);
                for (String group : descentChallengeGroups(map.get("type")))
                    add(keys, group, map.get("challenge"));
            }
            for (Object child : map.values())
                collectActivities(child, section, researchType, keys);
        } else if (value instanceof Collection<?> list) {
            list.forEach(child -> collectActivities(child, section, inheritedType, keys));
        }
    }

    static String conquestTypeKey(Map<?, ?> row) {
        String candidate = first(text(row.get("typeKey")), text(row.get("type")));
        return candidate != null && candidate.startsWith("CT_") ? candidate : null;
    }

    /**
     * 只在确切匹配已采纳活动身份时补充名称和说明，不猜测类型代码。
     *
     * @param original 保留游戏原始键的记录。
     * @param display 已本地化的展示副本。
     * @param section 所属业务分区。
     * @param type 当前条目自身的科研类型，任务子项不继承展示名称。
     * @param researchType 当前科研轮次的类型，用于定位任务偏差。
     * @param path 当前记录在投影中的路径。
     * @param handledReferenceFields 本轮已解析或已由所属资料域诊断的字段。
     */
    void decorate(Map<String, Object> original, Map<String, Object> display, String section, String type,
        String researchType, String path, Set<String> handledReferenceFields) {
        if (RESEARCH_TITLES.contains(section)) {
            if (type != null) {
                var record = activities.get(normalize(CONQUEST_TYPES + type));
                String label = record == null ? null : record.name();
                if (label != null) {
                    display.put("type", label);
                    display.put("typeResolutionStatus", WarframeWorldStateResolutionStatus.DIRECT.code());
                    handledReferenceFields.add(path + ".type");
                }
                if (record != null && record.description() != null)
                    display.put("typeDescription", record.description());
            }
            var variant = activities.get(normalize(variantKey(researchType, original.get("deviation"))));
            if (variant != null && variant.name() != null) {
                display.put("deviation", variant.name());
                handledReferenceFields.add(path + ".deviation");
            }
            if (variant != null && variant.description() != null)
                display.put("deviationDescription", variant.description());
            applyList(display, "risks", CONQUEST_CONDITIONS, original.get("risks"), activities, path,
                handledReferenceFields);
            applyList(display, "personalModifiers", CONQUEST_MODIFIERS, original.get("personalModifiers"), activities,
                path, handledReferenceFields);
        } else if (DESCENT_TITLE.equals(section)) {
            String mode = descentModeKey(original.get("type"));
            ActivityPresentation directMode = activities.get(normalize(mode));
            if (mode != null)
                handledReferenceFields.add(path + ".type");
            if (directMode != null && directMode.name() != null) {
                applyExactDescent(display, "type", original.get("type"), directMode);
            } else if (mode != null)
                applyDescentFallback(display, "type", original.get("type"),
                    activities.get(normalize(DESCENT_FALLBACK + mode)));
            if (original.get("challenge") instanceof String challenge) {
                handledReferenceFields.add(path + ".challenge");
                List<String> matches = descentChallengeGroups(original.get("type")).stream()
                    .map(prefix -> prefix + challenge).filter(key -> {
                        var record = activities.get(normalize(key));
                        return record != null && record.name() != null;
                    }).toList();
                if (matches.size() == 1) {
                    applyExactDescent(display, "challenge", challenge, activities.get(normalize(matches.getFirst())));
                } else if (matches.isEmpty()) {
                    applyDescentFallback(display, "challenge", challenge,
                        activities.get(normalize(DESCENT_FALLBACK + DESCENT_CHALLENGES + challenge)));
                } else {
                    display.put("challenge", challenge);
                    display.put("challengeKey", challenge);
                    display.put("challengeResolutionStatus", WarframeWorldStateResolutionStatus.AMBIGUOUS.code());
                    display.put("challengeCandidates", matches);
                }
            }
        }
    }

    private static void applyExactDescent(Map<String, Object> display, String field, Object raw,
        ActivityPresentation row) {
        display.put(field, row.name());
        display.put(field + "Key", raw);
        display.put(field + "ResolutionStatus", WarframeWorldStateResolutionStatus.DIRECT.code());
        applyActivity(display, field, row);
    }

    private static List<String> descentChallengeGroups(Object type) {
        if (DESCENT_PROTOFRAME_TYPE.equals(type))
            return List.of(DESCENT_PROTOFRAMES);
        return type instanceof String code && code.startsWith("DT_") ? List.of(DESCENT_CHALLENGES, DESCENT_MODES)
            : DESCENT_GROUPS;
    }

    /**
     * 降级匹配不冒充精确身份，详情同时保留原始值、匹配到的官方键与状态。
     *
     * @param display 展示中的下降挑战。
     * @param field 模式或挑战字段。
     * @param raw 上游原始代码。
     * @param fallback 唯一可解释的相似定义；缺失时原样保留。
     */
    private static void applyDescentFallback(Map<String, Object> display, String field, Object raw,
        ActivityPresentation fallback) {
        if (fallback == null) {
            display.put(field, raw);
            display.put(field + "ResolutionStatus", WarframeWorldStateResolutionStatus.UNRESOLVED.code());
            return;
        }
        display.putIfAbsent(field + "Key", raw);
        if (fallback.matchedKey() == null) {
            display.put(field, raw);
            display.put(field + "ResolutionStatus", WarframeWorldStateResolutionStatus.AMBIGUOUS.code());
            display.put(field + "Candidates", fallback.candidates());
            return;
        }
        display.put(field, raw);
        applyActivity(display, field, fallback);
        display.put(field + "ResolutionStatus", WarframeWorldStateResolutionStatus.SIMILAR_MATCH.code());
        display.put(field + "MatchedKey", fallback.matchedKey());
    }

    /**
     * 从官方 DT_ 代码还原游戏模式的业务键；只允许完整代码族和精确路径命中。
     *
     * @param type 官方下降模式代码。
     * @return 官方活动数据集中的对应分组路径；未知代码返回空。
     */
    private static String descentModeKey(Object type) {
        if (!(type instanceof String code) || !code.matches("DT_[A-Z0-9]+(?:_[A-Z0-9]+)*"))
            return null;
        StringBuilder key = new StringBuilder(DESCENT_MODES);
        for (String segment : code.substring(3).split("_")) {
            key.append(segment.charAt(0)).append(segment.substring(1).toLowerCase(Locale.ROOT));
        }
        return key.toString();
    }

    /**
     * 将已知科研类型映射到其任务变体分组，不凭名称猜测其他类型。
     *
     * @param type 官方科研类型。
     * @param value 上游任务偏差键。
     * @return 可直接查询的完整业务键；未知类型返回空。
     */
    private static String variantKey(String type, Object value) {
        String family = WarframeConquestType.variantGroup(type);
        return family == null || !(value instanceof String text) ? null : CONQUEST_VARIANTS + family + "/" + text;
    }

    /**
     * 补充有确定身份的名称和说明，不覆盖已经译好的中文。
     *
     * @param display 展示记录。
     * @param field 目标字段名。
     * @param row 已确认身份的活动描述。
     */
    private static void applyActivity(Map<String, Object> display, String field, ActivityPresentation row) {
        if (row == null)
            return;
        if (row.name() != null && !hasChinese(text(display.get(field)))) {
            display.put(field, row.name());
        }
        String detailField = "challenge".equals(field) ? "challengeDescription" : field + "Description";
        putIfAbsent(display, detailField, row.description());
    }

    /**
     * 逐项解析条件列表，同时保留原键到描述的映射供详情展示。
     *
     * @param display 展示记录。
     * @param field 条件字段。
     * @param prefix 活动定义分组。
     * @param original 原始条件列表。
     * @param activities 仅本次查询命中的已采纳活动记录。
     * @param path 当前记录在投影中的路径。
     * @param handledReferenceFields 本轮已解析或已由所属资料域诊断的字段。
     */
    private static void applyList(Map<String, Object> display, String field, String prefix, Object original,
        Map<String, ActivityPresentation> activities, String path, Set<String> handledReferenceFields) {
        if (!(original instanceof List<?> values))
            return;
        Map<String, String> descriptions = new LinkedHashMap<>();
        List<Object> names = new ArrayList<>();
        List<?> displayNames = display.get(field) instanceof List<?> list ? list : List.of();
        for (int index = 0; index < values.size(); index++) {
            Object value = values.get(index);
            if (!(value instanceof String key)) {
                names.add(index < displayNames.size() ? displayNames.get(index) : value);
                continue;
            }
            ActivityPresentation record = activities.get(normalize(prefix + key));
            String name = record == null ? null : record.name();
            Object current = index < displayNames.size() ? displayNames.get(index) : value;
            names.add(name == null ? current : name);
            if (name != null)
                handledReferenceFields.add(path + "." + field + "[" + index + "]");
            if (record != null && record.description() != null && !StrUtil.isBlank(record.description())) {
                descriptions.put(key, record.description());
            }
        }
        display.put(field, Collections.unmodifiableList(names));
        if ("personalModifiers".equals(field)) {
            List<Map<String, Object>> details = new ArrayList<>();
            for (int index = 0; index < values.size(); index++) {
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("name", names.get(index));
                if (values.get(index) instanceof String key && descriptions.containsKey(key)) {
                    String template = descriptions.get(key);
                    if (template.contains(CONQUEST_VALUE_TOKEN)) {
                        detail.put("descriptionTemplate", template);
                        detail.put("description", template.replace(CONQUEST_VALUE_TOKEN, "〔数值未提供〕"));
                    } else {
                        detail.put("description", template);
                    }
                }
                details.add(detail);
            }
            display.put("personalModifierDetails", details);
        }
        if (!descriptions.isEmpty()) {
            Map<String, Object> merged = new LinkedHashMap<>();
            if (display.get(field + "Descriptions") instanceof Map<?, ?> previous)
                merged.putAll(toStringKeys(previous));
            merged.putAll(descriptions);
            display.put(field + "Descriptions", merged);
        }
    }

    /**
     * 收集完整活动定义键，避免逐条补查。
     *
     * @param keys 目标键集合。
     * @param prefix 确认的官方分组。
     * @param values 单个原始键或键列表。
     */
    private static void add(Set<String> keys, String prefix, Object values) {
        if (values instanceof String value && !StrUtil.isBlank(value))
            keys.add(prefix + value);
        else if (values instanceof Collection<?> list)
            list.forEach(value -> add(keys, prefix, value));
    }

    private record FuzzyCandidate(String key, double score) {
    }

    private record ActivityPresentation(String name, String description, String matchedKey, List<String> candidates) {
    }
}
