package com.nnm.bot.warframe.persistent.official.parser.input;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialDojoRecipeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialRecipeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialAbilityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialAnimalDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialArchwingDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialDronesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialGearDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialRailjackWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialSentinelsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialShipDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWarframeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialAyatanDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBoosterDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCapturaDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialComponentDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCreditBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCustomsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialDecorationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialFusionBundlesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialInternalDefinitionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialMiscDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialResourcesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialRewardPackDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialSkinDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialSongDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialSupplyDropDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.market.OfficialMarketItemDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialAchievementDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialChallengeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialIntrinsicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialNightwaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialAbilityOwnerLocalizationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialImageDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialItemLocalizationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialLanguageStringDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialTextIconDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialUpgradeLocalizationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRelicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRewardPoolDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.statistics.OfficialUsage2020DTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.statistics.OfficialUsage2021DTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.statistics.OfficialUsage2022DTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.statistics.OfficialUsage2023DTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.statistics.OfficialUsage2024DTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.statistics.OfficialUsage2025DTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialArcaneDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialAvionicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialFocusDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialModSetDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenDefinitionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialUpgradeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialCodexDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialEnemyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialInboxDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialKeysDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialTilesetDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialActivityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialBossDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialFactionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialFissureModifierDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialMissionTypeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialSyndicateDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialUpgradeTypeDTO;

/** 每个官方数据 JSON 的显式输入类型；由生成器和契约测试共同维护。 */
public final class OfficialInputTypes {
    /**
     * 禁止实例化逐文件输入类型登记表。
     */
    private OfficialInputTypes() {
    }

    /**
     * 取得官方 JSON 所属的具体输入类型。
     *
     * @param domain 已经确认的官方文件领域。
     * @return 该文件独立的 DTO 类型；未登记领域直接失败。
     */
    public static Class<? extends OfficialRecordDTO> type(WarframeOfficialDataDomain domain) {
        return switch (domain) {
            case DOJO_RECIPE -> OfficialDojoRecipeDTO.class;
            case RECIPE -> OfficialRecipeDTO.class;
            case ABILITY -> OfficialAbilityDTO.class;
            case ARCHWING -> OfficialArchwingDTO.class;
            case ANIMAL -> OfficialAnimalDTO.class;
            case SENTINELS -> OfficialSentinelsDTO.class;
            case GEAR -> OfficialGearDTO.class;
            case RAILJACK_WEAPON -> OfficialRailjackWeaponDTO.class;
            case DRONES -> OfficialDronesDTO.class;
            case SHIP -> OfficialShipDTO.class;
            case WARFRAME -> OfficialWarframeDTO.class;
            case WEAPON -> OfficialWeaponDTO.class;
            case AYATAN -> OfficialAyatanDTO.class;
            case BOOSTER -> OfficialBoosterDTO.class;
            case BUNDLE -> OfficialBundleDTO.class;
            case CAPTURA -> OfficialCapturaDTO.class;
            case COMPONENT -> OfficialComponentDTO.class;
            case CREDIT_BUNDLE -> OfficialCreditBundleDTO.class;
            case CUSTOMS -> OfficialCustomsDTO.class;
            case DECORATION -> OfficialDecorationDTO.class;
            case FUSION_BUNDLES -> OfficialFusionBundlesDTO.class;
            case INTERNAL_DEFINITION -> OfficialInternalDefinitionDTO.class;
            case MISC -> OfficialMiscDTO.class;
            case RESOURCES -> OfficialResourcesDTO.class;
            case REWARD_PACK -> OfficialRewardPackDTO.class;
            case SKIN -> OfficialSkinDTO.class;
            case SONG -> OfficialSongDTO.class;
            case SUPPLY_DROP -> OfficialSupplyDropDTO.class;
            case MARKET_ITEM -> OfficialMarketItemDTO.class;
            case ACHIEVEMENT -> OfficialAchievementDTO.class;
            case CHALLENGE -> OfficialChallengeDTO.class;
            case INTRINSIC -> OfficialIntrinsicDTO.class;
            case NIGHTWAVE -> OfficialNightwaveDTO.class;
            case ABILITY_OWNER_LOCALIZATION -> OfficialAbilityOwnerLocalizationDTO.class;
            case IMAGE -> OfficialImageDTO.class;
            case ITEM_LOCALIZATION -> OfficialItemLocalizationDTO.class;
            case LANGUAGE_STRING -> OfficialLanguageStringDTO.class;
            case TEXT_ICON -> OfficialTextIconDTO.class;
            case UPGRADE_LOCALIZATION -> OfficialUpgradeLocalizationDTO.class;
            case BOUNTY -> OfficialBountyDTO.class;
            case DROP_TABLE -> OfficialDropTableDTO.class;
            case RELIC -> OfficialRelicDTO.class;
            case REWARD_POOL -> OfficialRewardPoolDTO.class;
            case USAGE_2020 -> OfficialUsage2020DTO.class;
            case USAGE_2021 -> OfficialUsage2021DTO.class;
            case USAGE_2022 -> OfficialUsage2022DTO.class;
            case USAGE_2023 -> OfficialUsage2023DTO.class;
            case USAGE_2024 -> OfficialUsage2024DTO.class;
            case USAGE_2025 -> OfficialUsage2025DTO.class;
            case ARCANE -> OfficialArcaneDTO.class;
            case AVIONIC -> OfficialAvionicDTO.class;
            case FOCUS -> OfficialFocusDTO.class;
            case MOD_SET -> OfficialModSetDTO.class;
            case UPGRADE -> OfficialUpgradeDTO.class;
            case RIVEN_ATTRIBUTE -> OfficialRivenAttributeDTO.class;
            case RIVEN_DEFINITION -> OfficialRivenDefinitionDTO.class;
            case RIVEN_WEAPON -> OfficialRivenWeaponDTO.class;
            case CODEX -> OfficialCodexDTO.class;
            case ENEMY -> OfficialEnemyDTO.class;
            case INBOX -> OfficialInboxDTO.class;
            case KEYS -> OfficialKeysDTO.class;
            case REGION -> OfficialRegionDTO.class;
            case TILESET -> OfficialTilesetDTO.class;
            case VENDOR -> OfficialVendorDTO.class;
            case ACTIVITY -> OfficialActivityDTO.class;
            case BOSS -> OfficialBossDTO.class;
            case FACTION -> OfficialFactionDTO.class;
            case FISSURE_MODIFIER -> OfficialFissureModifierDTO.class;
            case MISSION_TYPE -> OfficialMissionTypeDTO.class;
            case SYNDICATE -> OfficialSyndicateDTO.class;
            case UPGRADE_TYPE -> OfficialUpgradeTypeDTO.class;
            case OTHER -> throw new IllegalArgumentException("Unknown official dataset");
        };
    }
}
