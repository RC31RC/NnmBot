package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.util.List;
import lombok.Getter;

/** TEXT_ICON 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialTextIconDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 通用平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_AGNOSTIC")
    private String ditAgnostic;

    /**
     * 记录通用平台是否出现，同时区分未提供与显式空值。
     *
     * @param value 通用平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_AGNOSTIC")
    public void setDitAgnostic(String value) {
        this.ditAgnostic = value;
        markPresent("DIT_AGNOSTIC");
    }

    /** 安卓平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_ANDROID")
    private String ditAndroid;

    /**
     * 记录安卓平台是否出现，同时区分未提供与显式空值。
     *
     * @param value 安卓平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_ANDROID")
    public void setDitAndroid(String value) {
        this.ditAndroid = value;
        markPresent("DIT_ANDROID");
    }

    /** 自动平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_AUTO")
    private String ditAuto;

    /**
     * 记录自动平台是否出现，同时区分未提供与显式空值。
     *
     * @param value 自动平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_AUTO")
    public void setDitAuto(String value) {
        this.ditAuto = value;
        markPresent("DIT_AUTO");
    }

    /** iOS 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_IOS")
    private String ditIos;

    /**
     * 记录iOS 平台是否出现，同时区分未提供与显式空值。
     *
     * @param value iOS 平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_IOS")
    public void setDitIos(String value) {
        this.ditIos = value;
        markPresent("DIT_IOS");
    }

    /** 电脑平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_PC")
    private String ditPc;

    /**
     * 记录电脑平台是否出现，同时区分未提供与显式空值。
     *
     * @param value 电脑平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_PC")
    public void setDitPc(String value) {
        this.ditPc = value;
        markPresent("DIT_PC");
    }

    /** PlayStation 4 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_PS4")
    private String ditPs4;

    /**
     * 记录PlayStation 4 平台是否出现，同时区分未提供与显式空值。
     *
     * @param value PlayStation 4 平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_PS4")
    public void setDitPs4(String value) {
        this.ditPs4 = value;
        markPresent("DIT_PS4");
    }

    /** PlayStation 5 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_PS5")
    private String ditPs5;

    /**
     * 记录PlayStation 5 平台是否出现，同时区分未提供与显式空值。
     *
     * @param value PlayStation 5 平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_PS5")
    public void setDitPs5(String value) {
        this.ditPs5 = value;
        markPresent("DIT_PS5");
    }

    /** Steam 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_STEAM")
    private String ditSteam;

    /**
     * 记录Steam 平台是否出现，同时区分未提供与显式空值。
     *
     * @param value Steam 平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_STEAM")
    public void setDitSteam(String value) {
        this.ditSteam = value;
        markPresent("DIT_STEAM");
    }

    /** Switch 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_SWITCH")
    private String ditSwitch;

    /**
     * 记录Switch 平台是否出现，同时区分未提供与显式空值。
     *
     * @param value Switch 平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_SWITCH")
    public void setDitSwitch(String value) {
        this.ditSwitch = value;
        markPresent("DIT_SWITCH");
    }

    /** Xbox 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("DIT_XBONE")
    private String ditXbone;

    /**
     * 记录Xbox 平台是否出现，同时区分未提供与显式空值。
     *
     * @param value Xbox 平台，保留协议中的显式空值。
     */
    @JsonSetter("DIT_XBONE")
    public void setDitXbone(String value) {
        this.ditXbone = value;
        markPresent("DIT_XBONE");
    }

    /** 物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("itemReferences")
    private List<ItemReferencesDTO> itemReferences;

    /**
     * 记录物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品引用，保留协议中的显式空值。
     */
    @JsonSetter("itemReferences")
    public void setItemReferences(List<ItemReferencesDTO> value) {
        this.itemReferences = value;
        markPresent("itemReferences");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(null, null, null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return null;
    }

    /** 官方嵌套对象 ItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemReferencesDTO extends OfficialObjectDTO {
        /** 物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

}
