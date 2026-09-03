package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityAliasDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityMarketElementDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityMarketRivenTionAliasDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityMarketRivenTionDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityNodesDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityRewardPoolDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityRivenAnalyseTrendDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityRivenTrendDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityStateTranslationDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.dto.WarframeCommunityAliasDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityMarketElementDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityMarketRivenTionAliasDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityMarketRivenTionDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityNodeDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityRewardPoolDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityRivenAnalyseTrendDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityRivenTrendDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.dto.WarframeCommunityTranslationDetails;
import org.springframework.stereotype.Component;

/**
 * 在完整 JSON 与各数据集的结构化查询字段之间建立唯一映射。
 *
 * <p>所有写入路径都必须经过这里，避免人工编辑或冲突合并后 JSON 与数据库投影不一致。</p>
 */
@Component
public class WarframeCommunityDatasetProjectionMapper {

    public void sync(WarframeCommunityDatasetRecord record, JsonNode json) {
        if (record instanceof WarframeCommunityTranslationDataset value) {
            value.setSourceId(longValue(json, "id"));
            value.setChineseName(text(json, "cn"));
            value.setEnglishName(text(json, "en"));
            value.setPrimeItem(booleanValue(json, "is_prime"));
            value.setSetItem(booleanValue(json, "is_set"));
        } else if (record instanceof WarframeCommunityAliasDataset value) {
            value.setSourceId(longValue(json, "id"));
            value.setAliasName(text(json, "cn"));
            value.setTargetName(text(json, "en"));
        } else if (record instanceof WarframeCommunityNodesDataset value) {
            value.setUniqueName(text(json, "uniqueName"));
            value.setDisplayName(text(json, "name"));
            value.setSystemIndex(integerValue(json, "systemIndex"));
            value.setSystemName(text(json, "systemName"));
            value.setNodeType(integerValue(json, "nodeType"));
            value.setMasteryReq(integerValue(json, "masteryReq"));
            value.setMissionIndex(integerValue(json, "missionIndex"));
            value.setFactionIndex(integerValue(json, "factionIndex"));
            value.setMinEnemyLevel(integerValue(json, "minEnemyLevel"));
            value.setMaxEnemyLevel(integerValue(json, "maxEnemyLevel"));
        } else if (record instanceof WarframeCommunityRewardPoolDataset value) {
            JsonNode rewards = json.get("rewards");
            value.setUniqueName(text(json, "uniqueName"));
            value.setRewardCount(rewards != null && rewards.isArray() ? rewards.size() : 0);
            value.setRewardsJson(rewards == null || rewards.isNull() ? "[]" : rewards.toString());
        } else if (record instanceof WarframeCommunityStateTranslationDataset value) {
            value.setUniqueName(text(json, "uniqueName"));
            value.setDisplayName(text(json, "name"));
            value.setDescription(text(json, "description"));
        } else if (record instanceof WarframeCommunityMarketElementDataset value) {
            value.setSourceId(longValue(json, "id"));
            value.setElementCode(text(json, "en"));
            value.setDisplayName(text(json, "cn"));
        } else if (record instanceof WarframeCommunityMarketRivenTionDataset value) {
            value.setSourceId(longValue(json, "ids"));
            value.setEffect(text(json, "effect"));
            value.setAttributeGroup(text(json, "group"));
            value.setNegativeOnly(booleanValue(json, "negative_only"));
            value.setPositiveIsNegative(booleanValue(json, "positive_is_negative"));
            value.setPrefix(text(json, "prefix"));
            value.setSearchOnly(booleanValue(json, "search_only"));
            value.setSuffix(text(json, "suffix"));
            value.setUnits(text(json, "units"));
            value.setUrlName(text(json, "url_name"));
            value.setExclusiveToJson(jsonText(json, "exclusive_to"));
        } else if (record instanceof WarframeCommunityMarketRivenTionAliasDataset value) {
            value.setSourceId(longValue(json, "id"));
            value.setAttributeCode(text(json, "en"));
            value.setAliasName(text(json, "cn"));
        } else if (record instanceof WarframeCommunityRivenAnalyseTrendDataset value) {
            value.setSourceId(longValue(json, "id"));
            value.setDisplayName(text(json, "name"));
            value.setPrefix(text(json, "prefix"));
            value.setSuffix(text(json, "suffix"));
            value.setArchwing(doubleValue(json, "archwing"));
            value.setMeleeValue(doubleValue(json, "melle"));
            value.setPistol(doubleValue(json, "pistol"));
            value.setRifle(doubleValue(json, "rifle"));
            value.setShotgun(doubleValue(json, "shotgun"));
        } else if (record instanceof WarframeCommunityRivenTrendDataset value) {
            value.setSourceId(longValue(json, "id"));
            value.setTrendTime(text(json, "isDate"));
            value.setNewDot(text(json, "new_dot"));
            value.setNewNum(doubleValue(json, "new_num"));
            value.setOldDot(text(json, "old_dot"));
            value.setOldNum(doubleValue(json, "old_num"));
            value.setTrendName(text(json, "trend_name"));
            value.setWeaponType(text(json, "type"));
        }
    }

    public WarframeCommunityDatasetDetails details(WarframeCommunityDatasetRecord record) {
        return switch (record) {
            case WarframeCommunityTranslationDataset value ->
                    new WarframeCommunityTranslationDetails(
                            value.getSourceId(), value.getChineseName(), value.getEnglishName(),
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
                            value.getUniqueName(), value.getRewardCount(), value.getRewardsJson());
            case WarframeCommunityStateTranslationDataset value ->
                    new WarframeCommunityStateTranslationDetails(
                            value.getUniqueName(), value.getDisplayName(), value.getDescription());
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
            default -> throw new IllegalArgumentException(
                    "不支持的社区数据实体：" + record.getClass().getName());
        };
    }

    private static String text(JsonNode json, String field) {
        JsonNode value = json.get(field);
        return value == null || value.isNull() ? null : StrUtil.trim(value.asText());
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

    private static Integer integerValue(JsonNode json, String field) {
        JsonNode value = json.get(field);
        return value == null || value.isNull() || !value.canConvertToInt()
                ? null : value.intValue();
    }

    private static Double doubleValue(JsonNode json, String field) {
        JsonNode value = json.get(field);
        return value == null || value.isNull() || !value.isNumber()
                ? null : value.doubleValue();
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
