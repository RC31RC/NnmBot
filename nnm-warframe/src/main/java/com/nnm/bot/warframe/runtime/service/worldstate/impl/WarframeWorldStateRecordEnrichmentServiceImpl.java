package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import static com.nnm.bot.warframe.normalization.WarframeNameNormalizer.hasChinese;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.first;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.normalize;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.putIfAbsent;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.text;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.toStringKeys;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityMappingScope;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialRecipeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialBossDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialNightwaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialLanguageStringDTO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialReferenceKeys;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateRecordEnrichmentService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 使用已采纳官方／自维护记录补全管理端图片、科研条件与星图基础语境，不猜测歧义身份。 */
@Service
@Slf4j
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
    private static final String SYNDICATE_MISSIONS_TITLE = "集团任务";
    private static final String CREDIT_BUNDLE_ART = "credit-bundle";
    private static final List<WarframeOfficialDataDomain> UNKEYED_REWARD_DOMAINS =
        List.of(WarframeOfficialDataDomain.FUSION_BUNDLES, WarframeOfficialDataDomain.RESOURCES);

    @Resource
    private WarframeOfficialLookupService officialLookup;

    @Resource
    private WarframeImageResolver imageResolver;

    @Resource
    private WarframeWorldStateCodebookService codebookService;

    /** 只提供经人工核对的奖励身份和借图关系，不直接提供图标地址。 */
    @Resource
    private WarframeCommunityCatalogService communityCatalog;

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
        displays.forEach(output -> collect(output.data(), keys));
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
        var activities = new WarframeWorldStateActivityProjection(originals, officialLookup, codebookService);
        Set<String> nodeKeys = new LinkedHashSet<>();
        originals.forEach(output -> collectNodes(output.data(), nodeKeys));
        boolean hasCycles =
            imageResolver != null && originals.stream().anyMatch(output -> "平原".equals(output.data().get("title")));
        if (keys.isEmpty() && unkeyedItems.isEmpty() && !activities.requested() && nodeKeys.isEmpty()
            && referenceKeys.isEmpty() && !hasCycles) {
            return displays;
        }
        Map<WarframeCommunityMappingScope, Map<String, List<String>>> assetMappings =
            keys.isEmpty() && unkeyedItems.isEmpty() && !hasCycles ? Map.of() : readAssetMappings();
        Map<String, WarframeOfficialRecordReadDTO> nodes =
            nodeKeys.isEmpty() ? Map.of() : officialLookup.records(WarframeOfficialDataDomain.REGION, nodeKeys);
        Map<String, List<WarframeOfficialNameDTO>> matches = new LinkedHashMap<>();
        for (WarframeOfficialDataDomain domain : WarframeOfficialDataDomain.values()) {
            if (keys.isEmpty() || domain == WarframeOfficialDataDomain.REGION
                || (!domain.itemIndex() && domain != WarframeOfficialDataDomain.ITEM_LOCALIZATION)) {
                continue;
            }
            var index = officialLookup.nameIndex(domain);
            for (String key : keys) {
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
        addRecipeIcons(keys, presentations);
        addUnkeyedRewardIcons(unkeyedItems, assetMappings, presentations);
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
                String icon = approvedImage(assetMappings, CREDIT_BUNDLE_ART);
                if (StrUtil.startWith(icon, "https://")) {
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
        addRelatedItemIcons(keys, assetMappings, presentations);
        List<WarframeWorldStateCommandVO> result = new ArrayList<>(displays.size());
        for (int index = 0; index < displays.size(); index++) {
            var display = displays.get(index);
            Set<String> handledReferenceFields = new LinkedHashSet<>();
            Map<String, Object> enriched =
                decorate(originals.get(index).data(), display.data(), presentations, activities, nodes, references,
                    String.valueOf(originals.get(index).data().get("title")), null, "", handledReferenceFields);
            if ("平原".equals(originals.get(index).data().get("title")) && imageResolver != null) {
                enriched = addCycleImages(enriched, assetMappings);
            }
            var unresolved = display.unresolvedReferences().stream()
                .filter(reference -> !handledReferenceFields.contains(reference.fieldPath())).toList();
            result.add(new WarframeWorldStateCommandVO(display.text(), enriched, unresolved));
        }
        return result;
    }

    private Map<WarframeCommunityMappingScope, Map<String, List<String>>> readAssetMappings() {
        try {
            return communityCatalog.mappingTargets();
        } catch (RuntimeException exception) {
            log.warn("World state asset mappings unavailable; retaining official presentation", exception);
            return Map.of();
        }
    }

    private void addUnkeyedRewardIcons(Set<String> labels, Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings,
        Map<String, RecordPresentation> presentations) {
        Map<String, String> targets = new LinkedHashMap<>();
        labels.forEach(label -> WarframeCommunityMappingScope.REWARD_IDENTITY.target(mappings, label)
            .ifPresent(target -> targets.put(label, target)));
        if (targets.isEmpty())
            return;
        Set<String> keys = new LinkedHashSet<>(targets.values());
        for (var domain : UNKEYED_REWARD_DOMAINS) {
            var records = officialLookup.records(domain, keys);
            targets.forEach((label, target) -> {
                var record = records.get(normalize(target));
                String icon = record == null ? null : adoptedIcon(record);
                if (StrUtil.isNotBlank(icon))
                    presentations.putIfAbsent(label, new RecordPresentation(label, icon, null));
            });
        }
    }

    private void addRelatedItemIcons(Set<String> keys, Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings,
        Map<String, RecordPresentation> presentations) {
        Map<String, String> targets = new LinkedHashMap<>();
        for (String key : keys) {
            var presentation = presentations.get(key);
            if (key.startsWith("/") && (presentation == null || StrUtil.isBlank(presentation.imageUrl()))) {
                String canonical = WarframeOfficialReferenceKeys.candidates(key).getLast();
                WarframeCommunityMappingScope.ITEM_ICON.target(mappings, canonical)
                    .ifPresent(target -> targets.put(key, target));
            }
        }
        if (targets.isEmpty())
            return;
        var records =
            officialLookup.records(WarframeOfficialDataDomain.ITEM_LOCALIZATION, new LinkedHashSet<>(targets.values()));
        targets.forEach((key, target) -> {
            var record = records.get(normalize(target));
            String icon = record == null ? null : adoptedIcon(record);
            if (StrUtil.isNotBlank(icon)) {
                var original = presentations.get(key);
                presentations.put(key, new RecordPresentation(original == null ? null : original.name(), icon,
                    original == null ? null : original.description()));
            }
        });
    }

    /** 蓝图缺图时沿已采纳配方的产物身份补图，蓝图仍保留原业务身份和名称。 */
    private void addRecipeIcons(Set<String> keys, Map<String, RecordPresentation> presentations) {
        Map<String, String> recipeKeys = new LinkedHashMap<>();
        for (String key : keys) {
            var existing = presentations.get(key);
            if (existing != null && existing.imageUrl() != null)
                continue;
            if (key.startsWith("/"))
                recipeKeys.put(key, WarframeOfficialReferenceKeys.candidates(key).getLast());
        }
        if (recipeKeys.isEmpty())
            return;
        var recipes =
            officialLookup.records(WarframeOfficialDataDomain.RECIPE, new LinkedHashSet<>(recipeKeys.values()));
        Map<String, String> products = new LinkedHashMap<>();
        recipeKeys.forEach((key, recipeKey) -> {
            var row = recipes.get(normalize(recipeKey));
            if (row == null || !(row.data() instanceof OfficialRecipeDTO recipe))
                return;
            var existing = presentations.get(key);
            String icon = adoptedIcon(row);
            if (icon == null && StrUtil.startWith(recipe.getResultItemIconUrl(), "https://"))
                icon = recipe.getResultItemIconUrl();
            String name = first(existing == null ? null : existing.name(), row.localizedName(), row.officialName());
            presentations.put(key,
                new RecordPresentation(name, icon, existing == null ? null : existing.description()));
            String product = first(recipe.getResultItemUniqueName(), recipe.getResultType());
            if (icon == null && StrUtil.startWith(product, "/"))
                products.put(key, WarframeOfficialReferenceKeys.candidates(product).getLast());
        });
        if (products.isEmpty())
            return;
        var records = officialLookup.records(WarframeOfficialDataDomain.ITEM_LOCALIZATION,
            new LinkedHashSet<>(products.values()));
        products.forEach((key, product) -> {
            var row = records.get(normalize(product));
            String icon = row == null ? null : adoptedIcon(row);
            if (icon != null) {
                var original = presentations.get(key);
                presentations.put(key, new RecordPresentation(original.name(), icon, original.description()));
            }
        });
    }

    private String approvedImage(Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings, String role) {
        return WarframeCommunityMappingScope.WORLD_STATE_IMAGE.target(mappings, role)
            .map(reference -> imageResolver.resolve(reference, null)).orElse(null);
    }

    /**
     * 从已采纳官方图片表按路径解析各平原景观，不固定写死内容哈希。
     *
     * @param data 平原动态状态展示副本。
     * @param mappings 查库获得的周期图片身份。
     * @return 附带能够核实的图片地址的展示副本。
     */
    private Map<String, Object> addCycleImages(Map<String, Object> data, Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings) {
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
            if (item.get("cycleKey") instanceof String key) {
                String image = approvedImage(mappings, key);
                if (StrUtil.startWith(image, "https://"))
                    item.put("imageUrl", image);
            }
            items.add(Collections.unmodifiableMap(item));
        }
        result.put("items", List.copyOf(items));
        return Collections.unmodifiableMap(result);
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
            if (map.containsKey("imageUrl") && text(map.get("imageUrl")) == null && text(map.get("itemKey")) == null
                && text(map.get("displayName")) != null)
                keys.add(text(map.get("displayName")));
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
     * 只收集电波区域的原始挑战路径，不把沉沦之地的同名挑战误识为夜波。
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
     * @param path 当前记录在投影中的路径。
     * @param handledReferenceFields 本轮已解析或已由所属资料域诊断的字段，用于淘汰通用解析的旧诊断。
     * @return 不可变的展示字段副本。
     */
    private static Map<String, Object> decorate(Map<String, Object> original, Map<String, Object> display,
        Map<String, RecordPresentation> presentations, WarframeWorldStateActivityProjection activities,
        Map<String, WarframeOfficialRecordReadDTO> nodes,
        Map<WarframeOfficialDataDomain, Map<String, WarframeOfficialRecordReadDTO>> references, String section,
        String inheritedType, String path, Set<String> handledReferenceFields) {
        Map<String, Object> copy = new LinkedHashMap<>();
        String type = WarframeWorldStateActivityProjection.conquestTypeKey(original);
        String researchType = type == null ? inheritedType : type;
        for (var field : display.entrySet()) {
            String fieldPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
            copy.put(field.getKey(), decorateValue(original.get(field.getKey()), field.getValue(), presentations,
                activities, nodes, references, section, researchType, fieldPath, handledReferenceFields));
        }
        for (String key : ITEM_FIELDS) {
            Object identity =
                "itemKey".equals(key) && copy.get(key) instanceof String pathKey && pathKey.startsWith("/")
                    ? copy.get(key) : original.get(key);
            if (identity instanceof String reference) {
                RecordPresentation record = presentations.get(reference);
                if (record == null) {
                    continue;
                }
                if (!"itemKey".equals(key) && record.name() != null && Objects.equals(copy.get(key), reference)
                    && !Objects.equals(record.name(), reference))
                    copy.put(key, record.name());
                if ("itemKey".equals(key) && record.name() != null
                    && Objects.equals(copy.get("displayName"), reference))
                    copy.put("displayName", record.name());
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
        if (text(copy.get("imageUrl")) == null && text(original.get("itemKey")) == null
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
            if (name != null && !hasChinese(text(copy.get(field))))
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
                        "tauforged", BooleanUtil.isTrue(variant.getTauforged())))
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
                && hasChinese(language.getZhText()))
                copy.put("title", language.getZhText());
        }
        if (NIGHTWAVE_TITLES.contains(section) && original.get("challenge") instanceof String challenge) {
            var row = references.getOrDefault(WarframeOfficialDataDomain.NIGHTWAVE, Map.of())
                .get(normalize(NIGHTWAVE_PREFIX + challenge));
            if (row != null && row.data() instanceof OfficialNightwaveDTO nightwave) {
                String name =
                    first(nightwave.getNameZh(), row.localizedName(), nightwave.getNameEn(), row.officialName());
                if (name != null && !hasChinese(text(copy.get("title"))))
                    copy.put("title", name);
                String description =
                    first(nightwave.getDescriptionZh(), row.description(), nightwave.getDescriptionEn());
                if (description != null && !hasChinese(text(copy.get("description")))) {
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
        activities.decorate(original, copy, section, type, researchType, path, handledReferenceFields);
        if (original.get("node") instanceof String node) {
            WarframeOfficialRecordReadDTO record = nodes.get(normalize(node));
            if (record != null && record.data() instanceof OfficialRegionDTO region) {
                putIfAbsent(copy, "systemName", first(region.getZhSystemName(), region.getSystemName()));
                if (SYNDICATE_MISSIONS_TITLE.equals(section)) {
                    String name = first(region.getZhName(), region.getNameEn(), region.getName());
                    if (name != null) {
                        copy.put("node", name);
                        handledReferenceFields.add(path + ".node");
                    }
                    putIfAbsent(copy, "baseMissionType",
                        first(region.getMissionTypeNameZh(), region.getMissionTypeName(), region.getMissionType()));
                }
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
     * @param path 当前值在投影中的路径。
     * @param handledReferenceFields 本轮已解析或已由所属资料域诊断的字段。
     * @return 当前字段的展示副本。
     */
    private static Object decorateValue(Object original, Object display, Map<String, RecordPresentation> presentations,
        WarframeWorldStateActivityProjection activities, Map<String, WarframeOfficialRecordReadDTO> nodes,
        Map<WarframeOfficialDataDomain, Map<String, WarframeOfficialRecordReadDTO>> references, String section,
        String researchType, String path, Set<String> handledReferenceFields) {
        // 仅展示投影新增的字段以自身作为身份；否则无法补齐后加的赏金阶段奖励。
        if (original == null && (display instanceof Map<?, ?> || display instanceof List<?>))
            original = display;
        if (original instanceof Map<?, ?> originalMap && display instanceof Map<?, ?> displayMap) {
            return decorate(toStringKeys(originalMap), toStringKeys(displayMap), presentations, activities, nodes,
                references, section, researchType, path, handledReferenceFields);
        }
        if (original instanceof List<?> originalList && display instanceof List<?> displayList) {
            List<Object> result = new ArrayList<>(displayList.size());
            for (int index = 0; index < displayList.size(); index++) {
                result.add(decorateValue(index < originalList.size() ? originalList.get(index) : null,
                    displayList.get(index), presentations, activities, nodes, references, section, researchType,
                    path + "[" + index + "]", handledReferenceFields));
            }
            return Collections.unmodifiableList(result);
        }
        return display;
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

    private record RecordPresentation(String name, String imageUrl, String description) {
    }

}
