package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeExaltedItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponAdditionalItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponBehavioursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponCompatibilityTagsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponDamagePerShotItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponDefaultUpgradesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 5；由模型生成器维护。 */
final class OfficialRelationQueryDslPart05 {
    private OfficialRelationQueryDslPart05() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_warframe_exalted_items" -> {
                QOfficialWarframeExaltedItems row = new QOfficialWarframeExaltedItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_warframe_itemreferences_items" -> {
                QOfficialWarframeItemReferencesItems row = new QOfficialWarframeItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_weapon_root" -> {
                QOfficialWeaponRoot row = new QOfficialWeaponRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "accuracyState", "accuracy", "additionalItemsState", "bayonetOtherWeaponTypeState", "bayonetOtherWeaponType", "behavioursState", "blockingAngleState", "blockingAngle", "codexSecretState", "codexSecret", "comboDurationState", "comboDuration", "compatibilityTagsState", "creditsCostState", "creditsCost", "criticalChanceState", "criticalChance", "criticalMultiplierState", "criticalMultiplier", "damagePerShotState", "defaultUpgradesState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "donationStandingBonusState", "donationStandingBonus", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "fireRateState", "fireRate", "followThroughState", "followThrough", "gunTypeState", "gunType", "heavyAttackDamageState", "heavyAttackDamage", "heavySlamAttackState", "heavySlamAttack", "heavySlamRadialDamageState", "heavySlamRadialDamage", "heavySlamRadiusState", "heavySlamRadius", "holsterCategoryState", "holsterCategory", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "introducedAtState", "introducedAt", "itemReferencesState", "magazineSizeState", "magazineSize", "masteryReqState", "masteryReq", "maxLevelCapState", "maxLevelCap", "multishotState", "multishot", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "noiseState", "noise", "omegaAttenuationState", "omegaAttenuation", "oneTimePurchasableState", "oneTimePurchasable", "parentNameState", "parentNameValue", "partTypeState", "partType", "platinumCostState", "platinumCost", "primeOmegaAttenuationState", "primeOmegaAttenuation", "procChanceState", "procChance", "productCategoryState", "productCategoryValue", "rangeState", "range", "recordKeyState", "recordKey", "reloadTimeState", "reloadTime", "sentinelState", "sentinel", "slamAttackState", "slamAttack", "slamRadialDamageState", "slamRadialDamage", "slamRadiusState", "slamRadius", "slideAttackState", "slideAttack", "slotState", "slot", "totalDamageState", "totalDamage", "tradableState", "tradable", "triggerState", "trigger", "uniqueNameState", "uniqueName", "variantTypeState", "variantType", "windUpState", "windUp"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.accuracyState, row.accuracy, row.additionalItemsState, row.bayonetOtherWeaponTypeState, row.bayonetOtherWeaponType, row.behavioursState, row.blockingAngleState, row.blockingAngle, row.codexSecretState, row.codexSecret, row.comboDurationState, row.comboDuration, row.compatibilityTagsState, row.creditsCostState, row.creditsCost, row.criticalChanceState, row.criticalChance, row.criticalMultiplierState, row.criticalMultiplier, row.damagePerShotState, row.defaultUpgradesState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.donationStandingBonusState, row.donationStandingBonus, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.fireRateState, row.fireRate, row.followThroughState, row.followThrough, row.gunTypeState, row.gunType, row.heavyAttackDamageState, row.heavyAttackDamage, row.heavySlamAttackState, row.heavySlamAttack, row.heavySlamRadialDamageState, row.heavySlamRadialDamage, row.heavySlamRadiusState, row.heavySlamRadius, row.holsterCategoryState, row.holsterCategory, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.introducedAtState, row.introducedAt, row.itemReferencesState, row.magazineSizeState, row.magazineSize, row.masteryReqState, row.masteryReq, row.maxLevelCapState, row.maxLevelCap, row.multishotState, row.multishot, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.noiseState, row.noise, row.omegaAttenuationState, row.omegaAttenuation, row.oneTimePurchasableState, row.oneTimePurchasable, row.parentNameState, row.parentNameValue, row.partTypeState, row.partType, row.platinumCostState, row.platinumCost, row.primeOmegaAttenuationState, row.primeOmegaAttenuation, row.procChanceState, row.procChance, row.productCategoryState, row.productCategoryValue, row.rangeState, row.range, row.recordKeyState, row.recordKey, row.reloadTimeState, row.reloadTime, row.sentinelState, row.sentinel, row.slamAttackState, row.slamAttack, row.slamRadialDamageState, row.slamRadialDamage, row.slamRadiusState, row.slamRadius, row.slideAttackState, row.slideAttack, row.slotState, row.slot, row.totalDamageState, row.totalDamage, row.tradableState, row.tradable, row.triggerState, row.trigger, row.uniqueNameState, row.uniqueName, row.variantTypeState, row.variantType, row.windUpState, row.windUp));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_weapon_additionalitems_items" -> {
                QOfficialWeaponAdditionalItemsItems row = new QOfficialWeaponAdditionalItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_weapon_behaviours_items" -> {
                QOfficialWeaponBehavioursItems row = new QOfficialWeaponBehavioursItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "burstCountState", "burstCount", "burstDelayState", "burstDelay", "chargedProjectileAttackDtCorrosiveState", "chargedProjectileAttackDtCorrosive", "chargedProjectileAttackDtElectricityState", "chargedProjectileAttackDtElectricity", "chargedProjectileAttackDtExplosionState", "chargedProjectileAttackDtExplosion", "chargedProjectileAttackDtFireState", "chargedProjectileAttackDtFire", "chargedProjectileAttackDtFreezeState", "chargedProjectileAttackDtFreeze", "chargedProjectileAttackDtImpactState", "chargedProjectileAttackDtImpact", "chargedProjectileAttackDtMagneticState", "chargedProjectileAttackDtMagnetic", "chargedProjectileAttackDtPunctureState", "chargedProjectileAttackDtPuncture", "chargedProjectileAttackDtRadiationState", "chargedProjectileAttackDtRadiation", "chargedProjectileAttackDtSlashState", "chargedProjectileAttackDtSlash", "chargedProjectileAttackProcChanceState", "chargedProjectileAttackProcChance", "chargedProjectileEmbedDeathAttackDtExplosionState", "chargedProjectileEmbedDeathAttackDtExplosion", "chargedProjectileEmbedDeathAttackDtRadiationState", "chargedProjectileEmbedDeathAttackDtRadiation", "chargedProjectileEmbedDeathAttackDtViralState", "chargedProjectileEmbedDeathAttackDtViral", "chargedProjectileEmbedDeathAttackProcChanceState", "chargedProjectileEmbedDeathAttackProcChance", "chargedProjectileExplosiveAttackDtCorrosiveState", "chargedProjectileExplosiveAttackDtCorrosive", "chargedProjectileExplosiveAttackDtExplosionState", "chargedProjectileExplosiveAttackDtExplosion", "chargedProjectileExplosiveAttackDtFireState", "chargedProjectileExplosiveAttackDtFire", "chargedProjectileExplosiveAttackDtFreezeState", "chargedProjectileExplosiveAttackDtFreeze", "chargedProjectileExplosiveAttackDtPunctureState", "chargedProjectileExplosiveAttackDtPuncture", "chargedProjectileExplosiveAttackDtRadiationState", "chargedProjectileExplosiveAttackDtRadiation", "chargedProjectileExplosiveAttackDtSlashState", "chargedProjectileExplosiveAttackDtSlash", "chargedProjectileExplosiveAttackProcChanceState", "chargedProjectileExplosiveAttackProcChance", "fireIterationsState", "fireIterations", "impactDtCorrosiveState", "impactDtCorrosive", "impactDtElectricityState", "impactDtElectricity", "impactDtExplosionState", "impactDtExplosion", "impactDtFireState", "impactDtFire", "impactDtFreezeState", "impactDtFreeze", "impactDtGasState", "impactDtGas", "impactDtImpactState", "impactDtImpact", "impactDtMagneticState", "impactDtMagnetic", "impactDtPoisonState", "impactDtPoison", "impactDtPunctureState", "impactDtPuncture", "impactDtRadiationState", "impactDtRadiation", "impactDtSentientState", "impactDtSentient", "impactDtSlashState", "impactDtSlash", "impactDtViralState", "impactDtViral", "impactProcChanceState", "impactProcChance", "projectileAttackDtCorrosiveState", "projectileAttackDtCorrosive", "projectileAttackDtElectricityState", "projectileAttackDtElectricity", "projectileAttackDtExplosionState", "projectileAttackDtExplosion", "projectileAttackDtFireState", "projectileAttackDtFire", "projectileAttackDtFreezeState", "projectileAttackDtFreeze", "projectileAttackDtImpactState", "projectileAttackDtImpact", "projectileAttackDtMagneticState", "projectileAttackDtMagnetic", "projectileAttackDtPoisonState", "projectileAttackDtPoison", "projectileAttackDtPunctureState", "projectileAttackDtPuncture", "projectileAttackDtRadiantState", "projectileAttackDtRadiant", "projectileAttackDtRadiationState", "projectileAttackDtRadiation", "projectileAttackDtSlashState", "projectileAttackDtSlash", "projectileAttackDtViralState", "projectileAttackDtViral", "projectileAttackProcChanceState", "projectileAttackProcChance", "projectileEmbedDeathAttackDtCorrosiveState", "projectileEmbedDeathAttackDtCorrosive", "projectileEmbedDeathAttackDtElectricityState", "projectileEmbedDeathAttackDtElectricity", "projectileEmbedDeathAttackDtExplosionState", "projectileEmbedDeathAttackDtExplosion", "projectileEmbedDeathAttackDtFireState", "projectileEmbedDeathAttackDtFire", "projectileEmbedDeathAttackDtFreezeState", "projectileEmbedDeathAttackDtFreeze", "projectileEmbedDeathAttackDtGasState", "projectileEmbedDeathAttackDtGas", "projectileEmbedDeathAttackDtImpactState", "projectileEmbedDeathAttackDtImpact", "projectileEmbedDeathAttackDtPoisonState", "projectileEmbedDeathAttackDtPoison", "projectileEmbedDeathAttackDtPunctureState", "projectileEmbedDeathAttackDtPuncture", "projectileEmbedDeathAttackDtRadiationState", "projectileEmbedDeathAttackDtRadiation", "projectileEmbedDeathAttackDtSlashState", "projectileEmbedDeathAttackDtSlash", "projectileEmbedDeathAttackDtViralState", "projectileEmbedDeathAttackDtViral", "projectileEmbedDeathAttackProcChanceState", "projectileEmbedDeathAttackProcChance", "projectileExplosiveAttackDtCorrosiveState", "projectileExplosiveAttackDtCorrosive", "projectileExplosiveAttackDtElectricityState", "projectileExplosiveAttackDtElectricity", "projectileExplosiveAttackDtExplosionState", "projectileExplosiveAttackDtExplosion", "projectileExplosiveAttackDtFireState", "projectileExplosiveAttackDtFire", "projectileExplosiveAttackDtFreezeState", "projectileExplosiveAttackDtFreeze", "projectileExplosiveAttackDtGasState", "projectileExplosiveAttackDtGas", "projectileExplosiveAttackDtImpactState", "projectileExplosiveAttackDtImpact", "projectileExplosiveAttackDtPoisonState", "projectileExplosiveAttackDtPoison", "projectileExplosiveAttackDtPunctureState", "projectileExplosiveAttackDtPuncture", "projectileExplosiveAttackDtRadiationState", "projectileExplosiveAttackDtRadiation", "projectileExplosiveAttackDtSlashState", "projectileExplosiveAttackDtSlash", "projectileExplosiveAttackDtViralState", "projectileExplosiveAttackDtViral", "projectileExplosiveAttackProcChanceState", "projectileExplosiveAttackProcChance", "stateNameState", "stateName", "stateNameEnState", "stateNameEn", "stateNameZhState", "stateNameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.burstCountState, row.burstCount, row.burstDelayState, row.burstDelay, row.chargedProjectileAttackDtCorrosiveState, row.chargedProjectileAttackDtCorrosive, row.chargedProjectileAttackDtElectricityState, row.chargedProjectileAttackDtElectricity, row.chargedProjectileAttackDtExplosionState, row.chargedProjectileAttackDtExplosion, row.chargedProjectileAttackDtFireState, row.chargedProjectileAttackDtFire, row.chargedProjectileAttackDtFreezeState, row.chargedProjectileAttackDtFreeze, row.chargedProjectileAttackDtImpactState, row.chargedProjectileAttackDtImpact, row.chargedProjectileAttackDtMagneticState, row.chargedProjectileAttackDtMagnetic, row.chargedProjectileAttackDtPunctureState, row.chargedProjectileAttackDtPuncture, row.chargedProjectileAttackDtRadiationState, row.chargedProjectileAttackDtRadiation, row.chargedProjectileAttackDtSlashState, row.chargedProjectileAttackDtSlash, row.chargedProjectileAttackProcChanceState, row.chargedProjectileAttackProcChance, row.chargedProjectileEmbedDeathAttackDtExplosionState, row.chargedProjectileEmbedDeathAttackDtExplosion, row.chargedProjectileEmbedDeathAttackDtRadiationState, row.chargedProjectileEmbedDeathAttackDtRadiation, row.chargedProjectileEmbedDeathAttackDtViralState, row.chargedProjectileEmbedDeathAttackDtViral, row.chargedProjectileEmbedDeathAttackProcChanceState, row.chargedProjectileEmbedDeathAttackProcChance, row.chargedProjectileExplosiveAttackDtCorrosiveState, row.chargedProjectileExplosiveAttackDtCorrosive, row.chargedProjectileExplosiveAttackDtExplosionState, row.chargedProjectileExplosiveAttackDtExplosion, row.chargedProjectileExplosiveAttackDtFireState, row.chargedProjectileExplosiveAttackDtFire, row.chargedProjectileExplosiveAttackDtFreezeState, row.chargedProjectileExplosiveAttackDtFreeze, row.chargedProjectileExplosiveAttackDtPunctureState, row.chargedProjectileExplosiveAttackDtPuncture, row.chargedProjectileExplosiveAttackDtRadiationState, row.chargedProjectileExplosiveAttackDtRadiation, row.chargedProjectileExplosiveAttackDtSlashState, row.chargedProjectileExplosiveAttackDtSlash, row.chargedProjectileExplosiveAttackProcChanceState, row.chargedProjectileExplosiveAttackProcChance, row.fireIterationsState, row.fireIterations, row.impactDtCorrosiveState, row.impactDtCorrosive, row.impactDtElectricityState, row.impactDtElectricity, row.impactDtExplosionState, row.impactDtExplosion, row.impactDtFireState, row.impactDtFire, row.impactDtFreezeState, row.impactDtFreeze, row.impactDtGasState, row.impactDtGas, row.impactDtImpactState, row.impactDtImpact, row.impactDtMagneticState, row.impactDtMagnetic, row.impactDtPoisonState, row.impactDtPoison, row.impactDtPunctureState, row.impactDtPuncture, row.impactDtRadiationState, row.impactDtRadiation, row.impactDtSentientState, row.impactDtSentient, row.impactDtSlashState, row.impactDtSlash, row.impactDtViralState, row.impactDtViral, row.impactProcChanceState, row.impactProcChance, row.projectileAttackDtCorrosiveState, row.projectileAttackDtCorrosive, row.projectileAttackDtElectricityState, row.projectileAttackDtElectricity, row.projectileAttackDtExplosionState, row.projectileAttackDtExplosion, row.projectileAttackDtFireState, row.projectileAttackDtFire, row.projectileAttackDtFreezeState, row.projectileAttackDtFreeze, row.projectileAttackDtImpactState, row.projectileAttackDtImpact, row.projectileAttackDtMagneticState, row.projectileAttackDtMagnetic, row.projectileAttackDtPoisonState, row.projectileAttackDtPoison, row.projectileAttackDtPunctureState, row.projectileAttackDtPuncture, row.projectileAttackDtRadiantState, row.projectileAttackDtRadiant, row.projectileAttackDtRadiationState, row.projectileAttackDtRadiation, row.projectileAttackDtSlashState, row.projectileAttackDtSlash, row.projectileAttackDtViralState, row.projectileAttackDtViral, row.projectileAttackProcChanceState, row.projectileAttackProcChance, row.projectileEmbedDeathAttackDtCorrosiveState, row.projectileEmbedDeathAttackDtCorrosive, row.projectileEmbedDeathAttackDtElectricityState, row.projectileEmbedDeathAttackDtElectricity, row.projectileEmbedDeathAttackDtExplosionState, row.projectileEmbedDeathAttackDtExplosion, row.projectileEmbedDeathAttackDtFireState, row.projectileEmbedDeathAttackDtFire, row.projectileEmbedDeathAttackDtFreezeState, row.projectileEmbedDeathAttackDtFreeze, row.projectileEmbedDeathAttackDtGasState, row.projectileEmbedDeathAttackDtGas, row.projectileEmbedDeathAttackDtImpactState, row.projectileEmbedDeathAttackDtImpact, row.projectileEmbedDeathAttackDtPoisonState, row.projectileEmbedDeathAttackDtPoison, row.projectileEmbedDeathAttackDtPunctureState, row.projectileEmbedDeathAttackDtPuncture, row.projectileEmbedDeathAttackDtRadiationState, row.projectileEmbedDeathAttackDtRadiation, row.projectileEmbedDeathAttackDtSlashState, row.projectileEmbedDeathAttackDtSlash, row.projectileEmbedDeathAttackDtViralState, row.projectileEmbedDeathAttackDtViral, row.projectileEmbedDeathAttackProcChanceState, row.projectileEmbedDeathAttackProcChance, row.projectileExplosiveAttackDtCorrosiveState, row.projectileExplosiveAttackDtCorrosive, row.projectileExplosiveAttackDtElectricityState, row.projectileExplosiveAttackDtElectricity, row.projectileExplosiveAttackDtExplosionState, row.projectileExplosiveAttackDtExplosion, row.projectileExplosiveAttackDtFireState, row.projectileExplosiveAttackDtFire, row.projectileExplosiveAttackDtFreezeState, row.projectileExplosiveAttackDtFreeze, row.projectileExplosiveAttackDtGasState, row.projectileExplosiveAttackDtGas, row.projectileExplosiveAttackDtImpactState, row.projectileExplosiveAttackDtImpact, row.projectileExplosiveAttackDtPoisonState, row.projectileExplosiveAttackDtPoison, row.projectileExplosiveAttackDtPunctureState, row.projectileExplosiveAttackDtPuncture, row.projectileExplosiveAttackDtRadiationState, row.projectileExplosiveAttackDtRadiation, row.projectileExplosiveAttackDtSlashState, row.projectileExplosiveAttackDtSlash, row.projectileExplosiveAttackDtViralState, row.projectileExplosiveAttackDtViral, row.projectileExplosiveAttackProcChanceState, row.projectileExplosiveAttackProcChance, row.stateNameState, row.stateName, row.stateNameEnState, row.stateNameEn, row.stateNameZhState, row.stateNameZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_weapon_compatibilitytags_items" -> {
                QOfficialWeaponCompatibilityTagsItems row = new QOfficialWeaponCompatibilityTagsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_weapon_damagepershot_items" -> {
                QOfficialWeaponDamagePerShotItems row = new QOfficialWeaponDamagePerShotItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_weapon_defaultupgrades_items" -> {
                QOfficialWeaponDefaultUpgradesItems row = new QOfficialWeaponDefaultUpgradesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "itemTypeState", "itemType", "slotState", "slot"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemTypeState, row.itemType, row.slotState, row.slot));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_warframe_exalted_items" -> {
                QOfficialWarframeExaltedItems row = new QOfficialWarframeExaltedItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_warframe_itemreferences_items" -> {
                QOfficialWarframeItemReferencesItems row = new QOfficialWarframeItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_weapon_additionalitems_items" -> {
                QOfficialWeaponAdditionalItemsItems row = new QOfficialWeaponAdditionalItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_weapon_behaviours_items" -> {
                QOfficialWeaponBehavioursItems row = new QOfficialWeaponBehavioursItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_weapon_compatibilitytags_items" -> {
                QOfficialWeaponCompatibilityTagsItems row = new QOfficialWeaponCompatibilityTagsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_weapon_damagepershot_items" -> {
                QOfficialWeaponDamagePerShotItems row = new QOfficialWeaponDamagePerShotItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_weapon_defaultupgrades_items" -> {
                QOfficialWeaponDefaultUpgradesItems row = new QOfficialWeaponDefaultUpgradesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_warframe_exalted_items" -> {
                QOfficialWarframeExaltedItems row = new QOfficialWarframeExaltedItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_itemreferences_items" -> {
                QOfficialWarframeItemReferencesItems row = new QOfficialWarframeItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_root" -> {
                QOfficialWeaponRoot row = new QOfficialWeaponRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_additionalitems_items" -> {
                QOfficialWeaponAdditionalItemsItems row = new QOfficialWeaponAdditionalItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_behaviours_items" -> {
                QOfficialWeaponBehavioursItems row = new QOfficialWeaponBehavioursItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_compatibilitytags_items" -> {
                QOfficialWeaponCompatibilityTagsItems row = new QOfficialWeaponCompatibilityTagsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_damagepershot_items" -> {
                QOfficialWeaponDamagePerShotItems row = new QOfficialWeaponDamagePerShotItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_defaultupgrades_items" -> {
                QOfficialWeaponDefaultUpgradesItems row = new QOfficialWeaponDefaultUpgradesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    private static <T> List<OfficialRelationRowDTO> selectRoot(JPAQueryFactory query, EntityPathBase<T> row,
            NumberPath<Long> id, Collection<Long> ids, OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(id.in(ids)).orderBy(id.asc()).fetch();
    }

    private static <T> List<OfficialRelationRowDTO> selectChildren(JPAQueryFactory query, EntityPathBase<T> row,
            NumberPath<Long> id, NumberPath<Long> parentId, NumberPath<Integer> sortOrder, Collection<Long> ids,
            OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(parentId.in(ids))
                .orderBy(parentId.asc(), sortOrder.asc(), id.asc()).fetch();
    }
}
