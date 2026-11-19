package com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.util.List;
import lombok.Getter;

/** BOSS 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialBossDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 执刑官源力石分类的选择项；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("shardCategory__selection")
    private String shardCategorySelection;

    /**
     * 记录执刑官源力石分类的选择项是否出现，同时区分未提供与显式空值。
     *
     * @param value 执刑官源力石分类的选择项，保留协议中的显式空值。
     */
    @JsonSetter("shardCategory__selection")
    public void setShardCategorySelection(String value) {
        this.shardCategorySelection = value;
        markPresent("shardCategory__selection");
    }

    /** 执刑官源力石分类的变体列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("shardCategory__variants")
    private List<ShardCategoryVariantsDTO> shardCategoryVariants;

    /**
     * 记录执刑官源力石分类的变体列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 执刑官源力石分类的变体列表，保留协议中的显式空值。
     */
    @JsonSetter("shardCategory__variants")
    public void setShardCategoryVariants(List<ShardCategoryVariantsDTO> value) {
        this.shardCategoryVariants = value;
        markPresent("shardCategory__variants");
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
        return null;
    }

    /** 官方嵌套对象 ShardCategoryVariantsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ShardCategoryVariantsDTO extends OfficialObjectDTO {
        /** 执刑官源力石分类的变体列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录执刑官源力石分类的变体列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 执刑官源力石分类的变体列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 执刑官源力石分类的变体列表的名称本地化键；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameKey")
        private String nameKey;

        /**
         * 记录执刑官源力石分类的变体列表的名称本地化键是否出现，同时区分未提供与显式空值。
         *
         * @param value 执刑官源力石分类的变体列表的名称本地化键，保留协议中的显式空值。
         */
        @JsonSetter("nameKey")
        public void setNameKey(String value) {
            this.nameKey = value;
            markPresent("nameKey");
        }

        /** 执刑官源力石分类的变体列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录执刑官源力石分类的变体列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 执刑官源力石分类的变体列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 执刑官源力石分类的变体列表的源力石强化状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("tauforged")
        private Boolean tauforged;

        /**
         * 记录执刑官源力石分类的变体列表的源力石强化状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 执刑官源力石分类的变体列表的源力石强化状态，保留协议中的显式空值。
         */
        @JsonSetter("tauforged")
        public void setTauforged(Boolean value) {
            this.tauforged = value;
            markPresent("tauforged");
        }

    }

}
