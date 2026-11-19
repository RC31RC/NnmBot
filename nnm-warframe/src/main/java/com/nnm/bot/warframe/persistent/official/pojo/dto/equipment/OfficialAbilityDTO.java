package com.nnm.bot.warframe.persistent.official.pojo.dto.equipment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** ABILITY 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialAbilityDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 技能图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("abilityIconUrl")
    private String abilityIconUrl;

    /**
     * 记录技能图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 技能图标地址，保留协议中的显式空值。
     */
    @JsonSetter("abilityIconUrl")
    public void setAbilityIconUrl(String value) {
        this.abilityIconUrl = value;
        markPresent("abilityIconUrl");
    }

    /** 技能名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("abilityName")
    private String abilityName;

    /**
     * 记录技能名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 技能名称，保留协议中的显式空值。
     */
    @JsonSetter("abilityName")
    public void setAbilityName(String value) {
        this.abilityName = value;
        markPresent("abilityName");
    }

    /** 技能中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("abilityNameZh")
    private String abilityNameZh;

    /**
     * 记录技能中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 技能中文名称，保留协议中的显式空值。
     */
    @JsonSetter("abilityNameZh")
    public void setAbilityNameZh(String value) {
        this.abilityNameZh = value;
        markPresent("abilityNameZh");
    }

    /** 技能唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("abilityUniqueName")
    private String abilityUniqueName;

    /**
     * 记录技能唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 技能唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("abilityUniqueName")
    public void setAbilityUniqueName(String value) {
        this.abilityUniqueName = value;
        markPresent("abilityUniqueName");
    }

    /** 描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("description")
    private String description;

    /**
     * 记录描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 描述，保留协议中的显式空值。
     */
    @JsonSetter("description")
    public void setDescription(String value) {
        this.description = value;
        markPresent("description");
    }

    /** 英文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionEn")
    private String descriptionEn;

    /**
     * 记录英文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionEn")
    public void setDescriptionEn(String value) {
        this.descriptionEn = value;
        markPresent("descriptionEn");
    }

    /** 中文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionZh")
    private String descriptionZh;

    /**
     * 记录中文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionZh")
    public void setDescriptionZh(String value) {
        this.descriptionZh = value;
        markPresent("descriptionZh");
    }

    /** 持续能量消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("energyConsumptionOverTime")
    private BigDecimal energyConsumptionOverTime;

    /**
     * 记录持续能量消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 持续能量消耗，保留协议中的显式空值。
     */
    @JsonSetter("energyConsumptionOverTime")
    public void setEnergyConsumptionOverTime(BigDecimal value) {
        this.energyConsumptionOverTime = value;
        markPresent("energyConsumptionOverTime");
    }

    /** 启动所需能量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("energyRequiredToActivate")
    private BigDecimal energyRequiredToActivate;

    /**
     * 记录启动所需能量是否出现，同时区分未提供与显式空值。
     *
     * @param value 启动所需能量，保留协议中的显式空值。
     */
    @JsonSetter("energyRequiredToActivate")
    public void setEnergyRequiredToActivate(BigDecimal value) {
        this.energyRequiredToActivate = value;
        markPresent("energyRequiredToActivate");
    }

    /** 图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("icon")
    private String icon;

    /**
     * 记录图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标，保留协议中的显式空值。
     */
    @JsonSetter("icon")
    public void setIcon(String value) {
        this.icon = value;
        markPresent("icon");
    }

    /** 图标资源；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconAsset")
    private String iconAsset;

    /**
     * 记录图标资源是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标资源，保留协议中的显式空值。
     */
    @JsonSetter("iconAsset")
    public void setIconAsset(String value) {
        this.iconAsset = value;
        markPresent("iconAsset");
    }

    /** 图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconStatus")
    private String iconStatus;

    /**
     * 记录图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标状态，保留协议中的显式空值。
     */
    @JsonSetter("iconStatus")
    public void setIconStatus(String value) {
        this.iconStatus = value;
        markPresent("iconStatus");
    }

    /** 图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconUrl")
    private String iconUrl;

    /**
     * 记录图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标地址，保留协议中的显式空值。
     */
    @JsonSetter("iconUrl")
    public void setIconUrl(String value) {
        this.iconUrl = value;
        markPresent("iconUrl");
    }

    /** 名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("name")
    private String name;

    /**
     * 记录名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称，保留协议中的显式空值。
     */
    @JsonSetter("name")
    public void setName(String value) {
        this.name = value;
        markPresent("name");
    }

    /** 英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameEn")
    private String nameEn;

    /**
     * 记录英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameEn")
    public void setNameEn(String value) {
        this.nameEn = value;
        markPresent("nameEn");
    }

    /** 名称本地化键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameKey")
    private String nameKey;

    /**
     * 记录名称本地化键是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称本地化键，保留协议中的显式空值。
     */
    @JsonSetter("nameKey")
    public void setNameKey(String value) {
        this.nameKey = value;
        markPresent("nameKey");
    }

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameZh")
    private String nameZh;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameZh")
    public void setNameZh(String value) {
        this.nameZh = value;
        markPresent("nameZh");
    }

    /** 所属对象业务键列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ownerBkeys")
    private List<String> ownerBkeys;

    /**
     * 记录所属对象业务键列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 所属对象业务键列表，保留协议中的显式空值。
     */
    @JsonSetter("ownerBkeys")
    public void setOwnerBkeys(List<String> value) {
        this.ownerBkeys = value;
        markPresent("ownerBkeys");
    }

    /** 记录键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("recordKey")
    private String recordKey;

    /**
     * 记录记录键是否出现，同时区分未提供与显式空值。
     *
     * @param value 记录键，保留协议中的显式空值。
     */
    @JsonSetter("recordKey")
    public void setRecordKey(String value) {
        this.recordKey = value;
        markPresent("recordKey");
    }

    /** 唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("uniqueName")
    private String uniqueName;

    /**
     * 记录唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("uniqueName")
    public void setUniqueName(String value) {
        this.uniqueName = value;
        markPresent("uniqueName");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, abilityName, name), OfficialInputText.first(nameZh, abilityNameZh), null, null, OfficialInputText.first(descriptionZh, descriptionEn, description));
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(recordKey, uniqueName);
    }

}
