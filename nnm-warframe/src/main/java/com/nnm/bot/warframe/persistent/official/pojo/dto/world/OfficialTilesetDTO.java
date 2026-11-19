package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.util.List;
import lombok.Getter;

/** TILESET 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialTilesetDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 阵营；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("faction")
    private String faction;

    /**
     * 记录阵营是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营，保留协议中的显式空值。
     */
    @JsonSetter("faction")
    public void setFaction(String value) {
        this.faction = value;
        markPresent("faction");
    }

    /** 阵营英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("factionNameEn")
    private String factionNameEn;

    /**
     * 记录阵营英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营英文名称，保留协议中的显式空值。
     */
    @JsonSetter("factionNameEn")
    public void setFactionNameEn(String value) {
        this.factionNameEn = value;
        markPresent("factionNameEn");
    }

    /** 阵营中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("factionNameZh")
    private String factionNameZh;

    /**
     * 记录阵营中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营中文名称，保留协议中的显式空值。
     */
    @JsonSetter("factionNameZh")
    public void setFactionNameZh(String value) {
        this.factionNameZh = value;
        markPresent("factionNameZh");
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

    /** 任务定义列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionDefinitions")
    private List<MissionDefinitionsDTO> missionDefinitions;

    /**
     * 记录任务定义列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务定义列表，保留协议中的显式空值。
     */
    @JsonSetter("missionDefinitions")
    public void setMissionDefinitions(List<MissionDefinitionsDTO> value) {
        this.missionDefinitions = value;
        markPresent("missionDefinitions");
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
        return new OfficialRecordHeaderDTO(null, null, null, null, null);
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

    /** 官方嵌套对象 MissionDefinitionsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionDefinitionsItemReferencesDTO extends OfficialObjectDTO {
        /** 任务定义列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录任务定义列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 任务定义列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务定义列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务定义列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务定义列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务定义列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务定义列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务定义列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务定义列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务定义列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务定义列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务定义列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录任务定义列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 任务定义列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务定义列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务定义列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务定义列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 MissionDefinitionsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionDefinitionsDTO extends OfficialObjectDTO {
        /** 任务定义列表的高级敌人生成器列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("advancedSpawners")
        private List<String> advancedSpawners;

        /**
         * 记录任务定义列表的高级敌人生成器列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的高级敌人生成器列表，保留协议中的显式空值。
         */
        @JsonSetter("advancedSpawners")
        public void setAdvancedSpawners(List<String> value) {
            this.advancedSpawners = value;
            markPresent("advancedSpawners");
        }

        /** 任务定义列表的敌人配置列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("enemySpecs")
        private List<String> enemySpecs;

        /**
         * 记录任务定义列表的敌人配置列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的敌人配置列表，保留协议中的显式空值。
         */
        @JsonSetter("enemySpecs")
        public void setEnemySpecs(List<String> value) {
            this.enemySpecs = value;
            markPresent("enemySpecs");
        }

        /** 任务定义列表的额外敌人配置列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("extraEnemySpecs")
        private List<String> extraEnemySpecs;

        /**
         * 记录任务定义列表的额外敌人配置列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的额外敌人配置列表，保留协议中的显式空值。
         */
        @JsonSetter("extraEnemySpecs")
        public void setExtraEnemySpecs(List<String> value) {
            this.extraEnemySpecs = value;
            markPresent("extraEnemySpecs");
        }

        /** 任务定义列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<MissionDefinitionsItemReferencesDTO> itemReferences;

        /**
         * 记录任务定义列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<MissionDefinitionsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 任务定义列表的任务类型代码；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("missionType")
        private String missionType;

        /**
         * 记录任务定义列表的任务类型代码是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的任务类型代码，保留协议中的显式空值。
         */
        @JsonSetter("missionType")
        public void setMissionType(String value) {
            this.missionType = value;
            markPresent("missionType");
        }

        /** 任务定义列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务定义列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务定义列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务定义列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务定义列表的异常状态等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("procLevel")
        private String procLevel;

        /**
         * 记录任务定义列表的异常状态等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的异常状态等级，保留协议中的显式空值。
         */
        @JsonSetter("procLevel")
        public void setProcLevel(String value) {
            this.procLevel = value;
            markPresent("procLevel");
        }

        /** 任务定义列表的重点目标配置；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("vipAgent")
        private String vipAgent;

        /**
         * 记录任务定义列表的重点目标配置是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务定义列表的重点目标配置，保留协议中的显式空值。
         */
        @JsonSetter("vipAgent")
        public void setVipAgent(String value) {
            this.vipAgent = value;
            markPresent("vipAgent");
        }

    }

}
