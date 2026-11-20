package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialActivityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialBossDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialNightwaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialLanguageStringDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.runtime.constant.WarframeConquestType;
import com.nnm.bot.warframe.runtime.constant.WarframeDescentMatchPolicy;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialReferenceKeys;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants.UnkeyedRewardReference;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateRecordEnrichmentService;
import jakarta.annotation.Resource;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/** 使用已采纳官方／自维护记录补全管理端图片、科研条件与星图基础语境，不猜测歧义身份。 */
@Service
public class WarframeWorldStateRecordEnrichmentServiceImpl implements WarframeWorldStateRecordEnrichmentService {
    private static final Set<String> ITEM_FIELDS = Set.of("item", "itemKey", "reward", "uniqueName");
    private static final Set<String> CIRCUIT_TITLES = Set.of("普通回廊", "灵化轮换");
    private static final Set<String> NIGHTWAVE_TITLES = Set.of("电波", "午夜电波");
    private static final String NIGHTWAVE_PREFIX = "challenges/";
    private static final String SHARD_SELECTION_NOTE = "仅展示官方静态候选，实际源力石由账号与结算规则决定";
    private static final Map<String,
        WarframeOfficialDataDomain> STATIC_REFERENCES = Map.of("syndicate", WarframeOfficialDataDomain.SYNDICATE,
            "boss", WarframeOfficialDataDomain.BOSS, "faction", WarframeOfficialDataDomain.FACTION, "missionType",
            WarframeOfficialDataDomain.MISSION_TYPE, "modifier", WarframeOfficialDataDomain.FISSURE_MODIFIER,
            "upgradeType", WarframeOfficialDataDomain.UPGRADE_TYPE);
    private static final Set<String> RESEARCH_TITLES = Set.of("科研", "深层科研", "时光科研", "其他科研");
    private static final String DESCENT_TITLE = "深层下降";
    private static final String CONQUEST_TYPES = "conquests/types/";
    private static final String CONQUEST_CONDITIONS = "conquests/conditions/";
    private static final String CONQUEST_MODIFIERS = "conquests/personalModifiers/";
    private static final String CONQUEST_VARIANTS = "conquests/missionVariants/";
    private static final String DESCENT_CHALLENGES = "descents/challenges/";
    private static final String DESCENT_MODES = "descents/gameModes/";
    private static final String DESCENT_PROTOFRAMES = "descents/protoframes/";
    private static final String DESCENT_FALLBACK = "descents/fallback/";
    private static final List<String> DESCENT_GROUPS = List.of(DESCENT_CHALLENGES, DESCENT_MODES, DESCENT_PROTOFRAMES);

    @Resource
    private WarframeOfficialLookupService officialLookup;

    @Resource
    private WarframeImageResolver imageResolver;

    @Resource
    private WarframeWorldStateCodebookService codebookService;

    /**
     * 按已采纳静态数据批量补齐物品图片、活动说明和节点基础信息；原始快照保持不变。
     *
     * @param originals 名称翻译前的世界状态投影。
     * @param displays 名称翻译后的同序投影。
     * @return 带可用图片与说明的投影。
     */
    @Override
    public List<WarframeWorldStateCommandVO> enrich(List<WarframeWorldStateCommandVO> originals,
        List<WarframeWorldStateCommandVO> displays) {
        Set<String> keys = new LinkedHashSet<>();
        Set<String> unkeyedItems = new LinkedHashSet<>();
        originals.forEach(output -> {
            collect(output.data(), keys);
            collectUnkeyedItems(output.data(), unkeyedItems);
            if (CIRCUIT_TITLES.contains(output.data().get("title"))
                && output.data().get("items") instanceof Collection<?> rows) {
                rows.forEach(row -> {
                    if (row instanceof Map<?, ?> item && item.get("item") instanceof String name
                        && !CharSequenceUtil.isBlank(name))
                        keys.add(name);
                });
            }
        });
        // 奖励池阶段由后续已入库投影生成，不存在于原始官方任务行中。
        displays.forEach(output -> collectUnkeyedItems(output.data(), unkeyedItems));
        Map<WarframeOfficialDataDomain, Set<String>> referenceKeys = new EnumMap<>(WarframeOfficialDataDomain.class);
        originals.forEach(output -> {
            collectReferences(output.data(), referenceKeys);
            if (NIGHTWAVE_TITLES.contains(output.data().get("title"))) {
                collectNightwave(output.data(), referenceKeys.computeIfAbsent(WarframeOfficialDataDomain.NIGHTWAVE,
                    ignored -> new LinkedHashSet<>()));
            }
            if ("新闻".equals(output.data().get("title")) && output.data().get("items") instanceof Collection<?> rows) {
                for (Object row : rows) {
                    if (row instanceof Map<?, ?> map && map.get("title") instanceof String title
                        && title.startsWith("/Lotus/Language/")) {
                        referenceKeys.computeIfAbsent(WarframeOfficialDataDomain.LANGUAGE_STRING,
                            ignored -> new LinkedHashSet<>()).add(title);
                    }
                }
            }
        });
        Map<WarframeOfficialDataDomain, Map<String, WarframeOfficialRecordReadDTO>> references =
            new EnumMap<>(WarframeOfficialDataDomain.class);
        referenceKeys.forEach((domain, values) -> {
            Map<String, WarframeOfficialRecordReadDTO> found = officialLookup.records(domain, values);
            references.put(domain, found == null ? Map.of() : found);
        });
        Set<String> activityKeys = new LinkedHashSet<>();
        Set<String> nodeKeys = new LinkedHashSet<>();
        originals.forEach(output -> {
            String title = String.valueOf(output.data().get("title"));
            if (RESEARCH_TITLES.contains(title) || DESCENT_TITLE.equals(title)) {
                collectActivities(output.data(), title, null, activityKeys);
            }
            collectNodes(output.data(), nodeKeys);
        });
        boolean hasCycles =
            imageResolver != null && originals.stream().anyMatch(output -> "平原".equals(output.data().get("title")));
        if (keys.isEmpty() && unkeyedItems.isEmpty() && activityKeys.isEmpty() && nodeKeys.isEmpty()
            && referenceKeys.isEmpty() && !hasCycles) {
            return displays;
        }
        Map<String, WarframeOfficialRecordReadDTO> activityRecords = activityKeys.isEmpty() ? Map.of()
            : officialLookup.records(WarframeOfficialDataDomain.ACTIVITY, activityKeys);
        Map<String, ActivityPresentation> activities = new LinkedHashMap<>(activityPresentations(activityRecords));
        addDescentFallbacks(activityKeys, activities);
        Map<String, WarframeOfficialRecordReadDTO> nodes =
            nodeKeys.isEmpty() ? Map.of() : officialLookup.records(WarframeOfficialDataDomain.REGION, nodeKeys);
        Map<String, List<WarframeOfficialNameDTO>> matches = new LinkedHashMap<>();
        for (WarframeOfficialDataDomain domain : WarframeOfficialDataDomain.values()) {
            if (keys.isEmpty() || (!domain.itemIndex() && domain != WarframeOfficialDataDomain.ITEM_LOCALIZATION)) {
                continue;
            }
            var index = officialLookup.nameIndex(domain);
            for (String key : keys) {
                // 回廊只有英文装备名；不从其他同名数据集猜测武器或战甲身份。
                if (!key.startsWith("/") && domain != WarframeOfficialDataDomain.WARFRAME
                    && domain != WarframeOfficialDataDomain.WEAPON)
                    continue;
                for (String identity : index.matches(domain, key)) {
                    WarframeOfficialNameDTO name = index.items().get(identity);
                    if (name != null) {
                        matches.computeIfAbsent(key, ignored -> new ArrayList<>()).add(name);
                    }
                }
            }
        }
        matches.replaceAll(WarframeWorldStateRecordEnrichmentServiceImpl::uniqueItemIdentity);
        Map<WarframeOfficialDataDomain, Set<Long>> ids = new LinkedHashMap<>();
        matches.forEach((key, names) -> {
            if (names.stream().map(WarframeOfficialNameDTO::bkey).distinct().count() != 1) {
                return; // 不从多个同名游戏物品中猜测图标。
            }
            names.forEach(name -> ids.computeIfAbsent(WarframeOfficialDataDomain.fromCode(name.domainCode()),
                ignored -> new LinkedHashSet<>()).add(name.databaseId()));
        });
        Map<WarframeOfficialDataDomain, Map<Long, WarframeOfficialRecordReadDTO>> records =
            new EnumMap<>(WarframeOfficialDataDomain.class);
        ids.forEach((domain, recordIds) -> {
            if (domain != WarframeOfficialDataDomain.OTHER) {
                records.put(domain, officialLookup.recordsByIds(domain, recordIds));
            }
        });
        Map<String, RecordPresentation> presentations = new LinkedHashMap<>();
        matches.forEach((key, names) -> {
            if (names.stream().map(WarframeOfficialNameDTO::bkey).distinct().count() != 1) {
                return;
            }
            for (WarframeOfficialNameDTO name : names) {
                WarframeOfficialRecordReadDTO record =
                    records.getOrDefault(WarframeOfficialDataDomain.fromCode(name.domainCode()), Map.of())
                        .get(name.databaseId());
                if (record == null) {
                    continue;
                }
                String image = adoptedIcon(record);
                String description = first(record.description(), record.longDescription());
                if (image != null && image.startsWith("https://")) {
                    presentations.put(key, new RecordPresentation(first(record.localizedName(), record.officialName()),
                        image, description));
                    break;
                }
                String label = first(record.localizedName(), record.officialName());
                if (label != null || (description != null && !CharSequenceUtil.isBlank(description))) {
                    presentations.putIfAbsent(key, new RecordPresentation(label, null, description));
                }
            }
        });
        // 配方不在全域名称索引中；只按完整游戏路径查询已入库的 RECIPE 记录补图。
        Set<String> recipes = new LinkedHashSet<>();
        for (String key : keys) {
            for (String candidate : WarframeOfficialReferenceKeys.candidates(key))
                if (candidate.startsWith(WarframeWorldStateDisplayConstants.RECIPE_PREFIX))
                    recipes.add(candidate);
        }
        if (!recipes.isEmpty()) {
            var recordsByKey = officialLookup.records(WarframeOfficialDataDomain.RECIPE, recipes);
            if (recordsByKey != null) {
                for (String key : keys) {
                    var existing = presentations.get(key);
                    if (existing != null && existing.imageUrl() != null)
                        continue;
                    for (String candidate : WarframeOfficialReferenceKeys.candidates(key)) {
                        var record = recordsByKey.get(normalize(candidate));
                        if (record == null)
                            continue;
                        String icon = adoptedIcon(record);
                        if (icon == null)
                            continue;
                        presentations.put(key,
                            new RecordPresentation(first(record.localizedName(),
                                existing == null ? null : existing.name(), record.officialName()), icon,
                                existing == null ? null : existing.description()));
                        break;
                    }
                }
            }
        }
        // 无路径奖励不能用模糊同名索引推断身份；只用固定官方记录的已入库名称和图标。
        for (UnkeyedRewardReference reference : UnkeyedRewardReference.values()) {
            if (unkeyedItems.isEmpty())
                break;
            var found = officialLookup.records(reference.domain(), List.of(reference.key()));
            var record = found == null ? null : found.get(normalize(reference.key()));
            if (record == null)
                continue;
            String icon = adoptedIcon(record);
            if (icon == null)
                continue;
            for (String name : new String[] {record.localizedName(), record.officialName()}) {
                if (name != null && unkeyedItems.contains(name))
                    presentations.putIfAbsent(name, new RecordPresentation(name, icon, null));
            }
        }
        Map<String, String> creditBundles = new LinkedHashMap<>();
        for (String key : keys) {
            for (String candidate : WarframeOfficialReferenceKeys.candidates(key)) {
                if (WarframeWorldStateDisplayConstants.isNumericCreditBundle(candidate)) {
                    creditBundles.put(key, candidate);
                    break;
                }
            }
        }
        if (imageResolver != null && !creditBundles.isEmpty()) {
            // 仅给真实存在于已采纳 CREDIT_BUNDLE 表、且本身仍缺图的现金商品补通用图标。
            var bundles = officialLookup.records(WarframeOfficialDataDomain.CREDIT_BUNDLE,
                new LinkedHashSet<>(creditBundles.values()));
            if (bundles != null && !bundles.isEmpty()) {
                String icon = imageResolver.resolve(WarframeWorldStateDisplayConstants.GENERIC_CREDIT_ICON, null);
                if (icon != null && icon.startsWith("https://")) {
                    creditBundles.forEach((key, candidate) -> {
                        if (!bundles.containsKey(normalize(candidate)))
                            return;
                        var existing = presentations.get(key);
                        if (existing == null || existing.imageUrl() == null)
                            presentations.put(key, new RecordPresentation(existing == null ? null : existing.name(),
                                icon, existing == null ? null : existing.description()));
                    });
                }
            }
        }
        // 只有已采纳蓝图记录确实缺图时，才用已采纳的对应成品图片；蓝图名称不变。
        String blueprintKey = WarframeWorldStateDisplayConstants.UMBRA_FORMA_BLUEPRINT;
        RecordPresentation blueprint = presentations.get(blueprintKey);
        if (blueprint != null && blueprint.imageUrl() == null) {
            String productKey = WarframeWorldStateDisplayConstants.UMBRA_FORMA_PRODUCT;
            var product = officialLookup.records(WarframeOfficialDataDomain.ITEM_LOCALIZATION, List.of(productKey))
                .get(normalize(productKey));
            String icon = product == null ? null : WarframeOfficialValues.iconUrl(product.data());
            if (icon != null && icon.startsWith("https://"))
                presentations.put(blueprintKey,
                    new RecordPresentation(blueprint.name(), icon, blueprint.description()));
        }
        List<WarframeWorldStateCommandVO> result = new ArrayList<>(displays.size());
        for (int index = 0; index < displays.size(); index++) {
            var display = displays.get(index);
            Map<String, Object> enriched = decorate(originals.get(index).data(), display.data(), presentations,
                activities, nodes, references, String.valueOf(originals.get(index).data().get("title")), null);
            if ("平原".equals(originals.get(index).data().get("title")) && imageResolver != null) {
                enriched = addCycleImages(enriched);
            }
            result.add(new WarframeWorldStateCommandVO(display.text(), enriched, display.unresolvedReferences()));
        }
        return result;
    }

    /**
     * 从已采纳官方图片表按路径解析各平原景观，不固定写死内容哈希。
     *
     * @param data 平原动态状态展示副本。
     * @return 附带能够核实的图片地址的展示副本。
     */
    private Map<String, Object> addCycleImages(Map<String, Object> data) {
        Map<String, Object> result = new LinkedHashMap<>(data);
        if (!(data.get("items") instanceof List<?> entries))
            return result;
        List<Object> items = new ArrayList<>(entries.size());
        for (Object entry : entries) {
            if (!(entry instanceof Map<?, ?> source)) {
                items.add(entry);
                continue;
            }
            Map<String, Object> item = toStringKeys(source);
            if (item.get("name") instanceof String name) {
                String reference = WarframeWorldStateDisplayConstants.OPEN_WORLD_IMAGES.get(name);
                String image = reference == null ? null : imageResolver.resolve(reference, null);
                if (image != null && image.startsWith("https://"))
                    item.put("imageUrl", image);
            }
            items.add(Collections.unmodifiableMap(item));
        }
        result.put("items", List.copyOf(items));
        return Collections.unmodifiableMap(result);
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
                presentations.put(normalize(DESCENT_FALLBACK + reference), new ActivityPresentation(null, null, null));
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
                presentations.put(normalize(key), new ActivityPresentation(row.name(), row.description(), resolved));
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
                if (!containsChinese(first(activity.getNameZh(), row.localizedName()))
                    && text(activity.getNameKey()) != null)
                    languageKeys.add(activity.getNameKey());
                if (!containsChinese(first(activity.getDescriptionZh(), row.description()))
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
            if (!containsChinese(name))
                name = first(translatedName, name);
            if (!containsChinese(description))
                description = first(translatedDescription, description);
            result.put(normalize(key), new ActivityPresentation(name, description, null));
        });
        return Map.copyOf(result);
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
     * 同一物品的商店路径与游戏路径并存时，优先选已采纳的规范游戏路径。 只按完整身份匹配，不把相似英文名误认为同一物品。
     *
     * @param reference 原始物品引用。
     * @param matches 官方名称索引命中的候选。
     * @return 唯一完整身份对应的各官方记录，歧义时为空。
     */
    private static List<WarframeOfficialNameDTO> uniqueItemIdentity(String reference,
        List<WarframeOfficialNameDTO> matches) {
        List<String> candidates = WarframeOfficialReferenceKeys.candidates(reference);
        for (int index = candidates.size() - 1; index >= 0; index--) {
            String key = candidates.get(index);
            List<WarframeOfficialNameDTO> exact =
                matches.stream().filter(name -> key.equalsIgnoreCase(name.bkey())).toList();
            if (!exact.isEmpty())
                return exact;
        }
        return matches.stream().map(WarframeOfficialNameDTO::bkey).distinct().count() == 1 ? matches : List.of();
    }

    /**
     * 收集没有游戏路径的奖励名称，只用于与已入库的明确身份核对。
     *
     * @param value 原始展示字段。
     * @param names 收集到的非路径奖励名称。
     */
    private static void collectUnkeyedItems(Object value, Set<String> names) {
        if (value instanceof Map<?, ?> map) {
            if (map.get("itemKey") == null && map.get("count") != null && map.get("displayName") instanceof String label
                && !CharSequenceUtil.isBlank(label))
                names.add(label);
            map.forEach((key, child) -> {
                if (ITEM_FIELDS.contains(String.valueOf(key)) && child instanceof String text
                    && !CharSequenceUtil.isBlank(text) && !text.startsWith("/"))
                    names.add(text);
                collectUnkeyedItems(child, names);
            });
        } else if (value instanceof Collection<?> collection) {
            collection.forEach(child -> collectUnkeyedItems(child, names));
        }
    }

    /**
     * 优先使用物品自身的已采纳图标；缺少公开 URL 时仅从已采纳 IMAGE 表解析其图标路径。
     *
     * @param record 已采纳的官方物品记录。
     * @return HTTPS 图标地址，不存在时为空。
     */
    private String adoptedIcon(WarframeOfficialRecordReadDTO record) {
        String icon = WarframeOfficialValues.iconUrl(record.data());
        if (icon != null && icon.startsWith("https://"))
            return icon;
        if (imageResolver == null)
            return null;
        icon = imageResolver.resolve(WarframeOfficialValues.icon(record.data()), null);
        return icon != null && icon.startsWith("https://") ? icon : null;
    }

    /**
     * 从一次快照的原始投影中收集有明确路径的物品引用。
     *
     * @param value 当前业务字段或嵌套集合。
     * @param keys 去重后的物品路径。
     */
    private static void collect(Object value, Set<String> keys) {
        if (value instanceof Map<?, ?> map) {
            map.forEach((key, child) -> {
                if (ITEM_FIELDS.contains(String.valueOf(key)) && child instanceof String text && text.startsWith("/")) {
                    keys.add(text);
                }
                collect(child, keys);
            });
        } else if (value instanceof Collection<?> collection) {
            collection.forEach(child -> collect(child, keys));
        }
    }

    /**
     * 从原始投影收集官方静态业务键；类型和派系优先读取原始代码而非展示译名。
     *
     * @param value 可能嵌套的原始世界状态。
     * @param keys 按官方数据集组织的去重键。
     */
    private static void collectReferences(Object value, Map<WarframeOfficialDataDomain, Set<String>> keys) {
        if (value instanceof Map<?, ?> map) {
            STATIC_REFERENCES.forEach((field, domain) -> {
                String key = text(map.get(field + "Key"));
                if (key == null)
                    key = text(map.get(field));
                if (key != null)
                    keys.computeIfAbsent(domain, ignored -> new LinkedHashSet<>()).add(key);
            });
            map.values().forEach(child -> collectReferences(child, keys));
        } else if (value instanceof Collection<?> children) {
            children.forEach(child -> collectReferences(child, keys));
        }
    }

    /**
     * 只收集电波区域的原始挑战路径，不把深层下降的同名挑战误识为夜波。
     *
     * @param value 当前电波展示树。
     * @param keys 当前请求的官方夜波定义键。
     */
    private static void collectNightwave(Object value, Set<String> keys) {
        if (value instanceof Map<?, ?> map) {
            if (map.get("challenge") instanceof String key && key.startsWith("/"))
                keys.add(NIGHTWAVE_PREFIX + key);
            map.values().forEach(child -> collectNightwave(child, keys));
        } else if (value instanceof Collection<?> children) {
            children.forEach(child -> collectNightwave(child, keys));
        }
    }

    /**
     * 使用原始身份在展示副本上补齐图片与说明，原始内容不变。
     *
     * @param original 翻译前的字段。
     * @param display 名称解析后的字段。
     * @param presentations 已核实身份的可展示资料。
     * @param activities 已采纳的科研和下降条件说明。
     * @param nodes 与动态节点同键的官方星图记录。
     * @param references 按数据集和原始代码确定的静态业务引用。
     * @param section 当前业务分区。
     * @param inheritedType 上层科研类型，用于定位变体定义。
     * @return 不可变的展示字段副本。
     */
    private static Map<String, Object> decorate(Map<String, Object> original, Map<String, Object> display,
        Map<String, RecordPresentation> presentations, Map<String, ActivityPresentation> activities,
        Map<String, WarframeOfficialRecordReadDTO> nodes,
        Map<WarframeOfficialDataDomain, Map<String, WarframeOfficialRecordReadDTO>> references, String section,
        String inheritedType) {
        Map<String, Object> copy = new LinkedHashMap<>();
        String type = text(original.get("typeKey"));
        if (type == null && text(original.get("type")) != null && text(original.get("type")).startsWith("CT_")) {
            type = text(original.get("type"));
        }
        String researchType = type == null ? inheritedType : type;
        for (var field : display.entrySet()) {
            copy.put(field.getKey(), decorateValue(original.get(field.getKey()), field.getValue(), presentations,
                activities, nodes, references, section, researchType));
        }
        for (String key : ITEM_FIELDS) {
            if (original.get(key) instanceof String reference) {
                RecordPresentation record = presentations.get(reference);
                if (record == null) {
                    continue;
                }
                if (record.name() != null && Objects.equals(copy.get(key), reference)
                    && !Objects.equals(record.name(), reference))
                    copy.put(key, record.name());
                if (record.imageUrl() != null && (copy.get("imageUrl") == null || "".equals(copy.get("imageUrl")))) {
                    copy.put("imageUrl", record.imageUrl());
                }
                if (record.description() != null && !CharSequenceUtil.isBlank(record.description())
                    && (copy.get("description") == null || "".equals(copy.get("description")))) {
                    copy.put("description", record.description());
                }
            }
        }
        // 奖励表以 displayName 保存无物品路径的货币名称，不能把同名但有路径的物品替换掉。
        if (copy.get("imageUrl") == null && original.get("itemKey") == null && original.get("count") != null
            && copy.get("displayName") instanceof String label) {
            RecordPresentation record = presentations.get(label);
            if (record != null && record.imageUrl() != null)
                copy.put("imageUrl", record.imageUrl());
        }
        STATIC_REFERENCES.forEach((field, domain) -> {
            String reference = text(original.get(field + "Key"));
            if (reference == null)
                reference = text(original.get(field));
            WarframeOfficialRecordReadDTO record = references.getOrDefault(domain, Map.of()).get(normalize(reference));
            if (record == null)
                return;
            String name = first(record.localizedName(), record.officialName());
            if (name != null && !containsChinese(text(copy.get(field))))
                copy.put(field, name);
            String description = first(record.description(), record.longDescription());
            putIfAbsent(copy, field + "Description", description);
            if ("syndicate".equals(field) || "boss".equals(field))
                putIfAbsent(copy, "description", description);
            String image = WarframeOfficialValues.iconUrl(record.data());
            if (image != null && image.startsWith("https://"))
                putIfAbsent(copy, "imageUrl", image);
            if ("boss".equals(field) && record.data() instanceof OfficialBossDTO boss
                && boss.getShardCategoryVariants() != null) {
                // 官方只给候选类型，不推断特定玩家本周获得的源力石或融造结果。
                List<Map<String, Object>> variants = boss.getShardCategoryVariants().stream()
                    .filter(variant -> first(variant.getNameZh(), variant.getNameEn()) != null)
                    .map(variant -> Map.<String, Object>of("name", first(variant.getNameZh(), variant.getNameEn()),
                        "tauforged", Boolean.TRUE.equals(variant.getTauforged())))
                    .toList();
                if (!variants.isEmpty()) {
                    copy.putIfAbsent("possibleShardVariants", variants);
                    copy.putIfAbsent("shardSelectionStatus", SHARD_SELECTION_NOTE);
                }
            }
        });
        if ("新闻".equals(section) && original.get("title") instanceof String title
            && title.startsWith("/Lotus/Language/")) {
            var row =
                references.getOrDefault(WarframeOfficialDataDomain.LANGUAGE_STRING, Map.of()).get(normalize(title));
            if (row != null && row.data() instanceof OfficialLanguageStringDTO language
                && containsChinese(language.getZhText()))
                copy.put("title", language.getZhText());
        }
        if (NIGHTWAVE_TITLES.contains(section) && original.get("challenge") instanceof String challenge) {
            var row = references.getOrDefault(WarframeOfficialDataDomain.NIGHTWAVE, Map.of())
                .get(normalize(NIGHTWAVE_PREFIX + challenge));
            if (row != null && row.data() instanceof OfficialNightwaveDTO nightwave) {
                String name =
                    first(nightwave.getNameZh(), row.localizedName(), nightwave.getNameEn(), row.officialName());
                if (name != null && !containsChinese(text(copy.get("title"))))
                    copy.put("title", name);
                String description =
                    first(nightwave.getDescriptionZh(), row.description(), nightwave.getDescriptionEn());
                if (description != null && !containsChinese(text(copy.get("description")))) {
                    if (nightwave.getRequired() != null)
                        description = description.replace("|COUNT|",
                            nightwave.getRequired().stripTrailingZeros().toPlainString());
                    copy.put("description", description);
                }
                putIfAbsent(copy, "tip", first(nightwave.getTipZh(), nightwave.getTipEn()));
                if (nightwave.getRequired() != null && copy.get("requiredCount") == null)
                    copy.put("requiredCount", nightwave.getRequired());
                String icon = nightwave.getIconUrl();
                if (icon != null && icon.startsWith("https://"))
                    putIfAbsent(copy, "imageUrl", icon);
            }
        }
        if (RESEARCH_TITLES.contains(section) || DESCENT_TITLE.equals(section)) {
            decorateActivities(original, copy, activities, section, researchType);
        }
        if (original.get("node") instanceof String node) {
            WarframeOfficialRecordReadDTO record = nodes.get(normalize(node));
            if (record != null && record.data() instanceof OfficialRegionDTO region) {
                putIfAbsent(copy, "systemName", first(region.getZhSystemName(), region.getSystemName()));
                // 星图是基础语境，不覆盖动态任务类型与当前裂隙实际任务。
                if (text(original.get("missionType")) == null) {
                    putIfAbsent(copy, "baseMissionType",
                        first(region.getMissionNameZh(), region.getMissionTypeNameZh(), region.getMissionTypeName()));
                }
                if (original.get("faction") == null) {
                    putIfAbsent(copy, "baseFaction", first(region.getFactionNameZh(), region.getFactionName()));
                }
            }
        }
        return Collections.unmodifiableMap(copy);
    }

    /**
     * 沿相同字段与列表顺序遍历原始值和展示值。
     *
     * @param original 原始字段值。
     * @param display 已解析的字段值。
     * @param presentations 仅确认身份的资料。
     * @param activities 当前查得的科研与下降条件定义。
     * @param nodes 当前查得的星图节点。
     * @param references 按源代码查得的静态业务定义。
     * @param section 当前业务分区。
     * @param researchType 上层科研类型。
     * @return 当前字段的展示副本。
     */
    private static Object decorateValue(Object original, Object display, Map<String, RecordPresentation> presentations,
        Map<String, ActivityPresentation> activities, Map<String, WarframeOfficialRecordReadDTO> nodes,
        Map<WarframeOfficialDataDomain, Map<String, WarframeOfficialRecordReadDTO>> references, String section,
        String researchType) {
        // 仅展示投影新增的字段以自身作为身份；否则无法补齐后加的赏金阶段奖励。
        if (original == null && (display instanceof Map<?, ?> || display instanceof List<?>))
            original = display;
        if (original instanceof Map<?, ?> originalMap && display instanceof Map<?, ?> displayMap) {
            return decorate(toStringKeys(originalMap), toStringKeys(displayMap), presentations, activities, nodes,
                references, section, researchType);
        }
        if (original instanceof List<?> originalList && display instanceof List<?> displayList) {
            List<Object> result = new ArrayList<>(displayList.size());
            for (int index = 0; index < displayList.size(); index++) {
                result.add(decorateValue(index < originalList.size() ? originalList.get(index) : null,
                    displayList.get(index), presentations, activities, nodes, references, section, researchType));
            }
            return Collections.unmodifiableList(result);
        }
        return display;
    }

    /**
     * 将已经序列化的展示映射统一为文本键。
     *
     * @param source 已解析的业务映射。
     * @return 保留原值的文本键映射。
     */
    private static Map<String, Object> toStringKeys(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    /**
     * 按科研类型和深层下降分组收集可证实的活动记录业务键。
     *
     * @param value 原始投影中的嵌套值。
     * @param section 当前世界状态分区。
     * @param inheritedType 上层科研类型。
     * @param keys 同次查询所需业务键。
     */
    private static void collectActivities(Object value, String section, String inheritedType, Set<String> keys) {
        if (value instanceof Map<?, ?> map) {
            String type = text(map.get("typeKey"));
            if (type == null && text(map.get("type")) != null && text(map.get("type")).startsWith("CT_")) {
                type = text(map.get("type"));
            }
            String researchType = type == null ? inheritedType : type;
            if (RESEARCH_TITLES.contains(section)) {
                if (type != null)
                    keys.add(CONQUEST_TYPES + type);
                add(keys, CONQUEST_CONDITIONS, map.get("risks"));
                add(keys, CONQUEST_MODIFIERS, map.get("personalModifiers"));
                String family = WarframeConquestType.DEEP.matches(researchType) ? "LabConquest"
                    : WarframeConquestType.HEX.matches(researchType) ? "HexConquest" : null;
                if (family != null)
                    add(keys, CONQUEST_VARIANTS + family + "/", map.get("deviation"));
            } else if (DESCENT_TITLE.equals(section)) {
                String mode = descentModeKey(map.get("type"));
                if (mode != null)
                    keys.add(mode);
                for (String group : DESCENT_GROUPS)
                    add(keys, group, map.get("challenge"));
            }
            for (Object child : map.values())
                collectActivities(child, section, researchType, keys);
        } else if (value instanceof Collection<?> list) {
            list.forEach(child -> collectActivities(child, section, inheritedType, keys));
        }
    }

    /**
     * 收集本次快照引用的星图节点，使用数据库中已采纳的静态记录。
     *
     * @param value 原始投影值。
     * @param nodes 节点键集合。
     */
    private static void collectNodes(Object value, Set<String> nodes) {
        if (value instanceof Map<?, ?> map) {
            if (map.get("node") instanceof String node && !CharSequenceUtil.isBlank(node))
                nodes.add(node);
            map.values().forEach(child -> collectNodes(child, nodes));
        } else if (value instanceof Collection<?> list) {
            list.forEach(child -> collectNodes(child, nodes));
        }
    }

    /**
     * 只在确切匹配已采纳活动身份时补充名称和说明，不猜测类型代码。
     *
     * @param original 保留游戏原始键的记录。
     * @param display 已本地化的展示副本。
     * @param activities 同次查出的活动记录。
     * @param section 所属业务分区。
     * @param type 当前科研类型。
     */
    private static void decorateActivities(Map<String, Object> original, Map<String, Object> display,
        Map<String, ActivityPresentation> activities, String section, String type) {
        if (RESEARCH_TITLES.contains(section)) {
            applyActivity(display, "type", activities.get(normalize(CONQUEST_TYPES + type)));
            applyActivity(display, "deviation", activities.get(normalize(variantKey(type, original.get("deviation")))));
            applyList(display, "risks", CONQUEST_CONDITIONS, original.get("risks"), activities);
            applyList(display, "personalModifiers", CONQUEST_MODIFIERS, original.get("personalModifiers"), activities);
        } else if (DESCENT_TITLE.equals(section)) {
            String mode = descentModeKey(original.get("type"));
            ActivityPresentation directMode = activities.get(normalize(mode));
            if (directMode != null) {
                applyActivity(display, "type", directMode);
                display.putIfAbsent("typeResolutionStatus", "direct");
            } else if (mode != null)
                applyDescentFallback(display, "type", original.get("type"),
                    activities.get(normalize(DESCENT_FALLBACK + mode)));
            if (original.get("challenge") instanceof String challenge) {
                List<ActivityPresentation> matches = DESCENT_GROUPS.stream()
                    .map(prefix -> activities.get(normalize(prefix + challenge))).filter(Objects::nonNull).toList();
                if (matches.size() == 1) {
                    applyActivity(display, "challenge", matches.getFirst());
                    display.putIfAbsent("challengeResolutionStatus", "direct");
                } else if (matches.isEmpty())
                    applyDescentFallback(display, "challenge", challenge,
                        activities.get(normalize(DESCENT_FALLBACK + DESCENT_CHALLENGES + challenge)));
                else
                    display.put("challengeResolutionStatus", "ambiguous");
            }
        }
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
            display.putIfAbsent(field + "ResolutionStatus", "unresolved");
            return;
        }
        if (fallback.matchedKey() == null) {
            display.putIfAbsent(field + "ResolutionStatus", "ambiguous");
            return;
        }
        display.putIfAbsent(field + "Key", raw);
        applyActivity(display, field, fallback);
        display.put(field + "ResolutionStatus", "fallback");
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
        String family = WarframeConquestType.DEEP.matches(type) ? "LabConquest"
            : WarframeConquestType.HEX.matches(type) ? "HexConquest" : null;
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
        if (row.name() != null && !containsChinese(text(display.get(field)))) {
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
     */
    private static void applyList(Map<String, Object> display, String field, String prefix, Object original,
        Map<String, ActivityPresentation> activities) {
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
            names.add(name != null && !containsChinese(text(current)) ? name : current);
            if (record != null && record.description() != null && !CharSequenceUtil.isBlank(record.description())) {
                descriptions.put(key, record.description());
            }
        }
        display.put(field, Collections.unmodifiableList(names));
        if ("personalModifiers".equals(field)) {
            List<Map<String, Object>> details = new ArrayList<>();
            for (int index = 0; index < values.size(); index++) {
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("name", names.get(index));
                if (values.get(index) instanceof String key && descriptions.containsKey(key))
                    detail.put("description", descriptions.get(key));
                details.add(detail);
            }
            display.put("personalModifierDetails", details);
        }
        if (!descriptions.isEmpty()
            && (!(display.get(field + "Descriptions") instanceof Map<?, ?> previous) || previous.isEmpty())) {
            display.put(field + "Descriptions", descriptions);
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
        if (values instanceof String value && !CharSequenceUtil.isBlank(value))
            keys.add(prefix + value);
        else if (values instanceof Collection<?> list)
            list.forEach(value -> add(keys, prefix, value));
    }

    /**
     * 仅给缺少说明的展示记录补齐已确认的静态信息。
     *
     * @param display 当前展示行。
     * @param key 要补充的展示字段。
     * @param value 已核实的文本值。
     */
    private static void putIfAbsent(Map<String, Object> display, String key, String value) {
        if (value != null && !CharSequenceUtil.isBlank(value)
            && (display.get(key) == null || "".equals(display.get(key)))) {
            display.put(key, value);
        }
    }

    /**
     * 对齐 Repository 返回的大小写不敏感业务键。
     *
     * @param value 官方业务键。
     * @return 查询投影使用的规范键。
     */
    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    /**
     * 从原始字段中读取非空文本。
     *
     * @param value 待提取的字段值。
     * @return 非空文本或空。
     */
    private static String text(Object value) {
        return value instanceof String string && !CharSequenceUtil.isBlank(string) ? string : null;
    }

    /**
     * 按本地化优先级取第一个有效文本。
     *
     * @param values 按优先级排列的候选。
     * @return 首个有效文本或空。
     */
    private static String first(String... values) {
        for (String value : values)
            if (value != null && !CharSequenceUtil.isBlank(value))
                return value;
        return null;
    }

    /**
     * 判断已有展示文本是否已包含中文，避免英文覆盖中文。
     *
     * @param value 展示字段。
     * @return 已有中文时为真。
     */
    private static boolean containsChinese(String value) {
        return value != null && value.codePoints().anyMatch(code -> code >= 0x4e00 && code <= 0x9fff);
    }

    private record FuzzyCandidate(String key, double score) {
    }

    private record RecordPresentation(String name, String imageUrl, String description) {
    }

    private record ActivityPresentation(String name, String description, String matchedKey) {
    }
}
