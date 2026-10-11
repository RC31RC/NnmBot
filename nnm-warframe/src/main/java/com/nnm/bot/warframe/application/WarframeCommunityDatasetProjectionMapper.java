package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nnm.bot.warframe.model.entity.WarframeCommunityAliasDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityBountyDefinition;
import com.nnm.bot.warframe.model.entity.WarframeBountyStageOption;
import com.nnm.bot.warframe.pojo.WarframeBountyDefinitionDetails;
import com.nnm.bot.warframe.model.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketElementDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketComponentDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketRivenTionAliasDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketRivenTionDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityNodesDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRewardPoolDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenAnalyseTrendDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenTrendDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityStateTranslationDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTermDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityArbitrationEssenceDataset;
import com.nnm.bot.warframe.pojo.WarframeCommunityAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketElementDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketComponentDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityNodeDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRewardPoolDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenAnalyseTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTermDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRewardItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityArbitrationEssenceDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityFishGuideDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMapFeatureDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRelicDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRelicDropDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRelicRewardDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenUnrollableDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityWorldStateRuleDetails;
import com.nnm.bot.warframe.pojo.WarframeWorldStateCodebookDetails;
import com.nnm.bot.warframe.service.WarframeItemResolverService;
import com.nnm.bot.warframe.service.WarframeItemNameService;
import com.nnm.bot.warframe.model.entity.WarframeCommunityFishGuide;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMapFeature;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelic;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicDrop;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenUnrollable;
import com.nnm.bot.warframe.model.entity.WarframeCommunityWorldStateRule;
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
import com.nnm.bot.warframe.constant.WarframeWorldStateSortieRecordType;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * 在完整 JSON 与各数据集的结构化查询字段之间建立唯一映射。
 *
 * <p>所有写入路径都必须经过这里，避免人工编辑或冲突合并后 JSON 与数据库投影不一致。</p>
 */
@Component
public class WarframeCommunityDatasetProjectionMapper {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeItemNameService itemNameService;

    public void sync(WarframeCommunityDatasetRecord record, JsonNode json) {
        JsonNode data = payload(json);
        if (record instanceof WarframeCommunityBountyDefinition value) {
            value.setNameKey(text(data, "name"));
            value.setDescriptionKey(text(data, "description"));
            value.setIcon(text(data, "icon"));
            value.getStages().clear();
            JsonNode stages = data.path("stages");
            for (int stage = 0; stage < stages.size(); stage++) {
                for (int option = 0; option < stages.get(stage).size(); option++) {
                    WarframeBountyStageOption entry = new WarframeBountyStageOption();
                    entry.setStageNumber(stage + 1);
                    entry.setOptionNumber(option + 1);
                    entry.setJobReference(stages.get(stage).get(option).asText());
                    value.getStages().add(entry);
                }
            }
        } else if (record instanceof WarframeCommunityTranslationDataset value) {
            value.setSourceId(longValue(data, "id"));
            value.setLocale(text(data, "locale"));
            value.setDisplayName(text(data, "displayName"));
            value.setPrimeItem(booleanValue(data, "is_prime"));
            value.setSetItem(booleanValue(data, "is_set"));
        } else if (record instanceof WarframeCommunityAliasDataset value) {
            value.setSourceId(longValue(data, "id"));
            value.setAliasName(text(data, "cn"));
            value.setTargetName(text(data, "en"));
            value.setNormalizedAlias(WarframeItemResolverService.normalize(value.getAliasName()));
        } else if (record instanceof WarframeCommunityNodesDataset value) {
            value.setUniqueName(text(data, "uniqueName"));
            value.setDisplayName(text(data, "name"));
            value.setSystemIndex(integerValue(data, "systemIndex"));
            value.setSystemName(text(data, "systemName"));
            value.setNodeType(integerValue(data, "nodeType"));
            value.setMasteryReq(integerValue(data, "masteryReq"));
            value.setMissionIndex(integerValue(data, "missionIndex"));
            value.setFactionIndex(integerValue(data, "factionIndex"));
            value.setMinEnemyLevel(integerValue(data, "minEnemyLevel"));
            value.setMaxEnemyLevel(integerValue(data, "maxEnemyLevel"));
        } else if (record instanceof WarframeCommunityRewardPoolDataset value) {
            JsonNode rewards = data.get("rewards");
            value.setPoolKey(text(data, "poolKey"));
            value.setRewardItemsJson(rewards.toString());
        } else if (record instanceof WarframeCommunityStateTranslationDataset value) {
            value.setUniqueName(text(data, "uniqueName"));
            value.setDisplayName(text(data, "name"));
            value.setDescription(text(data, "description"));
        } else if (record instanceof WarframeCommunityMarketComponentDataset value) {
            value.setInputAlias(text(data, "input_alias"));
            value.setMarketSuffix(text(data, "market_suffix"));
        } else if (record instanceof WarframeCommunityMarketElementDataset value) {
            value.setSourceId(longValue(data, "id"));
            value.setElementCode(text(data, "en"));
            value.setDisplayName(text(data, "cn"));
        } else if (record instanceof WarframeCommunityMarketRivenTionDataset value) {
            value.setSourceId(longValue(data, "ids"));
            value.setEffect(text(data, "effect"));
            value.setAttributeGroup(text(data, "group"));
            value.setNegativeOnly(booleanValue(data, "negative_only"));
            value.setPositiveIsNegative(booleanValue(data, "positive_is_negative"));
            value.setPrefix(text(data, "prefix"));
            value.setSearchOnly(booleanValue(data, "search_only"));
            value.setSuffix(text(data, "suffix"));
            value.setUnits(text(data, "units"));
            value.setUrlName(text(data, "url_name"));
            value.setExclusiveToJson(jsonText(data, "exclusive_to"));
        } else if (record instanceof WarframeCommunityMarketRivenTionAliasDataset value) {
            value.setSourceId(longValue(data, "id"));
            value.setAttributeCode(text(data, "en"));
            value.setAliasName(text(data, "cn"));
        } else if (record instanceof WarframeCommunityRivenAnalyseTrendDataset value) {
            value.setSourceId(longValue(data, "id"));
            value.setDisplayName(text(data, "name"));
            value.setPrefix(text(data, "prefix"));
            value.setSuffix(text(data, "suffix"));
            value.setArchwing(doubleValue(data, "archwing"));
            value.setMeleeValue(doubleValue(data, "melle"));
            value.setPistol(doubleValue(data, "pistol"));
            value.setRifle(doubleValue(data, "rifle"));
            value.setShotgun(doubleValue(data, "shotgun"));
        } else if (record instanceof WarframeCommunityRivenTrendDataset value) {
            value.setSourceId(longValue(data, "id"));
            value.setTrendTime(text(data, "isDate"));
            value.setNewDot(text(data, "new_dot"));
            value.setNewNum(doubleValue(data, "new_num"));
            value.setOldDot(text(data, "old_dot"));
            value.setOldNum(doubleValue(data, "old_num"));
            value.setTrendName(text(data, "trend_name"));
            value.setWeaponType(text(data, "type"));
        } else if (record instanceof WarframeCommunityTermDataset value) {
            value.setTermGroup(text(data, "group", "termGroup", "type"));
            value.setEnglishText(text(data, "en", "english", "englishText", "key"));
            value.setChineseText(text(data, "zh", "chinese", "chineseText", "value"));
        } else if (record instanceof WarframeCommunityArbitrationEssenceDataset value) {
            value.setNodeName(text(data, "node", "nodeName", "name"));
            value.setEssenceAmount(integerValue(data, "essence", "essenceAmount", "value"));
        } else if (record instanceof WarframeCommunityFishGuide value) {
            value.setName(text(data, "name"));
        } else if (record instanceof WarframeCommunityMapFeature value) {
            value.setWorld(text(json, "world"));
            value.setName(text(data.path("properties"), "name"));
            value.setFeatureType(text(data.path("properties"), "type"));
            value.setGeometryType(text(data.path("geometry"), "type"));
        } else if (record instanceof WarframeCommunityRelic value) {
            String uniqueName = text(data, "uniqueName", "recordKey");
            value.setRelicName(text(data, "name", "uniqueName", "recordKey"));
            value.setUniqueName(uniqueName);
            value.setCategory(text(data, "category"));
            value.setEra(text(data, "era"));
            value.setIcon(text(data, "icon", "imageName"));
            value.setCodexSecret(booleanValue(data, "codexSecret"));
            value.setDescriptionKey(text(data, "description", "descriptionKey"));
            value.setQuality(text(data, "quality"));
            value.setRewardManifest(text(data, "rewardManifest"));
            value.setIntroducedAt(longValue(data, "introducedAt"));
            value.setMarketId(text(data.path("warframeMarket"), "id"));
            value.setUrlName(text(data.path("warframeMarket"), "urlName"));
            value.setVaulted(booleanValue(data.path("vaultInfo"), "vaulted"));
        } else if (record instanceof WarframeCommunityRelicReward value) {
            value.setRelicRecordKey(text(json, "relicRecordKey"));
            value.setRewardOrder(integerValue(json, "rewardOrder"));
            value.setRewardName(text(data.path("item"), "name"));
            value.setUniqueName(text(data.path("item"), "uniqueName"));
            value.setMarketId(text(data.path("item").path("warframeMarket"), "id"));
            value.setUrlName(text(data.path("item").path("warframeMarket"), "urlName"));
            value.setRarity(text(data, "rarity"));
            value.setChance(decimalValue(data, "chance"));
        } else if (record instanceof WarframeCommunityRelicDrop value) {
            value.setRelicName(text(json, "relicName"));
            value.setRelicRecordKey(text(json, "relicRecordKey"));
            value.setLocationText(text(data, "location", "node"));
            value.setRarity(text(data, "rarity"));
            value.setChance(decimalValue(data, "chance"));
            value.setDropOrder(integerValue(json, "rewardOrder", "dropOrder"));
        } else if (record instanceof WarframeCommunityRivenUnrollable value) {
            value.setAttributeCode(text(json, "weaponCode", "attributeCode"));
        } else if (record instanceof WarframeCommunityWorldStateRule value) {
            value.setDatasetCode(text(json, "datasetCode"));
            value.setLocale(text(json, "locale"));
        } else if (record instanceof WarframeWorldStateLanguage value) {
            value.setLocale(text(json, "locale"));
            value.setDisplayText(text(data, "value", "name"));
            value.setDisplayTextZh(text(data.path("zh"), "value", "name"));
            value.setDisplayTextEn(text(data.path("en"), "value", "name"));
        } else if (record instanceof WarframeWorldStateRecord value) {
            value.setLocale(text(json, "locale"));
            if (record instanceof WarframeWorldStateNode node) {
                node.setDisplayName(text(data, "value", "name"));
                node.setFactionReference(text(data, "faction"));
                node.setMissionTypeReference(text(data, "type"));
            } else if (record instanceof WarframeWorldStateFissureModifier modifier) {
                modifier.setDisplayName(text(data, "value", "name"));
                modifier.setTierNumber(integerValue(data, "num"));
            } else if (record instanceof WarframeWorldStateSortie sortie) {
                sortie.setRecordType(parseSortieRecordType(text(json, "recordType")));
                sortie.setDisplayName(StrUtil.nullToEmpty(text(data, "value", "name")));
                sortie.setDescription(StrUtil.nullToEmpty(text(data, "desc", "description")));
                sortie.setFactionReference(StrUtil.nullToEmpty(text(data, "faction")));
            } else if (record instanceof WarframeWorldStateMissionType value1) {
                value1.setDisplayName(text(data, "value", "name"));
            } else if (record instanceof WarframeWorldStateFaction value1) {
                value1.setDisplayName(text(data, "value", "name"));
            } else if (record instanceof WarframeWorldStateOperationType value1) {
                value1.setDisplayName(text(data, "value", "name"));
            } else if (record instanceof WarframeWorldStatePersistentEnemy value1) {
                value1.setDisplayName(text(data, "value", "name"));
            } else if (record instanceof WarframeWorldStateSyndicate value1) {
                value1.setDisplayName(text(data, "value", "name"));
            } else if (record instanceof WarframeWorldStateUpgradeType value1) {
                value1.setDisplayName(text(data, "value", "name"));
            }
        }
    }

    public WarframeCommunityDatasetDetails details(WarframeCommunityDatasetRecord record) {
        return switch (record) {
            case WarframeCommunityTranslationDataset value ->
                    new WarframeCommunityTranslationDetails(
                            value.getSourceId(), value.getLocale(), value.getDisplayName(),
                            value.getPrimeItem(), value.getSetItem());
            case WarframeCommunityAliasDataset value ->
                    new WarframeCommunityAliasDetails(
                            value.getSourceId(), value.getAliasName(), value.getTargetName());
            case WarframeCommunityNodesDataset value ->
                    new WarframeCommunityNodeDetails(
                            value.getUniqueName(), value.getDisplayName(), value.getSystemIndex(),
                            value.getSystemName(), value.getNodeType(), value.getMasteryReq(),
                            value.getMissionIndex(), value.getFactionIndex(),
                            value.getMinEnemyLevel(), value.getMaxEnemyLevel());
            case WarframeCommunityRewardPoolDataset value ->
                    new WarframeCommunityRewardPoolDetails(
                            value.getPoolKey(), rewardItems(value.getRewardItemsJson()));
            case WarframeCommunityStateTranslationDataset value ->
                    new WarframeCommunityStateTranslationDetails(
                            value.getUniqueName(), value.getDisplayName(), value.getDescription());
            case WarframeCommunityMarketComponentDataset value ->
                    new WarframeCommunityMarketComponentDetails(
                            value.getInputAlias(), value.getMarketSuffix());
            case WarframeCommunityMarketElementDataset value ->
                    new WarframeCommunityMarketElementDetails(
                            value.getSourceId(), value.getElementCode(), value.getDisplayName());
            case WarframeCommunityMarketRivenTionDataset value ->
                    new WarframeCommunityMarketRivenTionDetails(
                            value.getSourceId(), value.getEffect(), value.getAttributeGroup(),
                            value.getNegativeOnly(), value.getPositiveIsNegative(),
                            value.getPrefix(), value.getSearchOnly(), value.getSuffix(),
                            value.getUnits(), value.getUrlName(), value.getExclusiveToJson());
            case WarframeCommunityMarketRivenTionAliasDataset value ->
                    new WarframeCommunityMarketRivenTionAliasDetails(
                            value.getSourceId(), value.getAttributeCode(), value.getAliasName());
            case WarframeCommunityRivenAnalyseTrendDataset value ->
                    new WarframeCommunityRivenAnalyseTrendDetails(
                            value.getSourceId(), value.getDisplayName(), value.getPrefix(),
                            value.getSuffix(), value.getArchwing(), value.getMeleeValue(),
                            value.getPistol(), value.getRifle(), value.getShotgun());
            case WarframeCommunityRivenTrendDataset value ->
                    new WarframeCommunityRivenTrendDetails(
                            value.getSourceId(), value.getTrendTime(), value.getNewDot(),
                            value.getNewNum(), value.getOldDot(), value.getOldNum(),
                            value.getTrendName(), value.getWeaponType());
            case WarframeCommunityTermDataset value ->
                    new WarframeCommunityTermDetails(
                            value.getTermGroup(), value.getEnglishText(), value.getChineseText());
            case WarframeCommunityBountyDefinition value ->
                    new WarframeBountyDefinitionDetails(value.getRecordKey(), value.getNameKey(),
                            value.getDescriptionKey(), value.getIcon(), value.getStages().stream()
                            .map(stage -> new WarframeBountyDefinitionDetails.StageOption(
                                    stage.getStageNumber(), stage.getOptionNumber(), stage.getJobReference()))
                            .toList());
            case WarframeCommunityArbitrationEssenceDataset value ->
                    new WarframeCommunityArbitrationEssenceDetails(
                            value.getNodeName(), value.getEssenceAmount());
            case WarframeCommunityFishGuide value ->
                    new WarframeCommunityFishGuideDetails(value.getName());
            case WarframeCommunityMapFeature value ->
                    new WarframeCommunityMapFeatureDetails(value.getWorld(), value.getFeatureType(),
                            value.getName(), value.getGeometryType());
            case WarframeCommunityRelic value ->
                    new WarframeCommunityRelicDetails(value.getRelicName(), value.getUniqueName(),
                            value.getCategory(), value.getEra(), value.getIcon(),
                            value.getCodexSecret(), value.getDescriptionKey(), value.getQuality(),
                            value.getRewardManifest(), value.getIntroducedAt(), value.getMarketId(),
                            value.getUrlName(), value.getVaulted());
            case WarframeCommunityRelicReward value ->
                    new WarframeCommunityRelicRewardDetails(value.getRelicRecordKey(),
                            value.getRewardName(), value.getUniqueName(), value.getMarketId(),
                            value.getUrlName(), value.getRarity(), value.getChance(),
                            value.getRewardOrder());
            case WarframeCommunityRelicDrop value ->
                    new WarframeCommunityRelicDropDetails(value.getRelicName(),
                            value.getRelicRecordKey(), value.getLocationText(), value.getRarity(),
                            value.getChance(), value.getDropOrder());
            case WarframeCommunityRivenUnrollable value ->
                    new WarframeCommunityRivenUnrollableDetails(value.getAttributeCode());
            case WarframeCommunityWorldStateRule value ->
                    new WarframeCommunityWorldStateRuleDetails(value.getDatasetCode(), value.getLocale());
            case WarframeWorldStateLanguage value ->
                    new WarframeWorldStateCodebookDetails(value.getLocale(), null, null,
                            value.getDisplayText(), value.getDisplayTextZh(), value.getDisplayTextEn(),
                            null, null, null, null);
            case WarframeWorldStateRecord value ->
                    worldStateDetails(value);
            default -> throw new IllegalArgumentException(
                    "不支持的社区数据实体：" + record.getClass().getName());
        };
    }

    private List<WarframeCommunityRewardItem> rewardItems(String json) {
        try {
            JsonNode rewards = objectMapper.readTree(json);
            // 快照还保留上游字段，详情仅投影标准字段。
            var names = itemNameService.resolveAll(StreamSupport.stream(rewards.spliterator(), false)
                    .map(reward -> reward.path("itemKey").asText()).toList());
            return StreamSupport.stream(rewards.spliterator(), false)
                    .map(reward -> new WarframeCommunityRewardItem(
                            reward.path("stage").intValue(), reward.path("itemKey").asText(),
                            names.get(reward.path("itemKey").asText()).displayName(),
                            reward.path("count").intValue(),
                            reward.hasNonNull("probability") ? reward.get("probability").decimalValue() : null,
                            reward.hasNonNull("rarity") ? reward.get("rarity").asText() : null))
                    .toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("奖励池标准化投影损坏", exception);
        }
    }

    private static WarframeWorldStateCodebookDetails worldStateDetails(
            WarframeWorldStateRecord value) {
        String displayName = null;
        String faction = null;
        String missionType = null;
        Integer tier = null;
        String description = null;
        String recordType = null;
        if (value instanceof WarframeWorldStateNode node) {
            displayName = node.getDisplayName();
            faction = node.getFactionReference();
            missionType = node.getMissionTypeReference();
        } else if (value instanceof WarframeWorldStateFissureModifier modifier) {
            displayName = modifier.getDisplayName();
            tier = modifier.getTierNumber();
        } else if (value instanceof WarframeWorldStateSortie sortie) {
            recordType = sortie.getRecordType() == null ? null : sortie.getRecordType().name();
            displayName = sortie.getDisplayName();
            description = sortie.getDescription();
            faction = sortie.getFactionReference();
        } else if (value instanceof WarframeWorldStateMissionType item) {
            displayName = item.getDisplayName();
        } else if (value instanceof WarframeWorldStateFaction item) {
            displayName = item.getDisplayName();
        } else if (value instanceof WarframeWorldStateOperationType item) {
            displayName = item.getDisplayName();
        } else if (value instanceof WarframeWorldStatePersistentEnemy item) {
            displayName = item.getDisplayName();
        } else if (value instanceof WarframeWorldStateSyndicate item) {
            displayName = item.getDisplayName();
        } else if (value instanceof WarframeWorldStateUpgradeType item) {
            displayName = item.getDisplayName();
        }
        return new WarframeWorldStateCodebookDetails(value.getLocale(), recordType, displayName, null,
                null, null, faction, missionType, tier, description);
    }

    private static String text(JsonNode json, String... fields) {
        for (String field : fields) {
            JsonNode value = json.get(field);
            if (value != null && !value.isNull() && StrUtil.isNotBlank(value.asText())) {
                return StrUtil.trim(value.asText());
            }
        }
        return null;
    }

    private static WarframeWorldStateSortieRecordType parseSortieRecordType(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            return WarframeWorldStateSortieRecordType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("未知突击代码簿记录类型：" + value, exception);
        }
    }

    private static String jsonText(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.isTextual() ? StrUtil.trim(value.asText()) : value.toString();
    }

    private static Long longValue(JsonNode json, String field) {
        JsonNode value = json.get(field);
        return value == null || value.isNull() || !value.canConvertToLong()
                ? null : value.longValue();
    }

    private static Integer integerValue(JsonNode json, String... fields) {
        for (String field : fields) {
            JsonNode value = json.get(field);
            if (value != null && !value.isNull() && value.canConvertToInt()) {
                return value.intValue();
            }
        }
        return null;
    }

    private static Double doubleValue(JsonNode json, String field) {
        JsonNode value = json.get(field);
        return value == null || value.isNull() || !value.isNumber()
                ? null : value.doubleValue();
    }

    private static java.math.BigDecimal decimalValue(JsonNode json, String field) {
        JsonNode value = json.get(field);
        return value == null || value.isNull() || !value.isNumber()
                ? null : value.decimalValue();
    }

    private static JsonNode payload(JsonNode json) {
        if (json != null && json.isObject() && json.get("data") != null
                && json.get("data").isObject()) {
            return json.get("data");
        }
        return json;
    }

    private static Boolean booleanValue(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isBoolean()) {
            return value.booleanValue();
        }
        String text = value.asText();
        if ("1".equals(text) || "true".equalsIgnoreCase(text)) {
            return true;
        }
        if ("0".equals(text) || "false".equalsIgnoreCase(text)) {
            return false;
        }
        return null;
    }
}
