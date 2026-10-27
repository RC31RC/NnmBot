package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

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

/** CODEX 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialCodexDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 图片；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("image")
    private String image;

    /**
     * 记录图片是否出现，同时区分未提供与显式空值。
     *
     * @param value 图片，保留协议中的显式空值。
     */
    @JsonSetter("image")
    public void setImage(String value) {
        this.image = value;
        markPresent("image");
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

    /** 所需扫描次数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("reqScans")
    private BigDecimal reqScans;

    /**
     * 记录所需扫描次数是否出现，同时区分未提供与显式空值。
     *
     * @param value 所需扫描次数，保留协议中的显式空值。
     */
    @JsonSetter("reqScans")
    public void setReqScans(BigDecimal value) {
        this.reqScans = value;
        markPresent("reqScans");
    }

    /** 隐藏内容；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secret")
    private Boolean secret;

    /**
     * 记录隐藏内容是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏内容，保留协议中的显式空值。
     */
    @JsonSetter("secret")
    public void setSecret(Boolean value) {
        this.secret = value;
        markPresent("secret");
    }

    /** 隐藏通讯的物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretTransmission__itemReferences")
    private List<SecretTransmissionItemReferencesDTO> secretTransmissionItemReferences;

    /**
     * 记录隐藏通讯的物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏通讯的物品引用，保留协议中的显式空值。
     */
    @JsonSetter("secretTransmission__itemReferences")
    public void setSecretTransmissionItemReferences(List<SecretTransmissionItemReferencesDTO> value) {
        this.secretTransmissionItemReferences = value;
        markPresent("secretTransmission__itemReferences");
    }

    /** 隐藏通讯的声音资源路径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretTransmission__sound")
    private String secretTransmissionSound;

    /**
     * 记录隐藏通讯的声音资源路径是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏通讯的声音资源路径，保留协议中的显式空值。
     */
    @JsonSetter("secretTransmission__sound")
    public void setSecretTransmissionSound(String value) {
        this.secretTransmissionSound = value;
        markPresent("secretTransmission__sound");
    }

    /** 隐藏通讯的文本；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretTransmission__text")
    private String secretTransmissionText;

    /**
     * 记录隐藏通讯的文本是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏通讯的文本，保留协议中的显式空值。
     */
    @JsonSetter("secretTransmission__text")
    public void setSecretTransmissionText(String value) {
        this.secretTransmissionText = value;
        markPresent("secretTransmission__text");
    }

    /** 隐藏通讯的英文文本；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretTransmission__textEn")
    private String secretTransmissionTextEn;

    /**
     * 记录隐藏通讯的英文文本是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏通讯的英文文本，保留协议中的显式空值。
     */
    @JsonSetter("secretTransmission__textEn")
    public void setSecretTransmissionTextEn(String value) {
        this.secretTransmissionTextEn = value;
        markPresent("secretTransmission__textEn");
    }

    /** 隐藏通讯的中文文本；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretTransmission__textZh")
    private String secretTransmissionTextZh;

    /**
     * 记录隐藏通讯的中文文本是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏通讯的中文文本，保留协议中的显式空值。
     */
    @JsonSetter("secretTransmission__textZh")
    public void setSecretTransmissionTextZh(String value) {
        this.secretTransmissionTextZh = value;
        markPresent("secretTransmission__textZh");
    }

    /** 歌曲；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("song")
    private String song;

    /**
     * 记录歌曲是否出现，同时区分未提供与显式空值。
     *
     * @param value 歌曲，保留协议中的显式空值。
     */
    @JsonSetter("song")
    public void setSong(String value) {
        this.song = value;
        markPresent("song");
    }

    /** 战甲；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("suit")
    private String suit;

    /**
     * 记录战甲是否出现，同时区分未提供与显式空值。
     *
     * @param value 战甲，保留协议中的显式空值。
     */
    @JsonSetter("suit")
    public void setSuit(String value) {
        this.suit = value;
        markPresent("suit");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, null, OfficialInputText.first(descriptionZh, descriptionEn, description));
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

        /** 物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
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

    /** 官方嵌套对象 SecretTransmissionItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SecretTransmissionItemReferencesDTO extends OfficialObjectDTO {
        /** 隐藏通讯的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录隐藏通讯的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 隐藏通讯的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录隐藏通讯的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 隐藏通讯的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录隐藏通讯的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 隐藏通讯的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录隐藏通讯的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 隐藏通讯的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录隐藏通讯的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 隐藏通讯的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录隐藏通讯的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 隐藏通讯的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录隐藏通讯的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 隐藏通讯的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录隐藏通讯的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏通讯的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

}
