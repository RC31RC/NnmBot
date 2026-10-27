package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialNullDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.util.List;
import lombok.Getter;

/** INBOX 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialInboxDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 英文展示名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("displayNameEn")
    private String displayNameEn;

    /**
     * 记录英文展示名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文展示名称，保留协议中的显式空值。
     */
    @JsonSetter("displayNameEn")
    public void setDisplayNameEn(String value) {
        this.displayNameEn = value;
        markPresent("displayNameEn");
    }

    /** 中文展示名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("displayNameZh")
    private String displayNameZh;

    /**
     * 记录中文展示名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文展示名称，保留协议中的显式空值。
     */
    @JsonSetter("displayNameZh")
    public void setDisplayNameZh(String value) {
        this.displayNameZh = value;
        markPresent("displayNameZh");
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

    /** 消息的过场动画；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__cinematic")
    private String messageCinematic;

    /**
     * 记录消息的过场动画是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的过场动画，保留协议中的显式空值。
     */
    @JsonSetter("message__cinematic")
    public void setMessageCinematic(String value) {
        this.messageCinematic = value;
        markPresent("message__cinematic");
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

    /** 消息的图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__iconUrl")
    private String messageIconUrl;

    /**
     * 记录消息的图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的图标地址，保留协议中的显式空值。
     */
    @JsonSetter("message__iconUrl")
    public void setMessageIconUrl(String value) {
        this.messageIconUrl = value;
        markPresent("message__iconUrl");
    }

    /** 消息的物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__itemReferences")
    private List<MessageItemReferencesDTO> messageItemReferences;

    /**
     * 记录消息的物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的物品引用，保留协议中的显式空值。
     */
    @JsonSetter("message__itemReferences")
    public void setMessageItemReferences(List<MessageItemReferencesDTO> value) {
        this.messageItemReferences = value;
        markPresent("message__itemReferences");
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

    /** 消息的通讯；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("message__transmission")
    private String messageTransmission;

    /**
     * 记录消息的通讯是否出现，同时区分未提供与显式空值。
     *
     * @param value 消息的通讯，保留协议中的显式空值。
     */
    @JsonSetter("message__transmission")
    public void setMessageTransmission(String value) {
        this.messageTransmission = value;
        markPresent("message__transmission");
    }

    /** 是否仅发送一次；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("sendOnlyOnce")
    private Boolean sendOnlyOnce;

    /**
     * 记录是否仅发送一次是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅发送一次，保留协议中的显式空值。
     */
    @JsonSetter("sendOnlyOnce")
    public void setSendOnlyOnce(Boolean value) {
        this.sendOnlyOnce = value;
        markPresent("sendOnlyOnce");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(displayNameEn), OfficialInputText.first(displayNameZh), null, null, null);
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

    /** 官方嵌套对象 MessageItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MessageItemReferencesDTO extends OfficialObjectDTO {
        /** 消息的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录消息的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 消息的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录消息的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 消息的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录消息的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 消息的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录消息的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 消息的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录消息的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 消息的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录消息的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 消息的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录消息的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 消息的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录消息的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 消息的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

}
