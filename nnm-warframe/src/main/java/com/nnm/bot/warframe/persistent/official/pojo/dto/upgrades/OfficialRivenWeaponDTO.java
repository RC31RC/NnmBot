package com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialNullDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** RIVEN_WEAPON 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRivenWeaponDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 倾向值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("disposition")
    private BigDecimal disposition;

    /**
     * 记录倾向值是否出现，同时区分未提供与显式空值。
     *
     * @param value 倾向值，保留协议中的显式空值。
     */
    @JsonSetter("disposition")
    public void setDisposition(BigDecimal value) {
        this.disposition = value;
        markPresent("disposition");
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

    /** 关联市场商品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("marketListings")
    private List<MarketListingsDTO> marketListings;

    /**
     * 记录关联市场商品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 关联市场商品列表，保留协议中的显式空值。
     */
    @JsonSetter("marketListings")
    public void setMarketListings(List<MarketListingsDTO> value) {
        this.marketListings = value;
        markPresent("marketListings");
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

    /** 数据质量统计的已收录倾向值的数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__dispositionCount")
    private BigDecimal qualityDispositionCount;

    /**
     * 记录数据质量统计的已收录倾向值的数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的已收录倾向值的数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__dispositionCount")
    public void setQualityDispositionCount(BigDecimal value) {
        this.qualityDispositionCount = value;
        markPresent("quality__dispositionCount");
    }

    /** 数据质量统计的市场商品数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__marketListingCount")
    private BigDecimal qualityMarketListingCount;

    /**
     * 记录数据质量统计的市场商品数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的市场商品数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__marketListingCount")
    public void setQualityMarketListingCount(BigDecimal value) {
        this.qualityMarketListingCount = value;
        markPresent("quality__marketListingCount");
    }

    /** 数据质量统计的已关联的市场商品数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__marketListingsAttached")
    private BigDecimal qualityMarketListingsAttached;

    /**
     * 记录数据质量统计的已关联的市场商品数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的已关联的市场商品数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__marketListingsAttached")
    public void setQualityMarketListingsAttached(BigDecimal value) {
        this.qualityMarketListingsAttached = value;
        markPresent("quality__marketListingsAttached");
    }

    /** 数据质量统计的包含倾向值的市场商品数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__marketListingsWithDisposition")
    private BigDecimal qualityMarketListingsWithDisposition;

    /**
     * 记录数据质量统计的包含倾向值的市场商品数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的包含倾向值的市场商品数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__marketListingsWithDisposition")
    public void setQualityMarketListingsWithDisposition(BigDecimal value) {
        this.qualityMarketListingsWithDisposition = value;
        markPresent("quality__marketListingsWithDisposition");
    }

    /** 数据质量统计的缺少倾向值的市场商品数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__marketListingsWithoutDisposition")
    private BigDecimal qualityMarketListingsWithoutDisposition;

    /**
     * 记录数据质量统计的缺少倾向值的市场商品数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的缺少倾向值的市场商品数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__marketListingsWithoutDisposition")
    public void setQualityMarketListingsWithoutDisposition(BigDecimal value) {
        this.qualityMarketListingsWithoutDisposition = value;
        markPresent("quality__marketListingsWithoutDisposition");
    }

    /** 数据质量统计的未解析的市场武器列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__unresolvedMarketWeapons")
    private BigDecimal qualityUnresolvedMarketWeapons;

    /**
     * 记录数据质量统计的未解析的市场武器列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的未解析的市场武器列表，保留协议中的显式空值。
     */
    @JsonSetter("quality__unresolvedMarketWeapons")
    public void setQualityUnresolvedMarketWeapons(BigDecimal value) {
        this.qualityUnresolvedMarketWeapons = value;
        markPresent("quality__unresolvedMarketWeapons");
    }

    /** 数据质量统计的武器数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__weaponCount")
    private BigDecimal qualityWeaponCount;

    /**
     * 记录数据质量统计的武器数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的武器数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__weaponCount")
    public void setQualityWeaponCount(BigDecimal value) {
        this.qualityWeaponCount = value;
        markPresent("quality__weaponCount");
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

    /** 未解析的市场武器列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("unresolvedMarketWeapons")
    @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
    private List<Void> unresolvedMarketWeapons;

    /**
     * 记录未解析的市场武器列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 未解析的市场武器列表，保留协议中的显式空值。
     */
    @JsonSetter("unresolvedMarketWeapons")
    public void setUnresolvedMarketWeapons(List<Void> value) {
        this.unresolvedMarketWeapons = value;
        markPresent("unresolvedMarketWeapons");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(uniqueName);
    }

    /** 官方嵌套对象 MarketListingsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MarketListingsDTO extends OfficialObjectDTO {
        /** 关联市场商品列表的倾向值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("disposition")
        private BigDecimal disposition;

        /**
         * 记录关联市场商品列表的倾向值是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的倾向值，保留协议中的显式空值。
         */
        @JsonSetter("disposition")
        public void setDisposition(BigDecimal value) {
            this.disposition = value;
            markPresent("disposition");
        }

        /** 关联市场商品列表的游戏引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("gameRef")
        private String gameRef;

        /**
         * 记录关联市场商品列表的游戏引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的游戏引用，保留协议中的显式空值。
         */
        @JsonSetter("gameRef")
        public void setGameRef(String value) {
            this.gameRef = value;
            markPresent("gameRef");
        }

        /** 关联市场商品列表的组；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("group")
        private String group;

        /**
         * 记录关联市场商品列表的组是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的组，保留协议中的显式空值。
         */
        @JsonSetter("group")
        public void setGroup(String value) {
            this.group = value;
            markPresent("group");
        }

        /** 关联市场商品列表的图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("icon")
        private String icon;

        /**
         * 记录关联市场商品列表的图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的图标，保留协议中的显式空值。
         */
        @JsonSetter("icon")
        public void setIcon(String value) {
            this.icon = value;
            markPresent("icon");
        }

        /** 关联市场商品列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录关联市场商品列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 关联市场商品列表的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录关联市场商品列表的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 关联市场商品列表的标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("id")
        private String id;

        /**
         * 记录关联市场商品列表的标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的标识，保留协议中的显式空值。
         */
        @JsonSetter("id")
        public void setId(String value) {
            this.id = value;
            markPresent("id");
        }

        /** 关联市场商品列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录关联市场商品列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 关联市场商品列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录关联市场商品列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 关联市场商品列表的记录键；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("recordKey")
        private String recordKey;

        /**
         * 记录关联市场商品列表的记录键是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的记录键，保留协议中的显式空值。
         */
        @JsonSetter("recordKey")
        public void setRecordKey(String value) {
            this.recordKey = value;
            markPresent("recordKey");
        }

        /** 关联市场商品列表的所需段位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reqMasteryRank")
        private BigDecimal reqMasteryRank;

        /**
         * 记录关联市场商品列表的所需段位是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的所需段位，保留协议中的显式空值。
         */
        @JsonSetter("reqMasteryRank")
        public void setReqMasteryRank(BigDecimal value) {
            this.reqMasteryRank = value;
            markPresent("reqMasteryRank");
        }

        /** 关联市场商品列表的裂罅类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rivenType")
        private String rivenType;

        /**
         * 记录关联市场商品列表的裂罅类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的裂罅类型，保留协议中的显式空值。
         */
        @JsonSetter("rivenType")
        public void setRivenType(String value) {
            this.rivenType = value;
            markPresent("rivenType");
        }

        /** 关联市场商品列表的网址标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("slug")
        private String slug;

        /**
         * 记录关联市场商品列表的网址标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的网址标识，保留协议中的显式空值。
         */
        @JsonSetter("slug")
        public void setSlug(String value) {
            this.slug = value;
            markPresent("slug");
        }

        /** 关联市场商品列表的缩略图；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("thumb")
        private String thumb;

        /**
         * 记录关联市场商品列表的缩略图是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的缩略图，保留协议中的显式空值。
         */
        @JsonSetter("thumb")
        public void setThumb(String value) {
            this.thumb = value;
            markPresent("thumb");
        }

        /** 关联市场商品列表的网址标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("url_name")
        private String urlName;

        /**
         * 记录关联市场商品列表的网址标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的网址标识，保留协议中的显式空值。
         */
        @JsonSetter("url_name")
        public void setUrlName(String value) {
            this.urlName = value;
            markPresent("url_name");
        }

        /** 关联市场商品列表的中文图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhIcon")
        @JsonDeserialize(using = OfficialNullDeserializer.class)
        private Void zhIcon;

        /**
         * 记录关联市场商品列表的中文图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的中文图标，保留协议中的显式空值。
         */
        @JsonSetter("zhIcon")
        public void setZhIcon(Void value) {
            this.zhIcon = value;
            markPresent("zhIcon");
        }

        /** 关联市场商品列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhName")
        private String zhName;

        /**
         * 记录关联市场商品列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("zhName")
        public void setZhName(String value) {
            this.zhName = value;
            markPresent("zhName");
        }

        /** 关联市场商品列表的中文缩略图；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhThumb")
        @JsonDeserialize(using = OfficialNullDeserializer.class)
        private Void zhThumb;

        /**
         * 记录关联市场商品列表的中文缩略图是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的中文缩略图，保留协议中的显式空值。
         */
        @JsonSetter("zhThumb")
        public void setZhThumb(Void value) {
            this.zhThumb = value;
            markPresent("zhThumb");
        }

    }

}
