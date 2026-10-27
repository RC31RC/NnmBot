package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/** 源 INBOX 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_inbox_root")
public class OfficialInboxRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 英文展示名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "displaynameen_state")
    private Short displayNameEnState;

    /** 英文展示名称。 */
    @Column(name = "displaynameen", columnDefinition = "TEXT")
    private String displayNameEn;

    /** 中文展示名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "displaynamezh_state")
    private Short displayNameZhState;

    /** 中文展示名称。 */
    @Column(name = "displaynamezh", columnDefinition = "TEXT")
    private String displayNameZh;

    /** 消息的邮件附件列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_attachments_state")
    private Short messageAttachmentsState;

    /** 消息的正文是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_body_state")
    private Short messageBodyState;

    /** 消息的正文。 */
    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    /** 消息的英文正文是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_bodyen_state")
    private Short messageBodyEnState;

    /** 消息的英文正文。 */
    @Column(name = "message_bodyen", columnDefinition = "TEXT")
    private String messageBodyEn;

    /** 消息的中文正文是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_bodyzh_state")
    private Short messageBodyZhState;

    /** 消息的中文正文。 */
    @Column(name = "message_bodyzh", columnDefinition = "TEXT")
    private String messageBodyZh;

    /** 消息的过场动画是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_cinematic_state")
    private Short messageCinematicState;

    /** 消息的过场动画。 */
    @Column(name = "message_cinematic", columnDefinition = "TEXT")
    private String messageCinematic;

    /** 消息的带数量的附件列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_countedattachments_state")
    private Short messageCountedAttachmentsState;

    /** 消息的自定义业务数据是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_customdata_state")
    private Short messageCustomDataState;

    /** 消息的自定义业务数据。 */
    @Column(name = "message_customdata", columnDefinition = "TEXT")
    private String messageCustomData;

    /** 消息的是否高优先级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_highpriority_state")
    private Short messageHighPriorityState;

    /** 消息的是否高优先级。 */
    @Column(name = "message_highpriority")
    private Boolean messageHighPriority;

    /** 消息的图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_icon_state")
    private Short messageIconState;

    /** 消息的图标。 */
    @Column(name = "message_icon", columnDefinition = "TEXT")
    private String messageIcon;

    /** 消息的图标资源是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_iconasset_state")
    private Short messageIconAssetState;

    /** 消息的图标资源。 */
    @Column(name = "message_iconasset", columnDefinition = "TEXT")
    private String messageIconAsset;

    /** 消息的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_iconstatus_state")
    private Short messageIconStatusState;

    /** 消息的图标状态。 */
    @Column(name = "message_iconstatus", columnDefinition = "TEXT")
    private String messageIconStatus;

    /** 消息的图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_iconurl_state")
    private Short messageIconUrlState;

    /** 消息的图标地址。 */
    @Column(name = "message_iconurl", columnDefinition = "TEXT")
    private String messageIconUrl;

    /** 消息的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_itemreferences_state")
    private Short messageItemReferencesState;

    /** 消息的发件人是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_sender_state")
    private Short messageSenderState;

    /** 消息的发件人。 */
    @Column(name = "message_sender", columnDefinition = "TEXT")
    private String messageSender;

    /** 消息的发件人英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_senderen_state")
    private Short messageSenderEnState;

    /** 消息的发件人英文名称。 */
    @Column(name = "message_senderen", columnDefinition = "TEXT")
    private String messageSenderEn;

    /** 消息的发件人中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_senderzh_state")
    private Short messageSenderZhState;

    /** 消息的发件人中文名称。 */
    @Column(name = "message_senderzh", columnDefinition = "TEXT")
    private String messageSenderZh;

    /** 消息的标题是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_title_state")
    private Short messageTitleState;

    /** 消息的标题。 */
    @Column(name = "message_title", columnDefinition = "TEXT")
    private String messageTitle;

    /** 消息的英文标题是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_titleen_state")
    private Short messageTitleEnState;

    /** 消息的英文标题。 */
    @Column(name = "message_titleen", columnDefinition = "TEXT")
    private String messageTitleEn;

    /** 消息的中文标题是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_titlezh_state")
    private Short messageTitleZhState;

    /** 消息的中文标题。 */
    @Column(name = "message_titlezh", columnDefinition = "TEXT")
    private String messageTitleZh;

    /** 消息的通讯是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "message_transmission_state")
    private Short messageTransmissionState;

    /** 消息的通讯。 */
    @Column(name = "message_transmission", columnDefinition = "TEXT")
    private String messageTransmission;

    /** 是否仅发送一次是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sendonlyonce_state")
    private Short sendOnlyOnceState;

    /** 是否仅发送一次。 */
    @Column(name = "sendonlyonce")
    private Boolean sendOnlyOnce;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialInboxMessageAttachmentsItems> officialInboxMessageAttachmentsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialInboxMessageCountedAttachmentsItems> officialInboxMessageCountedAttachmentsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialInboxMessageItemReferencesItems> officialInboxMessageItemReferencesItemsRows = new ArrayList<>();
}
