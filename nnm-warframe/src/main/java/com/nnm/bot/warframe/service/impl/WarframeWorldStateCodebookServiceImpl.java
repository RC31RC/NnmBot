package com.nnm.bot.warframe.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.application.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.config.properties.WarframeWorldStateDataProperties;
import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.constant.WarframeReferenceType;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateFaction;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateFissureModifier;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateLanguage;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateMissionType;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateNode;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateOperationType;
import com.nnm.bot.warframe.model.entity.WarframeWorldStatePersistentEnemy;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateRecord;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateSortie;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateSyndicate;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateUpgradeType;
import com.nnm.bot.warframe.pojo.WarframeWorldStateCodebookEntry;
import com.nnm.bot.warframe.repository.WarframeWorldStateFactionRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateFissureModifierRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateLanguageRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateMissionTypeRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateNodeRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateOperationTypeRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStatePersistentEnemyRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateSortieRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateSyndicateRepository;
import com.nnm.bot.warframe.repository.WarframeWorldStateUpgradeTypeRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * WorldState 专用代码簿服务。
 *
 * <p>代码簿是动态数据展示所需的稳定参考数据，运行时唯一从
 * {@code wf_community_worldstate_*} 数据表读取。这里不读取 classpath
 * JSON，也不把本地资源作为数据库缺失时的隐式兜底；缺少记录就由上层按
 * 未解析引用处理，避免应用显示与数据库内容不一致。</p>
 */
@Service
public class WarframeWorldStateCodebookServiceImpl
        implements WarframeWorldStateCodebookService {

    private static final String SOURCE = "WARFRAME_WORLDSTATE_DATA";

    private final ObjectMapper objectMapper;
    private final WarframeWorldStateDataProperties properties;

    private volatile Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> entries;
    private volatile Map<String, WarframeWorldStateCodebookEntry> languageEntries;

    @Resource
    private WarframeWorldStateMissionTypeRepository missionTypeRepository;
    @Resource
    private WarframeWorldStateNodeRepository nodeRepository;
    @Resource
    private WarframeWorldStateFactionRepository factionRepository;
    @Resource
    private WarframeWorldStateFissureModifierRepository fissureRepository;
    @Resource
    private WarframeWorldStateSortieRepository sortieRepository;
    @Resource
    private WarframeWorldStateSyndicateRepository syndicateRepository;
    @Resource
    private WarframeWorldStateOperationTypeRepository operationRepository;
    @Resource
    private WarframeWorldStateUpgradeTypeRepository upgradeRepository;
    @Resource
    private WarframeWorldStatePersistentEnemyRepository persistentEnemyRepository;
    @Resource
    private WarframeWorldStateLanguageRepository languageRepository;

    @Autowired
    public WarframeWorldStateCodebookServiceImpl(
            ObjectMapper objectMapper,
            WarframeWorldStateDataProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /**
     * 供不启动 Spring 的单元测试使用。没有注入仓储时，服务返回空索引，
     * 不会读取任何本地资源。
     */
    public WarframeWorldStateCodebookServiceImpl() {
        this(new ObjectMapper(), new WarframeWorldStateDataProperties());
    }

    @Override
    public Optional<WarframeWorldStateCodebookEntry> find(
            WarframeReferenceType type, String reference) {
        if (type == null || StrUtil.isBlank(reference)) {
            return Optional.empty();
        }
        ensureLoaded();
        return Optional.ofNullable(entries.getOrDefault(type, Map.of()).get(
                normalizeKey(reference)));
    }

    @Override
    public Optional<WarframeWorldStateCodebookEntry> findLanguage(String reference) {
        if (StrUtil.isBlank(reference)) {
            return Optional.empty();
        }
        ensureLoaded();
        return Optional.ofNullable(languageEntries.get(
                reference.trim().toLowerCase(Locale.ROOT)));
    }

    @Override
    public synchronized void reload() {
        entries = null;
        languageEntries = null;
        ensureLoaded();
    }

    private synchronized void ensureLoaded() {
        if (entries != null && languageEntries != null) {
            return;
        }
        properties.validate();
        entries = immutableReferenceEntries(loadDatabaseEntries());
        languageEntries = immutableLanguageEntries(loadDatabaseLanguageEntries());
    }

    private Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>>
    loadDatabaseEntries() {
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> result =
                new EnumMap<>(WarframeReferenceType.class);
        String locale = StrUtil.blankToDefault(properties.getLocale(), "zh");
        List<WarframeWorldStateNode> nodes = nodeRepository == null
                ? List.of() : nodeRepository.findAllByEnabledTrue();

        addRecords(result, WarframeReferenceType.MISSION_TYPE,
                missionTypeRepository == null ? List.of() : missionTypeRepository.findAllByEnabledTrue(),
                locale);
        addRecords(result, WarframeReferenceType.NODE,
                nodes,
                locale);
        addNodeMissionAliases(result, nodes, locale);
        addRecords(result, WarframeReferenceType.FACTION,
                factionRepository == null ? List.of() : factionRepository.findAllByEnabledTrue(),
                locale);
        addNodeFactionAliases(result, nodes, locale);
        addRecords(result, WarframeReferenceType.FISSURE_TIER,
                fissureRepository == null ? List.of() : fissureRepository.findAllByEnabledTrue(),
                locale);
        List<WarframeWorldStateSortie> sorties = sortieRepository == null
                ? List.of() : sortieRepository.findAllByEnabledTrue();
        addRecords(result, WarframeReferenceType.MODIFIER,
                sorties.stream().filter(value -> value.getRecordType()
                        == com.nnm.bot.warframe.constant.WarframeWorldStateSortieRecordType.MODIFIER)
                        .toList(), locale);
        addRecords(result, WarframeReferenceType.SORTIE_BOSS,
                sorties.stream().filter(value -> value.getRecordType()
                        == com.nnm.bot.warframe.constant.WarframeWorldStateSortieRecordType.BOSS)
                        .toList(), locale);
        addRecords(result, WarframeReferenceType.SYNDICATE,
                syndicateRepository == null ? List.of() : syndicateRepository.findAllByEnabledTrue(),
                locale);
        addRecords(result, WarframeReferenceType.OPERATION_TYPE,
                operationRepository == null ? List.of() : operationRepository.findAllByEnabledTrue(),
                locale);
        addRecords(result, WarframeReferenceType.UPGRADE_TYPE,
                upgradeRepository == null ? List.of() : upgradeRepository.findAllByEnabledTrue(),
                locale);
        addRecords(result, WarframeReferenceType.PERSISTENT_ENEMY,
                persistentEnemyRepository == null
                        ? List.of() : persistentEnemyRepository.findAllByEnabledTrue(),
                locale);
        return result;
    }

    /**
     * Some live node data uses a composed or newly introduced mission label before the
     * compact mission-type dataset receives a matching record. The node JSON is already
     * the structured source for that label, so use it as a read-only alias without
     * replacing a matching mission-type record.
     */
    private void addNodeMissionAliases(
            Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> result,
            List<WarframeWorldStateNode> nodes,
            String locale) {
        Map<String, WarframeWorldStateCodebookEntry> index =
                result.computeIfAbsent(WarframeReferenceType.MISSION_TYPE,
                        ignored -> new LinkedHashMap<>());
        Map<String, String> displays = new LinkedHashMap<>();
        Set<String> ambiguous = new HashSet<>();
        for (WarframeWorldStateNode node : nodes) {
            try {
                JsonNode data = objectMapper.readTree(
                        StrUtil.blankToDefault(node.getCurrentJson(), "{}"));
                if (data == null) {
                    continue;
                }
                String display = text(data.path(locale), "type");
                if (StrUtil.isBlank(display)) {
                    display = text(data.path("zh"), "type");
                }
                if (StrUtil.isBlank(display)) {
                    display = text(data.path("en"), "type");
                }
                for (String name : List.of(
                        text(data.path("en"), "type"),
                        text(data.path("zh"), "type"))) {
                    String key = normalizeKey(name);
                    if (key.isBlank() || index.containsKey(key) || ambiguous.contains(key)) {
                        continue;
                    }
                    String previous = displays.putIfAbsent(key, display);
                    if (previous != null && !previous.equals(display)) {
                        displays.remove(key);
                        ambiguous.add(key);
                    }
                }
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Invalid WorldState node JSON: " + node.getRecordKey(), exception);
            }
        }
        displays.forEach((key, display) -> index.putIfAbsent(
                key, entry(key, display, "", "", "")));
    }

    /**
     * A few live node records refer to factions that are not yet present in the compact
     * faction dataset. Keep those references resolvable without making node data a
     * writable faction source or overriding a database faction entry.
     */
    private void addNodeFactionAliases(
            Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> result,
            List<WarframeWorldStateNode> nodes,
            String locale) {
        Map<String, WarframeWorldStateCodebookEntry> index =
                result.computeIfAbsent(WarframeReferenceType.FACTION,
                        ignored -> new LinkedHashMap<>());
        Map<String, WarframeWorldStateCodebookEntry> aliases = new LinkedHashMap<>();
        Set<String> ambiguous = new HashSet<>();
        for (WarframeWorldStateNode node : nodes) {
            try {
                JsonNode data = objectMapper.readTree(
                        StrUtil.blankToDefault(node.getCurrentJson(), "{}"));
                if (data == null) {
                    continue;
                }
                String display = text(data.path(locale), "enemy");
                if (StrUtil.isBlank(display)) {
                    display = text(data.path("zh"), "enemy");
                }
                if (StrUtil.isBlank(display)) {
                    display = text(data.path("en"), "enemy");
                }
                String reference = StrUtil.blankToDefault(node.getFactionReference(), "");
                if (StrUtil.isBlank(display) || StrUtil.isBlank(reference)) {
                    continue;
                }
                for (String name : List.of(
                        text(data.path("en"), "enemy"),
                        text(data.path("zh"), "enemy"))) {
                    String key = normalizeKey(name);
                    if (key.isBlank() || index.containsKey(key) || ambiguous.contains(key)) {
                        continue;
                    }
                    WarframeWorldStateCodebookEntry value =
                            entry(reference, display, "", "", "");
                    WarframeWorldStateCodebookEntry previous = aliases.putIfAbsent(key, value);
                    if (previous != null && !previous.displayName().equals(value.displayName())) {
                        aliases.remove(key);
                        ambiguous.add(key);
                    }
                }
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Invalid WorldState node JSON: " + node.getRecordKey(), exception);
            }
        }
        aliases.forEach(index::putIfAbsent);
    }

    private Map<String, WarframeWorldStateCodebookEntry> loadDatabaseLanguageEntries() {
        if (languageRepository == null) {
            return Map.of();
        }

        Map<String, WarframeWorldStateLanguage> preferred = new LinkedHashMap<>();
        String locale = StrUtil.blankToDefault(properties.getLocale(), "zh");
        languageRepository.findAllByEnabledTrue().stream()
                .sorted(Comparator.comparing(
                        record -> locale.equalsIgnoreCase(record.getLocale())))
                .forEach(record -> {
                    String key = normalizeKey(record.getLanguageKey());
                    if (key.isBlank()) {
                        return;
                    }
                    WarframeWorldStateLanguage current = preferred.get(key);
                    if (current == null || locale.equalsIgnoreCase(record.getLocale())) {
                        preferred.put(key, record);
                    }
                });

        Map<String, WarframeWorldStateCodebookEntry> result = new LinkedHashMap<>();
        preferred.values().forEach(record -> {
            String display = languageDisplay(record);
            if (StrUtil.isBlank(display)) {
                return;
            }
            result.put(normalizeKey(record.getLanguageKey()),
                    entry(record.getLanguageKey(), display, languageDescription(record), null, null));
        });
        return result;
    }

    private void addRecords(
            Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> result,
            WarframeReferenceType type,
            List<? extends WarframeWorldStateRecord> records,
            String locale) {
        Map<String, WarframeWorldStateRecord> preferred = new LinkedHashMap<>();
        for (WarframeWorldStateRecord record : records) {
            String key = normalizeKey(record.getRecordKey());
            if (StrUtil.isBlank(key)
                    || (!locale.equalsIgnoreCase(record.getLocale())
                    && !"en".equalsIgnoreCase(record.getLocale()))) {
                continue;
            }
            WarframeWorldStateRecord current = preferred.get(key);
            if (current == null
                    || (locale.equalsIgnoreCase(record.getLocale())
                    && StrUtil.isNotBlank(recordDisplay(record)))) {
                preferred.put(key, record);
            }
        }

        Map<String, WarframeWorldStateCodebookEntry> aliases = new LinkedHashMap<>();
        Set<String> ambiguous = new HashSet<>();
        for (WarframeWorldStateRecord record : preferred.values()) {
            String display = recordDisplay(record);
            String description = record instanceof WarframeWorldStateSortie sortie
                    ? StrUtil.blankToDefault(sortie.getDescription(), "")
                    : "";
            String related = relatedReference(record);
            Map<String, WarframeWorldStateCodebookEntry> index =
                    result.computeIfAbsent(type, ignored -> new LinkedHashMap<>());
            WarframeWorldStateCodebookEntry value =
                    entry(record.getRecordKey(), display, description, related,
                            factionReference(record));
            index.put(normalizeKey(record.getRecordKey()), value);
            if (type == WarframeReferenceType.MISSION_TYPE
                    || type == WarframeReferenceType.FACTION) {
                addNameAliases(aliases, ambiguous, record, value);
                if (type == WarframeReferenceType.FACTION) {
                    addFactionDisplayAlias(aliases, ambiguous, record, value);
                }
            }
            if (record instanceof WarframeWorldStateFissureModifier fissure
                    && fissure.getTierNumber() > 0) {
                int tier = fissure.getTierNumber();
                index.putIfAbsent(normalizeKey("T" + tier), value);
                index.putIfAbsent(Integer.toString(tier), value);
                index.putIfAbsent(normalizeKey("VoidT" + tier), value);
            }
        }
        if (type == WarframeReferenceType.MISSION_TYPE
                || type == WarframeReferenceType.FACTION) {
            Map<String, WarframeWorldStateCodebookEntry> index = result.get(type);
            if (index != null) {
                aliases.forEach(index::putIfAbsent);
            }
        }
    }

    /**
     * The compact faction dataset stores the readable name in a column, while
     * its JSON payload may only contain the upstream value. Keep both forms
     * resolvable so text schedules can use either representation.
     */
    private static void addFactionDisplayAlias(
            Map<String, WarframeWorldStateCodebookEntry> aliases,
            Set<String> ambiguous,
            WarframeWorldStateRecord record,
            WarframeWorldStateCodebookEntry entry) {
        if (!(record instanceof WarframeWorldStateFaction faction)) {
            return;
        }
        String key = normalizeKey(faction.getDisplayName());
        if (key.isBlank() || ambiguous.contains(key)) {
            return;
        }
        WarframeWorldStateCodebookEntry previous = aliases.putIfAbsent(key, entry);
        if (previous != null && !previous.displayName().equals(entry.displayName())) {
            aliases.remove(key);
            ambiguous.add(key);
        }
    }

    private void addNameAliases(
            Map<String, WarframeWorldStateCodebookEntry> aliases,
            Set<String> ambiguous,
            WarframeWorldStateRecord record,
            WarframeWorldStateCodebookEntry entry) {
        try {
            JsonNode current = objectMapper.readTree(
                    StrUtil.blankToDefault(record.getCurrentJson(), "{}"));
            JsonNode upstream = objectMapper.readTree(
                    StrUtil.blankToDefault(record.getUpstreamJson(), "{}"));
            for (String name : List.of(
                    text(current == null ? null : current.path("en"), "value"),
                    text(current == null ? null : current.path("zh"), "value"),
                    text(upstream, "value"))) {
                String key = normalizeKey(name);
                if (key.isBlank() || ambiguous.contains(key)) {
                    continue;
                }
                WarframeWorldStateCodebookEntry previous = aliases.putIfAbsent(key, entry);
                if (previous != null && !previous.displayName().equals(entry.displayName())) {
                    aliases.remove(key);
                    ambiguous.add(key);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Invalid WorldState codebook JSON: " + record.getRecordKey(), exception);
        }
    }

    private static String relatedReference(WarframeWorldStateRecord record) {
        if (record instanceof WarframeWorldStateNode node) {
            return StrUtil.blankToDefault(node.getMissionTypeReference(), "");
        }
        if (record instanceof WarframeWorldStateFissureModifier fissure) {
            return Integer.toString(fissure.getTierNumber());
        }
        if (record instanceof WarframeWorldStateSortie sortie) {
            return StrUtil.blankToDefault(sortie.getFactionReference(), "");
        }
        return "";
    }

    private static String factionReference(WarframeWorldStateRecord record) {
        if (record instanceof WarframeWorldStateNode node) {
            return StrUtil.blankToDefault(node.getFactionReference(), "");
        }
        if (record instanceof WarframeWorldStateSortie sortie) {
            return StrUtil.blankToDefault(sortie.getFactionReference(), "");
        }
        return "";
    }

    private String languageDisplay(WarframeWorldStateLanguage record) {
        String display = properties.getLocale().equalsIgnoreCase("zh")
                ? record.getDisplayTextZh()
                : record.getDisplayTextEn();
        return StrUtil.blankToDefault(
                display,
                StrUtil.blankToDefault(record.getDisplayTextEn(),
                        StrUtil.blankToDefault(record.getDisplayTextZh(),
                                record.getDisplayText())));
    }

    private String languageDescription(WarframeWorldStateLanguage record) {
        try {
            JsonNode data = objectMapper.readTree(
                    StrUtil.blankToDefault(record.getCurrentJson(), "{}"));
            if (data == null) {
                return "";
            }
            String locale = properties.getLocale().toLowerCase(Locale.ROOT);
            String description = text(data.path(locale), "desc");
            if (StrUtil.isBlank(description)) {
                description = text(data.path("zh"), "desc");
            }
            return StrUtil.blankToDefault(description, text(data.path("en"), "desc"));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Invalid WorldState language JSON: " + record.getLanguageKey(), exception);
        }
    }

    private static String recordDisplay(WarframeWorldStateRecord record) {
        if (record instanceof WarframeWorldStateMissionType value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateNode value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateFaction value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateFissureModifier value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateSortie value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateSyndicate value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateOperationType value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStateUpgradeType value) {
            return value.getDisplayName();
        }
        if (record instanceof WarframeWorldStatePersistentEnemy value) {
            return value.getDisplayName();
        }
        return "";
    }

    private static String text(JsonNode node, String field) {
        return node != null && node.isObject() ? node.path(field).asText("") : "";
    }

    private static String normalizeKey(String value) {
        return StrUtil.blankToDefault(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private static WarframeWorldStateCodebookEntry entry(
            String reference,
            String displayName,
            String description,
            String relatedReference,
            String factionReference) {
        return new WarframeWorldStateCodebookEntry(
                reference,
                displayName,
                description,
                relatedReference,
                SOURCE,
                StrUtil.isBlank(displayName)
                        ? WarframeReferenceResolutionStatus.UNRESOLVED
                        : WarframeReferenceResolutionStatus.RESOLVED,
                factionReference);
    }

    private static Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>>
    immutableReferenceEntries(
            Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> source) {
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntry>> result =
                new EnumMap<>(WarframeReferenceType.class);
        source.forEach((type, values) -> result.put(type,
                Collections.unmodifiableMap(new LinkedHashMap<>(values))));
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, WarframeWorldStateCodebookEntry> immutableLanguageEntries(
            Map<String, WarframeWorldStateCodebookEntry> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
