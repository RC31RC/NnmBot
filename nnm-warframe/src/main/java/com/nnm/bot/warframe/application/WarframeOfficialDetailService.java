package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.model.entity.WarframeOfficialExportRecord;
import com.nnm.bot.warframe.model.entity.WarframeOfficialRecipe;
import com.nnm.bot.warframe.model.entity.WarframeOfficialRegion;
import com.nnm.bot.warframe.model.entity.WarframeOfficialRelicArcane;
import com.nnm.bot.warframe.model.entity.WarframeOfficialUpgrade;
import com.nnm.bot.warframe.model.entity.WarframeOfficialWarframe;
import com.nnm.bot.warframe.model.entity.WarframeOfficialWeapon;
import com.nnm.bot.warframe.repository.WarframeOfficialRecipeRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialRegionRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialRelicArcaneRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialUpgradeRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialWarframeRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialWeaponRepository;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 维护 PublicExport 各领域详情表。
 *
 * <p>基础表保留共同身份和完整双语 JSON；这里仅提取高频查询字段。官方新增字段不会丢失，
 * 后续支持结构化字段时可以直接从基础表原始 JSON 重建。</p>
 */
@Service
public class WarframeOfficialDetailService {

    private static final int DETAIL_BATCH_SIZE = 500;

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialWeaponRepository weaponRepository;
    @Resource
    private WarframeOfficialWarframeRepository warframeRepository;
    @Resource
    private WarframeOfficialUpgradeRepository upgradeRepository;
    @Resource
    private WarframeOfficialRelicArcaneRepository relicArcaneRepository;
    @Resource
    private WarframeOfficialRegionRepository regionRepository;
    @Resource
    private WarframeOfficialRecipeRepository recipeRepository;

    public void synchronize(
            List<WarframeOfficialExportRecord> records,
            Collection<Long> disabledRecordIds) {
        List<WarframeOfficialExportRecord> activeRecords =
                records == null ? List.of() : records;
        Set<Long> disabledIds = disabledRecordIds == null
                ? Set.of()
                : disabledRecordIds.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        if (activeRecords.isEmpty() && disabledIds.isEmpty()) {
            return;
        }
        Set<Long> importedIds = activeRecords.stream()
                .map(WarframeOfficialExportRecord::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        synchronizeDomain(activeRecords, importedIds, disabledIds,
                WarframeOfficialDataDomain.WEAPON,
                weaponRepository, this::weapon, WarframeOfficialWeapon::getOfficialRecordId);
        synchronizeDomain(activeRecords, importedIds, disabledIds,
                WarframeOfficialDataDomain.WARFRAME,
                warframeRepository, this::warframe,
                WarframeOfficialWarframe::getOfficialRecordId);
        synchronizeDomain(activeRecords, importedIds, disabledIds,
                WarframeOfficialDataDomain.UPGRADE,
                upgradeRepository, this::upgrade, WarframeOfficialUpgrade::getOfficialRecordId);
        synchronizeDomain(activeRecords, importedIds, disabledIds,
                WarframeOfficialDataDomain.RELIC_ARCANE,
                relicArcaneRepository, this::relicArcane,
                WarframeOfficialRelicArcane::getOfficialRecordId);
        synchronizeDomain(activeRecords, importedIds, disabledIds,
                WarframeOfficialDataDomain.REGION,
                regionRepository, this::region, WarframeOfficialRegion::getOfficialRecordId);
        synchronizeDomain(activeRecords, importedIds, disabledIds,
                WarframeOfficialDataDomain.RECIPE,
                recipeRepository, this::recipe, WarframeOfficialRecipe::getOfficialRecordId);
    }

    public Map<Long, Map<String, Object>> loadAttributes(Collection<Long> recordIds) {
        return loadAttributes(null, recordIds);
    }

    public Map<Long, Map<String, Object>> loadAttributes(
            WarframeOfficialDataDomain domain,
            Collection<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Map<String, Object>> result = new LinkedHashMap<>();
        if (domain == null || domain == WarframeOfficialDataDomain.WEAPON) {
            loadWeapons(recordIds, result);
        }
        if (domain == null || domain == WarframeOfficialDataDomain.WARFRAME) {
            loadWarframes(recordIds, result);
        }
        if (domain == null || domain == WarframeOfficialDataDomain.UPGRADE) {
            loadUpgrades(recordIds, result);
        }
        if (domain == null || domain == WarframeOfficialDataDomain.RELIC_ARCANE) {
            loadRelicArcanes(recordIds, result);
        }
        if (domain == null || domain == WarframeOfficialDataDomain.REGION) {
            loadRegions(recordIds, result);
        }
        if (domain == null || domain == WarframeOfficialDataDomain.RECIPE) {
            loadRecipes(recordIds, result);
        }
        return result;
    }

    private void loadWeapons(
            Collection<Long> recordIds,
            Map<Long, Map<String, Object>> result) {
        findAllByIdInBatches(weaponRepository, recordIds).forEach(item -> {
            Map<String, Object> values = values(result, item.getOfficialRecordId());
            put(values, "masteryReq", item.getMasteryReq());
            put(values, "totalDamage", item.getTotalDamage());
            put(values, "damagePerShot", item.getDamagePerShotJson());
            put(values, "criticalChance", item.getCriticalChance());
            put(values, "criticalMultiplier", item.getCriticalMultiplier());
            put(values, "procChance", item.getProcChance());
            put(values, "fireRate", item.getFireRate());
            put(values, "accuracy", item.getAccuracy());
            put(values, "magazineSize", item.getMagazineSize());
            put(values, "reloadTime", item.getReloadTime());
            put(values, "multishot", item.getMultishot());
            put(values, "slot", item.getSlot());
            put(values, "noise", item.getNoise());
            put(values, "trigger", item.getTrigger());
            put(values, "omegaAttenuation", item.getOmegaAttenuation());
            put(values, "range", item.getRangeJson());
            put(values, "slamAttack", item.getSlamAttack());
            put(values, "slideAttack", item.getSlideAttack());
            put(values, "windUp", item.getWindUp());
            put(values, "levelStats", item.getLevelStatsJson());
            put(values, "ingredients", item.getIngredientsJson());
            put(values, "tags", item.getTagsJson());
        });
    }

    private void loadWarframes(
            Collection<Long> recordIds,
            Map<Long, Map<String, Object>> result) {
        findAllByIdInBatches(warframeRepository, recordIds).forEach(item -> {
            Map<String, Object> values = values(result, item.getOfficialRecordId());
            put(values, "health", item.getHealth());
            put(values, "shield", item.getShield());
            put(values, "armor", item.getArmor());
            put(values, "stamina", item.getStamina());
            put(values, "power", item.getPower());
            put(values, "masteryReq", item.getMasteryReq());
            put(values, "sprintSpeed", item.getSprintSpeed());
            put(values, "codexSecret", item.getCodexSecret());
            put(values, "abilities", item.getAbilitiesJson());
            put(values, "exalted", item.getExaltedJson());
            put(values, "stats", item.getStatsJson());
            put(values, "tags", item.getTagsJson());
        });
    }

    private void loadUpgrades(
            Collection<Long> recordIds,
            Map<Long, Map<String, Object>> result) {
        findAllByIdInBatches(upgradeRepository, recordIds).forEach(item -> {
            Map<String, Object> values = values(result, item.getOfficialRecordId());
            put(values, "baseDrain", item.getBaseDrain());
            put(values, "fusionLimit", item.getFusionLimit());
            put(values, "compatName", item.getCompatName());
            put(values, "type", item.getType());
            put(values, "subtype", item.getSubtype());
            put(values, "polarity", item.getPolarity());
            put(values, "rarity", item.getRarity());
            put(values, "isUtility", item.getUtility());
            put(values, "isAugment", item.getAugment());
            put(values, "isExilus", item.getExilus());
            put(values, "numUpgradesInSet", item.getNumUpgradesInSet());
            put(values, "modSet", item.getModSet());
            put(values, "modSetValues", item.getModSetValuesJson());
            put(values, "availableChallenges", item.getAvailableChallengesJson());
            put(values, "levelStats", item.getLevelStatsJson());
            put(values, "upgradeEntries", item.getUpgradeEntriesJson());
            put(values, "tags", item.getTagsJson());
        });
    }

    private void loadRelicArcanes(
            Collection<Long> recordIds,
            Map<Long, Map<String, Object>> result) {
        findAllByIdInBatches(relicArcaneRepository, recordIds).forEach(item -> {
            Map<String, Object> values = values(result, item.getOfficialRecordId());
            put(values, "type", item.getType());
            put(values, "rarity", item.getRarity());
            put(values, "levelStats", item.getLevelStatsJson());
            put(values, "relicRewards", item.getRelicRewardsJson());
        });
    }

    private void loadRegions(
            Collection<Long> recordIds,
            Map<Long, Map<String, Object>> result) {
        findAllByIdInBatches(regionRepository, recordIds).forEach(item -> {
            Map<String, Object> values = values(result, item.getOfficialRecordId());
            put(values, "systemName", item.getSystemName());
            put(values, "systemIndex", item.getSystemIndex());
            put(values, "nodeType", item.getNodeType());
            put(values, "masteryReq", item.getMasteryReq());
            put(values, "missionIndex", item.getMissionIndex());
            put(values, "factionIndex", item.getFactionIndex());
            put(values, "minEnemyLevel", item.getMinEnemyLevel());
            put(values, "maxEnemyLevel", item.getMaxEnemyLevel());
        });
    }

    private void loadRecipes(
            Collection<Long> recordIds,
            Map<Long, Map<String, Object>> result) {
        findAllByIdInBatches(recipeRepository, recordIds).forEach(item -> {
            Map<String, Object> values = values(result, item.getOfficialRecordId());
            put(values, "resultType", item.getResultType());
            put(values, "buildPrice", item.getBuildPrice());
            put(values, "buildTime", item.getBuildTime());
            put(values, "consumeOnUse", item.getConsumeOnUse());
            put(values, "skipBuildTimePrice", item.getSkipBuildTimePrice());
            put(values, "num", item.getNum());
            put(values, "primeSellingPrice", item.getPrimeSellingPrice());
            put(values, "alwaysAvailable", item.getAlwaysAvailable());
            put(values, "ingredients", item.getIngredientsJson());
            put(values, "secretIngredients", item.getSecretIngredientsJson());
        });
    }

    private <T> void synchronizeDomain(
            List<WarframeOfficialExportRecord> records,
            Set<Long> importedIds,
            Set<Long> disabledIds,
            WarframeOfficialDataDomain domain,
            JpaRepository<T, Long> repository,
            Function<WarframeOfficialExportRecord, T> mapper,
            Function<T, Long> idExtractor) {
        List<T> desired = records.stream()
                .filter(record -> domain == WarframeOfficialDataDomain.fromExportType(
                        record.getExportType()))
                .map(mapper)
                .toList();
        Set<Long> desiredIds = desired.stream()
                .map(idExtractor)
                .collect(Collectors.toSet());
        Set<Long> staleIds = new HashSet<>(disabledIds);
        if (!importedIds.isEmpty() || !desiredIds.isEmpty()) {
            repository.findAll().stream()
                    .map(idExtractor)
                    .filter(Objects::nonNull)
                    .filter(importedIds::contains)
                    .filter(id -> !desiredIds.contains(id))
                    .forEach(staleIds::add);
        }
        staleIds.removeAll(desiredIds);
        List<Long> staleIdList = List.copyOf(staleIds);
        for (int from = 0; from < staleIdList.size(); from += DETAIL_BATCH_SIZE) {
            int to = Math.min(from + DETAIL_BATCH_SIZE, staleIdList.size());
            List<Long> batch = staleIdList.subList(from, to);
            repository.deleteAllByIdInBatch(batch.size() == staleIdList.size()
                    ? staleIds : batch);
        }
        for (int from = 0; from < desired.size(); from += DETAIL_BATCH_SIZE) {
            int to = Math.min(from + DETAIL_BATCH_SIZE, desired.size());
            repository.saveAll(desired.subList(from, to));
        }
        if (!desired.isEmpty() || !staleIdList.isEmpty()) {
            repository.flush();
        }
    }

    private <T, ID> List<T> findAllByIdInBatches(
            JpaRepository<T, ID> repository,
            Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<ID> idList = new ArrayList<>(ids);
        List<T> result = new ArrayList<>(idList.size());
        for (int from = 0; from < idList.size(); from += DETAIL_BATCH_SIZE) {
            int to = Math.min(from + DETAIL_BATCH_SIZE, idList.size());
            result.addAll(repository.findAllById(idList.subList(from, to)));
        }
        return result;
    }

    private WarframeOfficialWeapon weapon(WarframeOfficialExportRecord record) {
        JsonValues json = json(record);
        WarframeOfficialWeapon item = new WarframeOfficialWeapon();
        item.setOfficialRecordId(record.getId());
        item.setMasteryReq(json.integer("masteryReq"));
        item.setTotalDamage(json.decimal("totalDamage"));
        item.setDamagePerShotJson(json.json("damagePerShot"));
        item.setCriticalChance(json.decimal("criticalChance"));
        item.setCriticalMultiplier(json.decimal("criticalMultiplier"));
        item.setProcChance(json.decimal("procChance"));
        item.setFireRate(json.decimal("fireRate"));
        item.setAccuracy(json.decimal("accuracy"));
        item.setMagazineSize(json.integer("magazineSize"));
        item.setReloadTime(json.decimal("reloadTime"));
        item.setMultishot(json.decimal("multishot"));
        item.setSlot(json.integer("slot"));
        item.setNoise(json.text("noise"));
        item.setTrigger(json.text("trigger"));
        item.setOmegaAttenuation(json.decimal("omegaAttenuation"));
        item.setRangeJson(json.json("range"));
        item.setSlamAttack(json.decimal("slamAttack"));
        item.setSlideAttack(json.decimal("slideAttack"));
        item.setWindUp(json.decimal("windUp"));
        item.setLevelStatsJson(json.json("levelStats"));
        item.setIngredientsJson(json.json("ingredients"));
        item.setTagsJson(json.json("tags"));
        return item;
    }

    private WarframeOfficialWarframe warframe(WarframeOfficialExportRecord record) {
        JsonValues json = json(record);
        WarframeOfficialWarframe item = new WarframeOfficialWarframe();
        item.setOfficialRecordId(record.getId());
        item.setHealth(json.integer("health"));
        item.setShield(json.integer("shield"));
        item.setArmor(json.integer("armor"));
        item.setStamina(json.integer("stamina"));
        item.setPower(json.integer("power"));
        item.setMasteryReq(json.integer("masteryReq"));
        item.setSprintSpeed(json.decimal("sprintSpeed"));
        item.setCodexSecret(json.bool("codexSecret"));
        item.setAbilitiesJson(json.json("abilities"));
        item.setExaltedJson(json.json("exalted"));
        item.setStatsJson(json.json("stats"));
        item.setTagsJson(json.json("tags"));
        return item;
    }

    private WarframeOfficialUpgrade upgrade(WarframeOfficialExportRecord record) {
        JsonValues json = json(record);
        WarframeOfficialUpgrade item = new WarframeOfficialUpgrade();
        item.setOfficialRecordId(record.getId());
        item.setBaseDrain(json.integer("baseDrain"));
        item.setFusionLimit(json.integer("fusionLimit"));
        item.setCompatName(json.text("compatName"));
        item.setType(json.text("type"));
        item.setSubtype(json.text("subtype"));
        item.setPolarity(json.text("polarity"));
        item.setRarity(json.text("rarity"));
        item.setUtility(json.bool("isUtility"));
        item.setAugment(json.bool("isAugment"));
        item.setExilus(json.bool("isExilus"));
        item.setNumUpgradesInSet(json.integer("numUpgradesInSet"));
        item.setModSet(json.text("modSet"));
        item.setModSetValuesJson(json.json("modSetValues"));
        item.setAvailableChallengesJson(json.json("availableChallenges"));
        item.setLevelStatsJson(json.json("levelStats"));
        item.setUpgradeEntriesJson(json.json("upgradeEntries"));
        item.setTagsJson(json.json("tags"));
        return item;
    }

    private WarframeOfficialRelicArcane relicArcane(WarframeOfficialExportRecord record) {
        JsonValues json = json(record);
        WarframeOfficialRelicArcane item = new WarframeOfficialRelicArcane();
        item.setOfficialRecordId(record.getId());
        item.setType(json.text("type"));
        item.setRarity(json.text("rarity"));
        item.setLevelStatsJson(json.json("levelStats"));
        item.setRelicRewardsJson(json.json("relicRewards"));
        return item;
    }

    private WarframeOfficialRegion region(WarframeOfficialExportRecord record) {
        JsonValues json = json(record);
        WarframeOfficialRegion item = new WarframeOfficialRegion();
        item.setOfficialRecordId(record.getId());
        item.setSystemName(json.text("systemName"));
        item.setSystemIndex(json.integer("systemIndex"));
        item.setNodeType(json.integer("nodeType"));
        item.setMasteryReq(json.integer("masteryReq"));
        item.setMissionIndex(json.integer("missionIndex"));
        item.setFactionIndex(json.integer("factionIndex"));
        item.setMinEnemyLevel(json.integer("minEnemyLevel"));
        item.setMaxEnemyLevel(json.integer("maxEnemyLevel"));
        return item;
    }

    private WarframeOfficialRecipe recipe(WarframeOfficialExportRecord record) {
        JsonValues json = json(record);
        WarframeOfficialRecipe item = new WarframeOfficialRecipe();
        item.setOfficialRecordId(record.getId());
        item.setResultType(json.text("resultType"));
        item.setBuildPrice(json.integer("buildPrice"));
        item.setBuildTime(json.integer("buildTime"));
        item.setConsumeOnUse(json.bool("consumeOnUse"));
        item.setSkipBuildTimePrice(json.integer("skipBuildTimePrice"));
        item.setNum(json.integer("num"));
        item.setPrimeSellingPrice(json.integer("primeSellingPrice"));
        item.setAlwaysAvailable(json.bool("alwaysAvailable"));
        item.setIngredientsJson(json.json("ingredients"));
        item.setSecretIngredientsJson(json.json("secretIngredients"));
        return item;
    }

    private JsonValues json(WarframeOfficialExportRecord record) {
        return new JsonValues(
                read(record.getRawJson()),
                read(record.getLocalizedRawJson()));
    }

    private JsonNode read(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Map<String, Object> values(
            Map<Long, Map<String, Object>> target, Long id) {
        return target.computeIfAbsent(id, ignored -> new LinkedHashMap<>());
    }

    private static void put(Map<String, Object> target, String name, Object value) {
        if (value != null && (!(value instanceof String text) || StrUtil.isNotBlank(text))) {
            target.put(name, value);
        }
    }

    private record JsonValues(JsonNode english, JsonNode localized) {

        private JsonNode value(String field) {
            if (english != null && english.hasNonNull(field)) {
                return english.get(field);
            }
            return localized != null && localized.hasNonNull(field)
                    ? localized.get(field) : null;
        }

        private String text(String field) {
            JsonNode value = value(field);
            return value != null && value.isValueNode() ? value.asText() : null;
        }

        private Integer integer(String field) {
            JsonNode value = value(field);
            return value != null && value.canConvertToInt() ? value.intValue() : null;
        }

        private Double decimal(String field) {
            JsonNode value = value(field);
            return value != null && value.isNumber() ? value.doubleValue() : null;
        }

        private Boolean bool(String field) {
            JsonNode value = value(field);
            return value != null && value.isBoolean() ? value.booleanValue() : null;
        }

        private String json(String field) {
            JsonNode value = value(field);
            return value == null || value.isNull() ? null : value.toString();
        }
    }
}
