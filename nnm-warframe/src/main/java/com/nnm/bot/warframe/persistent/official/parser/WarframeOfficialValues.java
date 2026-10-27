package com.nnm.bot.warframe.persistent.official.parser;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import java.math.BigDecimal;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialSyndicateDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialActivityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRelicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRewardPoolDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialKeysDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialEnemyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialCodexDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialModSetDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialFocusDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenDefinitionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialArcaneDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialUpgradeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialAvionicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.market.OfficialMarketItemDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialChallengeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialIntrinsicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialNightwaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialAchievementDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialRecipeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialDojoRecipeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialInternalDefinitionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialSkinDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialResourcesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialFusionBundlesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialSupplyDropDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialAyatanDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCreditBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialDecorationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCustomsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialComponentDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCapturaDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialMiscDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialRewardPackDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialSongDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBoosterDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialAbilityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialGearDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialRailjackWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialSentinelsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialShipDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialArchwingDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWarframeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialDronesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialAnimalDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialUpgradeLocalizationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialItemLocalizationDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialAbilityOwnerLocalizationDTO;

/** 官方固定字段投影的空值与数值转换，不解析 JSON 或按字符串读取业务字段。 */
public final class WarframeOfficialValues {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeOfficialValues() { }
    /**
     * 按调用方明确给出的字段顺序选取首个有值文本。
     *
     * @param values 待处理的值集合。
     * @return 按约定优先顺序取得的首个有效文本；均缺省时为空。
     */
    public static String first(Object... values) {
        for (Object value : values) {
            if (value instanceof String text && StrUtil.isNotBlank(text)) return text;
            if (value instanceof BigDecimal number) return number.stripTrailingZeros().toPlainString();
            if (value instanceof Boolean flag) return flag.toString();
        }
        return "";
    }
    /**
     * 优先使用本地检索字段中的名称。
     *
     * @param row 当前数据行。
     * @return 优先使用的业务名称对应的文本。
     */
    public static String name(WarframeOfficialRecordReadDTO row) {
        return first(row.localizedName(), row.officialName(), row.bkey());
    }
    /**
     * 按各具体文件的描述字段构造文本。
     *
     * @param value 待处理的字段值。
     * @return 业务对象的说明对应的文本。
     */
    public static String description(OfficialObjectDTO value) {
        if (value == null) return "";
        return switch (value) {
            case OfficialSyndicateDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialActivityDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn());
            case OfficialBountyDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialKeysDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialEnemyDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialCodexDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialModSetDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialFocusDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialUpgradeDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialChallengeDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialIntrinsicDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialNightwaveDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialAchievementDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialDojoRecipeDTO data -> first(data.getDescription());
            case OfficialSkinDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialResourcesDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialFusionBundlesDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialBundleDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialSupplyDropDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialAyatanDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialCreditBundleDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialDecorationDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialCustomsDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialComponentDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialCapturaDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialMiscDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialRewardPackDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialSongDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialBoosterDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialAbilityDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialGearDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialRailjackWeaponDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialSentinelsDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialShipDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialArchwingDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialWarframeDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialDronesDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialAnimalDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialWeaponDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialItemLocalizationDTO data -> first(data.getZhDescription(), data.getDescription());
            case OfficialAbilityOwnerLocalizationDTO data -> first(data.getZhDescription(), data.getDescription());
            case OfficialSyndicateDTO.TitlesDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialRelicDTO.StatesKeyDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialRivenDefinitionDTO.AvailableChallengesComplicationsDTO data -> first(data.getDescriptionZh(), data.getDescription());
            case OfficialRivenDefinitionDTO.AvailableChallengesDTO data -> first(data.getDescriptionZh(), data.getDescription());
            case OfficialUpgradeDTO.AvailableChallengesComplicationsDTO data -> first(data.getDescription());
            case OfficialUpgradeDTO.AvailableChallengesDTO data -> first(data.getDescription());
            case OfficialIntrinsicDTO.RanksDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialArchwingDTO.AbilitiesDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialWarframeDTO.AbilitiesDTO data -> first(data.getDescriptionZh(), data.getDescriptionEn(), data.getDescription());
            case OfficialAbilityOwnerLocalizationDTO.AbilitiesDTO data -> first(data.getZhDescription(), data.getDescription());
            default -> "";
        };
    }
    /**
     * 返回可安全遍历的可选集合。
     *
     * @param value 待校验或转换的成员集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static <T> List<T> values(List<T> value) { return value == null ? List.of() : value; }
    /**
     * 读取可选引用的指定成员，越界返回空值。
     *
     * @param value 待校验或转换的成员集合。
     * @param index 待读取的成员下标。
     * @return 目标类型对象。
     */
    public static <T> T at(List<T> value, int index) { return value == null || index < 0 || index >= value.size() ? null : value.get(index); }
    /**
     * 保留原始十进制精度。
     *
     * @param value 待校验的精确数值；缺失值保持为空。
     * @return 当前操作计算或读取的数值。
     */
    public static BigDecimal decimal(BigDecimal value) { return value; }
    /**
     * 可选整数必须精确转换，避免静默截断。
     *
     * @param value 待校验的精确数值；缺失值保持为空。
     * @return 当前操作计算或读取的数值。
     */
    public static Integer integer(BigDecimal value) { return value == null ? null : value.intValueExact(); }
    /**
     * 可选长整数必须精确转换。
     *
     * @param value 待校验的精确数值；缺失值保持为空。
     * @return 当前范围内符合条件的条目数。
     */
    public static Long count(BigDecimal value) { return value == null ? null : value.longValueExact(); }
    /**
     * 保留未提供布尔字段的空值。
     *
     * @param value 待处理的字段值。
     * @return 满足取得布尔值时为真，否则为假。
     */
    public static Boolean bool(Boolean value) { return value; }
    /**
     * 读取各文件明确声明的icon字段。
     *
     * @param value 待处理的字段值。
     * @return 图标对应的文本。
     */
    public static String icon(OfficialObjectDTO value) {
        if (value == null) return "";
        return switch (value) {
            case OfficialSyndicateDTO data -> first(data.getIcon());
            case OfficialBountyDTO data -> first(data.getIcon());
            case OfficialKeysDTO data -> first(data.getIcon());
            case OfficialEnemyDTO data -> first(data.getIcon());
            case OfficialCodexDTO data -> first(data.getIcon());
            case OfficialModSetDTO data -> first(data.getIcon());
            case OfficialFocusDTO data -> first(data.getIcon());
            case OfficialArcaneDTO data -> first(data.getIcon());
            case OfficialUpgradeDTO data -> first(data.getIcon());
            case OfficialAvionicDTO data -> first(data.getIcon());
            case OfficialMarketItemDTO data -> first(data.getIcon());
            case OfficialChallengeDTO data -> first(data.getIcon());
            case OfficialIntrinsicDTO data -> first(data.getIcon());
            case OfficialNightwaveDTO data -> first(data.getIcon());
            case OfficialAchievementDTO data -> first(data.getIcon());
            case OfficialDojoRecipeDTO data -> first(data.getIcon());
            case OfficialSkinDTO data -> first(data.getIcon());
            case OfficialResourcesDTO data -> first(data.getIcon());
            case OfficialFusionBundlesDTO data -> first(data.getIcon());
            case OfficialBundleDTO data -> first(data.getIcon());
            case OfficialSupplyDropDTO data -> first(data.getIcon());
            case OfficialAyatanDTO data -> first(data.getIcon());
            case OfficialCreditBundleDTO data -> first(data.getIcon());
            case OfficialDecorationDTO data -> first(data.getIcon());
            case OfficialCustomsDTO data -> first(data.getIcon());
            case OfficialComponentDTO data -> first(data.getIcon());
            case OfficialCapturaDTO data -> first(data.getIcon());
            case OfficialMiscDTO data -> first(data.getIcon());
            case OfficialRewardPackDTO data -> first(data.getIcon());
            case OfficialSongDTO data -> first(data.getIcon());
            case OfficialBoosterDTO data -> first(data.getIcon());
            case OfficialAbilityDTO data -> first(data.getIcon());
            case OfficialGearDTO data -> first(data.getIcon());
            case OfficialRailjackWeaponDTO data -> first(data.getIcon());
            case OfficialSentinelsDTO data -> first(data.getIcon());
            case OfficialShipDTO data -> first(data.getIcon());
            case OfficialArchwingDTO data -> first(data.getIcon());
            case OfficialWarframeDTO data -> first(data.getIcon());
            case OfficialDronesDTO data -> first(data.getIcon());
            case OfficialAnimalDTO data -> first(data.getIcon());
            case OfficialWeaponDTO data -> first(data.getIcon());
            case OfficialSyndicateDTO.TitlesDTO data -> first(data.getIcon());
            case OfficialRelicDTO.StatesKeyDTO data -> first(data.getIcon());
            case OfficialRivenWeaponDTO.MarketListingsDTO data -> first(data.getIcon());
            case OfficialModSetDTO.MembersDTO data -> first(data.getIcon());
            case OfficialArchwingDTO.AbilitiesDTO data -> first(data.getIcon());
            case OfficialWarframeDTO.AbilitiesDTO data -> first(data.getIcon());
            default -> "";
        };
    }
    /**
     * 读取各文件明确声明的iconUrl字段。
     *
     * @param value 待处理的字段值。
     * @return 图标地址对应的文本。
     */
    public static String iconUrl(OfficialObjectDTO value) {
        if (value == null) return "";
        return switch (value) {
            case OfficialSyndicateDTO data -> first(data.getIconUrl());
            case OfficialRelicDTO data -> first(data.getIconUrl());
            case OfficialKeysDTO data -> first(data.getIconUrl());
            case OfficialEnemyDTO data -> first(data.getIconUrl());
            case OfficialCodexDTO data -> first(data.getIconUrl());
            case OfficialRivenWeaponDTO data -> first(data.getIconUrl());
            case OfficialModSetDTO data -> first(data.getIconUrl());
            case OfficialFocusDTO data -> first(data.getIconUrl());
            case OfficialRivenDefinitionDTO data -> first(data.getIconUrl());
            case OfficialArcaneDTO data -> first(data.getIconUrl());
            case OfficialUpgradeDTO data -> first(data.getIconUrl());
            case OfficialAvionicDTO data -> first(data.getIconUrl());
            case OfficialMarketItemDTO data -> first(data.getIconUrl());
            case OfficialChallengeDTO data -> first(data.getIconUrl());
            case OfficialNightwaveDTO data -> first(data.getIconUrl());
            case OfficialRecipeDTO data -> first(data.getIconUrl());
            case OfficialDojoRecipeDTO data -> first(data.getIconUrl());
            case OfficialSkinDTO data -> first(data.getIconUrl());
            case OfficialResourcesDTO data -> first(data.getIconUrl());
            case OfficialFusionBundlesDTO data -> first(data.getIconUrl());
            case OfficialBundleDTO data -> first(data.getIconUrl());
            case OfficialSupplyDropDTO data -> first(data.getIconUrl());
            case OfficialAyatanDTO data -> first(data.getIconUrl());
            case OfficialDecorationDTO data -> first(data.getIconUrl());
            case OfficialCustomsDTO data -> first(data.getIconUrl());
            case OfficialComponentDTO data -> first(data.getIconUrl());
            case OfficialCapturaDTO data -> first(data.getIconUrl());
            case OfficialMiscDTO data -> first(data.getIconUrl());
            case OfficialRewardPackDTO data -> first(data.getIconUrl());
            case OfficialSongDTO data -> first(data.getIconUrl());
            case OfficialAbilityDTO data -> first(data.getIconUrl());
            case OfficialGearDTO data -> first(data.getIconUrl());
            case OfficialRailjackWeaponDTO data -> first(data.getIconUrl());
            case OfficialSentinelsDTO data -> first(data.getIconUrl());
            case OfficialShipDTO data -> first(data.getIconUrl());
            case OfficialArchwingDTO data -> first(data.getIconUrl());
            case OfficialWarframeDTO data -> first(data.getIconUrl());
            case OfficialDronesDTO data -> first(data.getIconUrl());
            case OfficialAnimalDTO data -> first(data.getIconUrl());
            case OfficialWeaponDTO data -> first(data.getIconUrl());
            case OfficialUpgradeLocalizationDTO data -> first(data.getIconUrl());
            case OfficialItemLocalizationDTO data -> first(data.getIconUrl());
            case OfficialAbilityOwnerLocalizationDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.FavoursItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.InitiationSacrificeItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.MedallionsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.TitlesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.TitlesRewardItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.TitlesSacrificeItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSyndicateDTO.TitlesDTO data -> first(data.getIconUrl());
            case OfficialDropTableDTO.GroupsRewardsItemCandidatesDTO data -> first(data.getIconUrl());
            case OfficialDropTableDTO.ResourcesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialDropTableDTO.RewardsItemCandidatesDTO data -> first(data.getIconUrl());
            case OfficialRelicDTO.MarketListingsDTO data -> first(data.getIconUrl());
            case OfficialRelicDTO.StatesKeyRewardsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRelicDTO.StatesKeyDTO data -> first(data.getIconUrl());
            case OfficialRewardPoolDTO.StagesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRegionDTO.MiscItemFeeItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRegionDTO.MissionRewardCountedItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRegionDTO.SystemsResourcesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialKeysDTO.ChainStagesMessageToSendWhenTriggeredCountedAttachmentsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialKeysDTO.MissionRewardCountedItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialKeysDTO.MissionRewardCountedStoreItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialKeysDTO.RewardsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialEnemyDTO.ItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialEnemyDTO.StagesItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialCodexDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialVendorDTO.ItemsItemPricesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialVendorDTO.ItemsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialVendorDTO.RandomItemPricesPerBinItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRivenWeaponDTO.MarketListingsDTO data -> first(data.getIconUrl());
            case OfficialModSetDTO.MembersDTO data -> first(data.getIconUrl());
            case OfficialRivenAttributeDTO.OfficialEntriesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialUpgradeDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialChallengeDTO.CountedRewardsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRecipeDTO.IngredientsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRecipeDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRecipeDTO.SecretIngredientsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialDojoRecipeDTO.IngredientsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialDojoRecipeDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialInternalDefinitionDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialResourcesDTO.HelminthSnackItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialResourcesDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialBundleDTO.ComponentsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialBundleDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSupplyDropDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialDecorationDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialCapturaDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialMiscDTO.DissectionPartsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialMiscDTO.HelminthSnackItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialMiscDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRewardPackDTO.ComponentsItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialGearDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRailjackWeaponDTO.DefaultUpgradesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialRailjackWeaponDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSentinelsDTO.DefaultUpgradesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialSentinelsDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialArchwingDTO.AbilitiesDTO data -> first(data.getIconUrl());
            case OfficialArchwingDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialWarframeDTO.AbilitiesDTO data -> first(data.getIconUrl());
            case OfficialAnimalDTO.ConservationItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialWeaponDTO.DefaultUpgradesItemReferencesDTO data -> first(data.getIconUrl());
            case OfficialWeaponDTO.ItemReferencesDTO data -> first(data.getIconUrl());
            default -> "";
        };
    }
}
