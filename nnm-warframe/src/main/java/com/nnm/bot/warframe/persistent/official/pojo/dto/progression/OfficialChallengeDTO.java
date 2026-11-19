package com.nnm.bot.warframe.persistent.official.pojo.dto.progression;

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

/** CHALLENGE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialChallengeDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 带数量的奖励列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("countedRewards")
    private List<CountedRewardsDTO> countedRewards;

    /**
     * 记录带数量的奖励列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 带数量的奖励列表，保留协议中的显式空值。
     */
    @JsonSetter("countedRewards")
    public void setCountedRewards(List<CountedRewardsDTO> value) {
        this.countedRewards = value;
        markPresent("countedRewards");
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

    /** 背景说明；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("flavour")
    private String flavour;

    /**
     * 记录背景说明是否出现，同时区分未提供与显式空值。
     *
     * @param value 背景说明，保留协议中的显式空值。
     */
    @JsonSetter("flavour")
    public void setFlavour(String value) {
        this.flavour = value;
        markPresent("flavour");
    }

    /** 英文背景说明；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("flavourEn")
    private String flavourEn;

    /**
     * 记录英文背景说明是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文背景说明，保留协议中的显式空值。
     */
    @JsonSetter("flavourEn")
    public void setFlavourEn(String value) {
        this.flavourEn = value;
        markPresent("flavourEn");
    }

    /** 中文背景说明；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("flavourZh")
    private String flavourZh;

    /**
     * 记录中文背景说明是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文背景说明，保留协议中的显式空值。
     */
    @JsonSetter("flavourZh")
    public void setFlavourZh(String value) {
        this.flavourZh = value;
        markPresent("flavourZh");
    }

    /** 提示；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("hint")
    private String hint;

    /**
     * 记录提示是否出现，同时区分未提供与显式空值。
     *
     * @param value 提示，保留协议中的显式空值。
     */
    @JsonSetter("hint")
    public void setHint(String value) {
        this.hint = value;
        markPresent("hint");
    }

    /** 英文提示；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("hintEn")
    private String hintEn;

    /**
     * 记录英文提示是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文提示，保留协议中的显式空值。
     */
    @JsonSetter("hintEn")
    public void setHintEn(String value) {
        this.hintEn = value;
        markPresent("hintEn");
    }

    /** 中文提示；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("hintZh")
    private String hintZh;

    /**
     * 记录中文提示是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文提示，保留协议中的显式空值。
     */
    @JsonSetter("hintZh")
    public void setHintZh(String value) {
        this.hintZh = value;
        markPresent("hintZh");
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

    /** 消息的邮件附件列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__attachments")
    private List<String> messageAttachments;

    /**
     * 记录消息的邮件附件列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的邮件附件列表，保留协议中的显式空值。
     */
    @JsonSetter("message__attachments")
    public void setMessageAttachments(List<String> value) {
        this.messageAttachments = value;
        markPresent("message__attachments");
    }

    /** 消息的正文；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__body")
    private String messageBody;

    /**
     * 记录消息的正文是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的正文，保留协议中的显式空值。
     */
    @JsonSetter("message__body")
    public void setMessageBody(String value) {
        this.messageBody = value;
        markPresent("message__body");
    }

    /** 消息的英文正文；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__bodyEn")
    private String messageBodyEn;

    /**
     * 记录消息的英文正文是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的英文正文，保留协议中的显式空值。
     */
    @JsonSetter("message__bodyEn")
    public void setMessageBodyEn(String value) {
        this.messageBodyEn = value;
        markPresent("message__bodyEn");
    }

    /** 消息的中文正文；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__bodyZh")
    private String messageBodyZh;

    /**
     * 记录消息的中文正文是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的中文正文，保留协议中的显式空值。
     */
    @JsonSetter("message__bodyZh")
    public void setMessageBodyZh(String value) {
        this.messageBodyZh = value;
        markPresent("message__bodyZh");
    }

    /** 消息的带数量的附件列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__countedAttachments")
    @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
    private List<Void> messageCountedAttachments;

    /**
     * 记录消息的带数量的附件列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的带数量的附件列表，保留协议中的显式空值。
     */
    @JsonSetter("message__countedAttachments")
    public void setMessageCountedAttachments(List<Void> value) {
        this.messageCountedAttachments = value;
        markPresent("message__countedAttachments");
    }

    /** 消息的自定义业务数据；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__customData")
    private String messageCustomData;

    /**
     * 记录消息的自定义业务数据是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的自定义业务数据，保留协议中的显式空值。
     */
    @JsonSetter("message__customData")
    public void setMessageCustomData(String value) {
        this.messageCustomData = value;
        markPresent("message__customData");
    }

    /** 消息的是否高优先级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__highPriority")
    private Boolean messageHighPriority;

    /**
     * 记录消息的是否高优先级是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的是否高优先级，保留协议中的显式空值。
     */
    @JsonSetter("message__highPriority")
    public void setMessageHighPriority(Boolean value) {
        this.messageHighPriority = value;
        markPresent("message__highPriority");
    }

    /** 消息的图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__icon")
    private String messageIcon;

    /**
     * 记录消息的图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的图标，保留协议中的显式空值。
     */
    @JsonSetter("message__icon")
    public void setMessageIcon(String value) {
        this.messageIcon = value;
        markPresent("message__icon");
    }

    /** 消息的图标资源；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__iconAsset")
    private String messageIconAsset;

    /**
     * 记录消息的图标资源是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的图标资源，保留协议中的显式空值。
     */
    @JsonSetter("message__iconAsset")
    public void setMessageIconAsset(String value) {
        this.messageIconAsset = value;
        markPresent("message__iconAsset");
    }

    /** 消息的图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__iconStatus")
    private String messageIconStatus;

    /**
     * 记录消息的图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的图标状态，保留协议中的显式空值。
     */
    @JsonSetter("message__iconStatus")
    public void setMessageIconStatus(String value) {
        this.messageIconStatus = value;
        markPresent("message__iconStatus");
    }

    /** 消息的发件人；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__sender")
    private String messageSender;

    /**
     * 记录消息的发件人是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的发件人，保留协议中的显式空值。
     */
    @JsonSetter("message__sender")
    public void setMessageSender(String value) {
        this.messageSender = value;
        markPresent("message__sender");
    }

    /** 消息的发件人英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__senderEn")
    private String messageSenderEn;

    /**
     * 记录消息的发件人英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的发件人英文名称，保留协议中的显式空值。
     */
    @JsonSetter("message__senderEn")
    public void setMessageSenderEn(String value) {
        this.messageSenderEn = value;
        markPresent("message__senderEn");
    }

    /** 消息的发件人中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__senderZh")
    private String messageSenderZh;

    /**
     * 记录消息的发件人中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的发件人中文名称，保留协议中的显式空值。
     */
    @JsonSetter("message__senderZh")
    public void setMessageSenderZh(String value) {
        this.messageSenderZh = value;
        markPresent("message__senderZh");
    }

    /** 消息的标题；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__title")
    private String messageTitle;

    /**
     * 记录消息的标题是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的标题，保留协议中的显式空值。
     */
    @JsonSetter("message__title")
    public void setMessageTitle(String value) {
        this.messageTitle = value;
        markPresent("message__title");
    }

    /** 消息的英文标题；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__titleEn")
    private String messageTitleEn;

    /**
     * 记录消息的英文标题是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的英文标题，保留协议中的显式空值。
     */
    @JsonSetter("message__titleEn")
    public void setMessageTitleEn(String value) {
        this.messageTitleEn = value;
        markPresent("message__titleEn");
    }

    /** 消息的中文标题；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__titleZh")
    private String messageTitleZh;

    /**
     * 记录消息的中文标题是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的中文标题，保留协议中的显式空值。
     */
    @JsonSetter("message__titleZh")
    public void setMessageTitleZh(String value) {
        this.messageTitleZh = value;
        markPresent("message__titleZh");
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

    /** 需求数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("requiredCount")
    private BigDecimal requiredCount;

    /**
     * 记录需求数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 需求数量，保留协议中的显式空值。
     */
    @JsonSetter("requiredCount")
    public void setRequiredCount(BigDecimal value) {
        this.requiredCount = value;
        markPresent("requiredCount");
    }

    /** 声望；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("standing")
    private BigDecimal standing;

    /**
     * 记录声望是否出现，同时区分未提供与显式空值。
     *
     * @param value 声望，保留协议中的显式空值。
     */
    @JsonSetter("standing")
    public void setStanding(BigDecimal value) {
        this.standing = value;
        markPresent("standing");
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

    /** 官方嵌套对象 CountedRewardsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class CountedRewardsItemReferencesDTO extends OfficialObjectDTO {
        /** 带数量的奖励列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录带数量的奖励列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 带数量的奖励列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录带数量的奖励列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 带数量的奖励列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录带数量的奖励列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 带数量的奖励列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录带数量的奖励列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 带数量的奖励列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录带数量的奖励列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 带数量的奖励列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录带数量的奖励列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 带数量的奖励列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录带数量的奖励列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 带数量的奖励列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录带数量的奖励列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 带数量的奖励列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录带数量的奖励列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 CountedRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class CountedRewardsDTO extends OfficialObjectDTO {
        /** 带数量的奖励列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录带数量的奖励列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 带数量的奖励列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<CountedRewardsItemReferencesDTO> itemReferences;

        /**
         * 记录带数量的奖励列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<CountedRewardsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 带数量的奖励列表的商店物品路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("StoreItem")
        private String storeItem;

        /**
         * 记录带数量的奖励列表的商店物品路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 带数量的奖励列表的商店物品路径，保留协议中的显式空值。
         */
        @JsonSetter("StoreItem")
        public void setStoreItem(String value) {
            this.storeItem = value;
            markPresent("StoreItem");
        }

    }

}
