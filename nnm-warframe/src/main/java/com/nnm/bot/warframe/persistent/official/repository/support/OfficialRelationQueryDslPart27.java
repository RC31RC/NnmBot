package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyAvatarTypesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyBehavioursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyHitProxiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyHitProxiesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyItemsItemsItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 27；由模型生成器维护。 */
final class OfficialRelationQueryDslPart27 {
    private OfficialRelationQueryDslPart27() { }

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
            case "wf_official_enemy_root" -> {
                QOfficialEnemyRoot row = new QOfficialEnemyRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "armorState", "armor", "avatarTypesState", "baseLevelState", "baseLevel", "behavioursState", "codexScansRequiredState", "codexScansRequired", "codexSecretState", "codexSecret", "criticalChanceState", "criticalChance", "criticalMultiplierState", "criticalMultiplier", "damageAttenState", "damageAtten", "damageControllerState", "damageController", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "droptableState", "droptable", "excludeFromSimulacrumState", "excludeFromSimulacrum", "factionState", "faction", "factionResistanceKeywordState", "factionResistanceKeyword", "healthState", "health", "hitProxiesState", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "isFrivolousState", "isFrivolous", "itemReferencesState", "itemsState", "killXPRewardState", "killXPReward", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "poolKeyState", "poolKey", "shieldState", "shield", "stagesState", "unhandledProcTypesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.armorState, row.armor, row.avatarTypesState, row.baseLevelState, row.baseLevel, row.behavioursState, row.codexScansRequiredState, row.codexScansRequired, row.codexSecretState, row.codexSecret, row.criticalChanceState, row.criticalChance, row.criticalMultiplierState, row.criticalMultiplier, row.damageAttenState, row.damageAtten, row.damageControllerState, row.damageController, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.droptableState, row.droptable, row.excludeFromSimulacrumState, row.excludeFromSimulacrum, row.factionState, row.faction, row.factionResistanceKeywordState, row.factionResistanceKeyword, row.healthState, row.health, row.hitProxiesState, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.isFrivolousState, row.isFrivolous, row.itemReferencesState, row.itemsState, row.killXPRewardState, row.killXPReward, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.poolKeyState, row.poolKey, row.shieldState, row.shield, row.stagesState, row.unhandledProcTypesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_enemy_avatartypes_items" -> {
                QOfficialEnemyAvatarTypesItems row = new QOfficialEnemyAvatarTypesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "referenceDomainState", "referenceDomain", "referenceFieldState", "referenceField", "referenceIconStatusState", "referenceIconStatus", "referenceNameState", "referenceName", "referenceNameEnState", "referenceNameEn", "referenceNameZhState", "referenceNameZh", "referenceReferenceKindState", "referenceReferenceKind", "referenceResolutionState", "referenceResolution", "referenceUniqueNameState", "referenceUniqueName", "uniqueNameState", "uniqueName", "variantState", "variant"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.referenceDomainState, row.referenceDomain, row.referenceFieldState, row.referenceField, row.referenceIconStatusState, row.referenceIconStatus, row.referenceNameState, row.referenceName, row.referenceNameEnState, row.referenceNameEn, row.referenceNameZhState, row.referenceNameZh, row.referenceReferenceKindState, row.referenceReferenceKind, row.referenceResolutionState, row.referenceResolution, row.referenceUniqueNameState, row.referenceUniqueName, row.uniqueNameState, row.uniqueName, row.variantState, row.variant));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_behaviours_items" -> {
                QOfficialEnemyBehavioursItems row = new QOfficialEnemyBehavioursItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "burstCountState", "burstCount", "burstDelayState", "burstDelay", "chargedProjectileAttackState", "chargedProjectileAttackDtElectricityState", "chargedProjectileAttackDtElectricity", "chargedProjectileAttackDtFireState", "chargedProjectileAttackDtFire", "chargedProjectileAttackDtFreezeState", "chargedProjectileAttackDtFreeze", "chargedProjectileAttackDtHealthDrainState", "chargedProjectileAttackDtHealthDrain", "chargedProjectileAttackDtImpactState", "chargedProjectileAttackDtImpact", "chargedProjectileAttackDtMagneticState", "chargedProjectileAttackDtMagnetic", "chargedProjectileAttackDtPunctureState", "chargedProjectileAttackDtPuncture", "chargedProjectileAttackDtSentientState", "chargedProjectileAttackDtSentient", "chargedProjectileAttackDtSlashState", "chargedProjectileAttackDtSlash", "chargedProjectileAttackProcChanceState", "chargedProjectileAttackProcChance", "chargedProjectileEmbedDeathAttackDtElectricityState", "chargedProjectileEmbedDeathAttackDtElectricity", "chargedProjectileEmbedDeathAttackDtExplosionState", "chargedProjectileEmbedDeathAttackDtExplosion", "chargedProjectileEmbedDeathAttackProcChanceState", "chargedProjectileEmbedDeathAttackProcChance", "chargedProjectileExplosiveAttackDtExplosionState", "chargedProjectileExplosiveAttackDtExplosion", "chargedProjectileExplosiveAttackDtFireState", "chargedProjectileExplosiveAttackDtFire", "chargedProjectileExplosiveAttackDtImpactState", "chargedProjectileExplosiveAttackDtImpact", "chargedProjectileExplosiveAttackDtMagneticState", "chargedProjectileExplosiveAttackDtMagnetic", "chargedProjectileExplosiveAttackDtSentientState", "chargedProjectileExplosiveAttackDtSentient", "chargedProjectileExplosiveAttackProcChanceState", "chargedProjectileExplosiveAttackProcChance", "fireIterationsState", "fireIterations", "impactDtCorrosiveState", "impactDtCorrosive", "impactDtElectricityState", "impactDtElectricity", "impactDtExplosionState", "impactDtExplosion", "impactDtFireState", "impactDtFire", "impactDtFreezeState", "impactDtFreeze", "impactDtGasState", "impactDtGas", "impactDtImpactState", "impactDtImpact", "impactDtMagneticState", "impactDtMagnetic", "impactDtPoisonState", "impactDtPoison", "impactDtPunctureState", "impactDtPuncture", "impactDtRadiationState", "impactDtRadiation", "impactDtSentientState", "impactDtSentient", "impactDtSlashState", "impactDtSlash", "impactDtViralState", "impactDtViral", "impactProcChanceState", "impactProcChance", "projectileAttackState", "projectileAttackDtCorrosiveState", "projectileAttackDtCorrosive", "projectileAttackDtElectricityState", "projectileAttackDtElectricity", "projectileAttackDtExplosionState", "projectileAttackDtExplosion", "projectileAttackDtFireState", "projectileAttackDtFire", "projectileAttackDtFreezeState", "projectileAttackDtFreeze", "projectileAttackDtGasState", "projectileAttackDtGas", "projectileAttackDtHealthDrainState", "projectileAttackDtHealthDrain", "projectileAttackDtImpactState", "projectileAttackDtImpact", "projectileAttackDtMagneticState", "projectileAttackDtMagnetic", "projectileAttackDtPoisonState", "projectileAttackDtPoison", "projectileAttackDtPunctureState", "projectileAttackDtPuncture", "projectileAttackDtRadiationState", "projectileAttackDtRadiation", "projectileAttackDtSentientState", "projectileAttackDtSentient", "projectileAttackDtSlashState", "projectileAttackDtSlash", "projectileAttackDtViralState", "projectileAttackDtViral", "projectileAttackProcChanceState", "projectileAttackProcChance", "projectileEmbedDeathAttackDtCorrosiveState", "projectileEmbedDeathAttackDtCorrosive", "projectileEmbedDeathAttackDtElectricityState", "projectileEmbedDeathAttackDtElectricity", "projectileEmbedDeathAttackDtExplosionState", "projectileEmbedDeathAttackDtExplosion", "projectileEmbedDeathAttackDtFireState", "projectileEmbedDeathAttackDtFire", "projectileEmbedDeathAttackDtFreezeState", "projectileEmbedDeathAttackDtFreeze", "projectileEmbedDeathAttackDtGasState", "projectileEmbedDeathAttackDtGas", "projectileEmbedDeathAttackDtImpactState", "projectileEmbedDeathAttackDtImpact", "projectileEmbedDeathAttackProcChanceState", "projectileEmbedDeathAttackProcChance", "projectileExplosiveAttackDtCorrosiveState", "projectileExplosiveAttackDtCorrosive", "projectileExplosiveAttackDtElectricityState", "projectileExplosiveAttackDtElectricity", "projectileExplosiveAttackDtExplosionState", "projectileExplosiveAttackDtExplosion", "projectileExplosiveAttackDtFireState", "projectileExplosiveAttackDtFire", "projectileExplosiveAttackDtFreezeState", "projectileExplosiveAttackDtFreeze", "projectileExplosiveAttackDtGasState", "projectileExplosiveAttackDtGas", "projectileExplosiveAttackDtImpactState", "projectileExplosiveAttackDtImpact", "projectileExplosiveAttackDtMagneticState", "projectileExplosiveAttackDtMagnetic", "projectileExplosiveAttackDtPoisonState", "projectileExplosiveAttackDtPoison", "projectileExplosiveAttackDtPunctureState", "projectileExplosiveAttackDtPuncture", "projectileExplosiveAttackDtRadiationState", "projectileExplosiveAttackDtRadiation", "projectileExplosiveAttackDtSentientState", "projectileExplosiveAttackDtSentient", "projectileExplosiveAttackDtShieldDrainState", "projectileExplosiveAttackDtShieldDrain", "projectileExplosiveAttackDtSlashState", "projectileExplosiveAttackDtSlash", "projectileExplosiveAttackDtViralState", "projectileExplosiveAttackDtViral", "projectileExplosiveAttackProcChanceState", "projectileExplosiveAttackProcChance", "stateNameState", "stateName", "stateNameEnState", "stateNameEn", "stateNameZhState", "stateNameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.burstCountState, row.burstCount, row.burstDelayState, row.burstDelay, row.chargedProjectileAttackState, row.chargedProjectileAttackDtElectricityState, row.chargedProjectileAttackDtElectricity, row.chargedProjectileAttackDtFireState, row.chargedProjectileAttackDtFire, row.chargedProjectileAttackDtFreezeState, row.chargedProjectileAttackDtFreeze, row.chargedProjectileAttackDtHealthDrainState, row.chargedProjectileAttackDtHealthDrain, row.chargedProjectileAttackDtImpactState, row.chargedProjectileAttackDtImpact, row.chargedProjectileAttackDtMagneticState, row.chargedProjectileAttackDtMagnetic, row.chargedProjectileAttackDtPunctureState, row.chargedProjectileAttackDtPuncture, row.chargedProjectileAttackDtSentientState, row.chargedProjectileAttackDtSentient, row.chargedProjectileAttackDtSlashState, row.chargedProjectileAttackDtSlash, row.chargedProjectileAttackProcChanceState, row.chargedProjectileAttackProcChance, row.chargedProjectileEmbedDeathAttackDtElectricityState, row.chargedProjectileEmbedDeathAttackDtElectricity, row.chargedProjectileEmbedDeathAttackDtExplosionState, row.chargedProjectileEmbedDeathAttackDtExplosion, row.chargedProjectileEmbedDeathAttackProcChanceState, row.chargedProjectileEmbedDeathAttackProcChance, row.chargedProjectileExplosiveAttackDtExplosionState, row.chargedProjectileExplosiveAttackDtExplosion, row.chargedProjectileExplosiveAttackDtFireState, row.chargedProjectileExplosiveAttackDtFire, row.chargedProjectileExplosiveAttackDtImpactState, row.chargedProjectileExplosiveAttackDtImpact, row.chargedProjectileExplosiveAttackDtMagneticState, row.chargedProjectileExplosiveAttackDtMagnetic, row.chargedProjectileExplosiveAttackDtSentientState, row.chargedProjectileExplosiveAttackDtSentient, row.chargedProjectileExplosiveAttackProcChanceState, row.chargedProjectileExplosiveAttackProcChance, row.fireIterationsState, row.fireIterations, row.impactDtCorrosiveState, row.impactDtCorrosive, row.impactDtElectricityState, row.impactDtElectricity, row.impactDtExplosionState, row.impactDtExplosion, row.impactDtFireState, row.impactDtFire, row.impactDtFreezeState, row.impactDtFreeze, row.impactDtGasState, row.impactDtGas, row.impactDtImpactState, row.impactDtImpact, row.impactDtMagneticState, row.impactDtMagnetic, row.impactDtPoisonState, row.impactDtPoison, row.impactDtPunctureState, row.impactDtPuncture, row.impactDtRadiationState, row.impactDtRadiation, row.impactDtSentientState, row.impactDtSentient, row.impactDtSlashState, row.impactDtSlash, row.impactDtViralState, row.impactDtViral, row.impactProcChanceState, row.impactProcChance, row.projectileAttackState, row.projectileAttackDtCorrosiveState, row.projectileAttackDtCorrosive, row.projectileAttackDtElectricityState, row.projectileAttackDtElectricity, row.projectileAttackDtExplosionState, row.projectileAttackDtExplosion, row.projectileAttackDtFireState, row.projectileAttackDtFire, row.projectileAttackDtFreezeState, row.projectileAttackDtFreeze, row.projectileAttackDtGasState, row.projectileAttackDtGas, row.projectileAttackDtHealthDrainState, row.projectileAttackDtHealthDrain, row.projectileAttackDtImpactState, row.projectileAttackDtImpact, row.projectileAttackDtMagneticState, row.projectileAttackDtMagnetic, row.projectileAttackDtPoisonState, row.projectileAttackDtPoison, row.projectileAttackDtPunctureState, row.projectileAttackDtPuncture, row.projectileAttackDtRadiationState, row.projectileAttackDtRadiation, row.projectileAttackDtSentientState, row.projectileAttackDtSentient, row.projectileAttackDtSlashState, row.projectileAttackDtSlash, row.projectileAttackDtViralState, row.projectileAttackDtViral, row.projectileAttackProcChanceState, row.projectileAttackProcChance, row.projectileEmbedDeathAttackDtCorrosiveState, row.projectileEmbedDeathAttackDtCorrosive, row.projectileEmbedDeathAttackDtElectricityState, row.projectileEmbedDeathAttackDtElectricity, row.projectileEmbedDeathAttackDtExplosionState, row.projectileEmbedDeathAttackDtExplosion, row.projectileEmbedDeathAttackDtFireState, row.projectileEmbedDeathAttackDtFire, row.projectileEmbedDeathAttackDtFreezeState, row.projectileEmbedDeathAttackDtFreeze, row.projectileEmbedDeathAttackDtGasState, row.projectileEmbedDeathAttackDtGas, row.projectileEmbedDeathAttackDtImpactState, row.projectileEmbedDeathAttackDtImpact, row.projectileEmbedDeathAttackProcChanceState, row.projectileEmbedDeathAttackProcChance, row.projectileExplosiveAttackDtCorrosiveState, row.projectileExplosiveAttackDtCorrosive, row.projectileExplosiveAttackDtElectricityState, row.projectileExplosiveAttackDtElectricity, row.projectileExplosiveAttackDtExplosionState, row.projectileExplosiveAttackDtExplosion, row.projectileExplosiveAttackDtFireState, row.projectileExplosiveAttackDtFire, row.projectileExplosiveAttackDtFreezeState, row.projectileExplosiveAttackDtFreeze, row.projectileExplosiveAttackDtGasState, row.projectileExplosiveAttackDtGas, row.projectileExplosiveAttackDtImpactState, row.projectileExplosiveAttackDtImpact, row.projectileExplosiveAttackDtMagneticState, row.projectileExplosiveAttackDtMagnetic, row.projectileExplosiveAttackDtPoisonState, row.projectileExplosiveAttackDtPoison, row.projectileExplosiveAttackDtPunctureState, row.projectileExplosiveAttackDtPuncture, row.projectileExplosiveAttackDtRadiationState, row.projectileExplosiveAttackDtRadiation, row.projectileExplosiveAttackDtSentientState, row.projectileExplosiveAttackDtSentient, row.projectileExplosiveAttackDtShieldDrainState, row.projectileExplosiveAttackDtShieldDrain, row.projectileExplosiveAttackDtSlashState, row.projectileExplosiveAttackDtSlash, row.projectileExplosiveAttackDtViralState, row.projectileExplosiveAttackDtViral, row.projectileExplosiveAttackProcChanceState, row.projectileExplosiveAttackProcChance, row.stateNameState, row.stateName, row.stateNameEnState, row.stateNameEn, row.stateNameZhState, row.stateNameZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_hitproxies_items" -> {
                QOfficialEnemyHitProxiesItems row = new QOfficialEnemyHitProxiesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "boneState", "bone", "itemReferencesState", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.boneState, row.bone, row.itemReferencesState, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_hitproxies_items_itemreferences_items" -> {
                QOfficialEnemyHitProxiesItemsItemReferencesItems row = new QOfficialEnemyHitProxiesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_itemreferences_items" -> {
                QOfficialEnemyItemReferencesItems row = new QOfficialEnemyItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_items_items" -> {
                QOfficialEnemyItemsItems row = new QOfficialEnemyItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_items_items_itemreferences_items" -> {
                QOfficialEnemyItemsItemsItemReferencesItems row = new QOfficialEnemyItemsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
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
            case "wf_official_enemy_avatartypes_items" -> {
                QOfficialEnemyAvatarTypesItems row = new QOfficialEnemyAvatarTypesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_behaviours_items" -> {
                QOfficialEnemyBehavioursItems row = new QOfficialEnemyBehavioursItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_hitproxies_items" -> {
                QOfficialEnemyHitProxiesItems row = new QOfficialEnemyHitProxiesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_hitproxies_items_itemreferences_items" -> {
                QOfficialEnemyHitProxiesItemsItemReferencesItems row = new QOfficialEnemyHitProxiesItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_itemreferences_items" -> {
                QOfficialEnemyItemReferencesItems row = new QOfficialEnemyItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_items_items" -> {
                QOfficialEnemyItemsItems row = new QOfficialEnemyItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_items_items_itemreferences_items" -> {
                QOfficialEnemyItemsItemsItemReferencesItems row = new QOfficialEnemyItemsItemsItemReferencesItems(ROW);
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
            case "wf_official_enemy_root" -> {
                QOfficialEnemyRoot row = new QOfficialEnemyRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_avatartypes_items" -> {
                QOfficialEnemyAvatarTypesItems row = new QOfficialEnemyAvatarTypesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_behaviours_items" -> {
                QOfficialEnemyBehavioursItems row = new QOfficialEnemyBehavioursItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_hitproxies_items" -> {
                QOfficialEnemyHitProxiesItems row = new QOfficialEnemyHitProxiesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_hitproxies_items_itemreferences_items" -> {
                QOfficialEnemyHitProxiesItemsItemReferencesItems row = new QOfficialEnemyHitProxiesItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_itemreferences_items" -> {
                QOfficialEnemyItemReferencesItems row = new QOfficialEnemyItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_items_items" -> {
                QOfficialEnemyItemsItems row = new QOfficialEnemyItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_items_items_itemreferences_items" -> {
                QOfficialEnemyItemsItemsItemReferencesItems row = new QOfficialEnemyItemsItemsItemReferencesItems(ROW);
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
