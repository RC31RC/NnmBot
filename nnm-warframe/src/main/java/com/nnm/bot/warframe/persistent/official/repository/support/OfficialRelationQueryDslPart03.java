package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialGearItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponBehavioursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponCompatibilityTagsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponDamagePerShotItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponDefaultUpgradesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialRailjackWeaponItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 3；由模型生成器维护。 */
final class OfficialRelationQueryDslPart03 {
    private OfficialRelationQueryDslPart03() { }

    /**
     * 查询官方关系表指定主记录的类型化标量投影。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param table 官方关系表名。
     * @param ids 待查询的主记录标识。
     * @return 按记录标识排序的关系行投影。
     */
    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_gear_itemreferences_items" -> {
                QOfficialGearItemReferencesItems row = new QOfficialGearItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_railjack_weapon_root" -> {
                QOfficialRailjackWeaponRoot row = new QOfficialRailjackWeaponRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "accuracyState", "accuracy", "behavioursState", "codexSecretState", "codexSecret", "compatibilityTagsState", "creditsCostState", "creditsCost", "criticalChanceState", "criticalChance", "criticalMultiplierState", "criticalMultiplier", "damagePerShotState", "defaultUpgradesState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "fireRateState", "fireRate", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "magazineSizeState", "magazineSize", "masteryReqState", "masteryReq", "multishotState", "multishot", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "noiseState", "noise", "omegaAttenuationState", "omegaAttenuation", "parentNameState", "parentNameValue", "procChanceState", "procChance", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "reloadTimeState", "reloadTime", "slotState", "slot", "totalDamageState", "totalDamage", "triggerState", "trigger", "uniqueNameState", "uniqueName", "variantTypeState", "variantType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.accuracyState, row.accuracy, row.behavioursState, row.codexSecretState, row.codexSecret, row.compatibilityTagsState, row.creditsCostState, row.creditsCost, row.criticalChanceState, row.criticalChance, row.criticalMultiplierState, row.criticalMultiplier, row.damagePerShotState, row.defaultUpgradesState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.fireRateState, row.fireRate, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.magazineSizeState, row.magazineSize, row.masteryReqState, row.masteryReq, row.multishotState, row.multishot, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.noiseState, row.noise, row.omegaAttenuationState, row.omegaAttenuation, row.parentNameState, row.parentNameValue, row.procChanceState, row.procChance, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.reloadTimeState, row.reloadTime, row.slotState, row.slot, row.totalDamageState, row.totalDamage, row.triggerState, row.trigger, row.uniqueNameState, row.uniqueName, row.variantTypeState, row.variantType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_railjack_weapon_behaviours_items" -> {
                QOfficialRailjackWeaponBehavioursItems row = new QOfficialRailjackWeaponBehavioursItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "burstCountState", "burstCount", "burstDelayState", "burstDelay", "fireIterationsState", "fireIterations", "impactDtElectricityState", "impactDtElectricity", "impactDtFireState", "impactDtFire", "impactDtImpactState", "impactDtImpact", "impactDtPunctureState", "impactDtPuncture", "impactDtSlashState", "impactDtSlash", "impactProcChanceState", "impactProcChance", "projectileAttackDtElectricityState", "projectileAttackDtElectricity", "projectileAttackDtFreezeState", "projectileAttackDtFreeze", "projectileAttackDtImpactState", "projectileAttackDtImpact", "projectileAttackDtPoisonState", "projectileAttackDtPoison", "projectileAttackDtPunctureState", "projectileAttackDtPuncture", "projectileAttackDtSlashState", "projectileAttackDtSlash", "projectileAttackProcChanceState", "projectileAttackProcChance", "stateNameState", "stateName", "stateNameEnState", "stateNameEn", "stateNameZhState", "stateNameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.burstCountState, row.burstCount, row.burstDelayState, row.burstDelay, row.fireIterationsState, row.fireIterations, row.impactDtElectricityState, row.impactDtElectricity, row.impactDtFireState, row.impactDtFire, row.impactDtImpactState, row.impactDtImpact, row.impactDtPunctureState, row.impactDtPuncture, row.impactDtSlashState, row.impactDtSlash, row.impactProcChanceState, row.impactProcChance, row.projectileAttackDtElectricityState, row.projectileAttackDtElectricity, row.projectileAttackDtFreezeState, row.projectileAttackDtFreeze, row.projectileAttackDtImpactState, row.projectileAttackDtImpact, row.projectileAttackDtPoisonState, row.projectileAttackDtPoison, row.projectileAttackDtPunctureState, row.projectileAttackDtPuncture, row.projectileAttackDtSlashState, row.projectileAttackDtSlash, row.projectileAttackProcChanceState, row.projectileAttackProcChance, row.stateNameState, row.stateName, row.stateNameEnState, row.stateNameEn, row.stateNameZhState, row.stateNameZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_railjack_weapon_compatibilitytags_items" -> {
                QOfficialRailjackWeaponCompatibilityTagsItems row = new QOfficialRailjackWeaponCompatibilityTagsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_railjack_weapon_damagepershot_items" -> {
                QOfficialRailjackWeaponDamagePerShotItems row = new QOfficialRailjackWeaponDamagePerShotItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_railjack_weapon_defaultupgrades_items" -> {
                QOfficialRailjackWeaponDefaultUpgradesItems row = new QOfficialRailjackWeaponDefaultUpgradesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "itemTypeState", "itemType", "slotState", "slot"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemTypeState, row.itemType, row.slotState, row.slot));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_railjack_weapon_defaultupgrades_items_it_86b44aa1db" -> {
                QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems row = new QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_railjack_weapon_itemreferences_items" -> {
                QOfficialRailjackWeaponItemReferencesItems row = new QOfficialRailjackWeaponItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    /**
     * 查询指定官方子表中关联到父记录的子行标识。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param table 官方子表名。
     * @param parentIds 父记录主键集合。
     * @return 匹配的子行主键。
     */
    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_gear_itemreferences_items" -> {
                QOfficialGearItemReferencesItems row = new QOfficialGearItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_railjack_weapon_behaviours_items" -> {
                QOfficialRailjackWeaponBehavioursItems row = new QOfficialRailjackWeaponBehavioursItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_railjack_weapon_compatibilitytags_items" -> {
                QOfficialRailjackWeaponCompatibilityTagsItems row = new QOfficialRailjackWeaponCompatibilityTagsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_railjack_weapon_damagepershot_items" -> {
                QOfficialRailjackWeaponDamagePerShotItems row = new QOfficialRailjackWeaponDamagePerShotItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_railjack_weapon_defaultupgrades_items" -> {
                QOfficialRailjackWeaponDefaultUpgradesItems row = new QOfficialRailjackWeaponDefaultUpgradesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_railjack_weapon_defaultupgrades_items_it_86b44aa1db" -> {
                QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems row = new QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_railjack_weapon_itemreferences_items" -> {
                QOfficialRailjackWeaponItemReferencesItems row = new QOfficialRailjackWeaponItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    /**
     * 按标识批量删除官方关系表记录。
     *
     * @param query 执行删除的 QueryDSL 工厂。
     * @param table 官方关系表名。
     * @param ids 待删除的关系记录主键。
     * @return 实际删除的记录数。
     */
    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_gear_itemreferences_items" -> {
                QOfficialGearItemReferencesItems row = new QOfficialGearItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_root" -> {
                QOfficialRailjackWeaponRoot row = new QOfficialRailjackWeaponRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_behaviours_items" -> {
                QOfficialRailjackWeaponBehavioursItems row = new QOfficialRailjackWeaponBehavioursItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_compatibilitytags_items" -> {
                QOfficialRailjackWeaponCompatibilityTagsItems row = new QOfficialRailjackWeaponCompatibilityTagsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_damagepershot_items" -> {
                QOfficialRailjackWeaponDamagePerShotItems row = new QOfficialRailjackWeaponDamagePerShotItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_defaultupgrades_items" -> {
                QOfficialRailjackWeaponDefaultUpgradesItems row = new QOfficialRailjackWeaponDefaultUpgradesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_defaultupgrades_items_it_86b44aa1db" -> {
                QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems row = new QOfficialRailjackWeaponDefaultUpgradesItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_railjack_weapon_itemreferences_items" -> {
                QOfficialRailjackWeaponItemReferencesItems row = new QOfficialRailjackWeaponItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    /**
     * 查询官方独立主表的关系字段投影。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param row 当前官方主表的实体路径。
     * @param id 主表主键路径。
     * @param ids 待查询的主记录标识。
     * @param projection 关系字段到非受管 DTO 的投影。
     * @return 按主键排序的关系行投影。
     */
    private static <T> List<OfficialRelationRowDTO> selectRoot(JPAQueryFactory query, EntityPathBase<T> row,
            NumberPath<Long> id, Collection<Long> ids, OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(id.in(ids)).orderBy(id.asc()).fetch();
    }

    /**
     * 按父记录及源顺序查询官方子表字段投影。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param row 当前官方子表的实体路径。
     * @param id 子表主键路径。
     * @param parentId 直接父记录主键路径。
     * @param sortOrder 源文件中的子行顺序路径。
     * @param ids 父记录主键集合。
     * @param projection 关系字段到非受管 DTO 的投影。
     * @return 按父记录和源顺序排列的关系行投影。
     */
    private static <T> List<OfficialRelationRowDTO> selectChildren(JPAQueryFactory query, EntityPathBase<T> row,
            NumberPath<Long> id, NumberPath<Long> parentId, NumberPath<Integer> sortOrder, Collection<Long> ids,
            OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(parentId.in(ids))
                .orderBy(parentId.asc(), sortOrder.asc(), id.asc()).fetch();
    }
}
