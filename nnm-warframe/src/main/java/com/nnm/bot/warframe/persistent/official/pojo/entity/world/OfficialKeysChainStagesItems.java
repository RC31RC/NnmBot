package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/** 源 KEYS chainStages/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_keys_chainstages_items")
public class OfficialKeysChainStagesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 触发后给予的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemstogivewhentriggered_state")
    private Short itemsToGiveWhenTriggeredState;

    /** 键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "key_value_state")
    private Short keyState;

    /** 键。 */
    @Column(name = "key_value", columnDefinition = "TEXT")
    private String key;

    /** 触发后发送的邮件的邮件附件列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_attachments_state")
    private Short messageToSendWhenTriggeredAttachmentsState;

    /** 触发后发送的邮件的正文是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_body_state")
    private Short messageToSendWhenTriggeredBodyState;

    /** 触发后发送的邮件的正文。 */
    @Column(name = "messagetosendwhentriggered_body", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredBody;

    /** 触发后发送的邮件的英文正文是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_bodyen_state")
    private Short messageToSendWhenTriggeredBodyEnState;

    /** 触发后发送的邮件的英文正文。 */
    @Column(name = "messagetosendwhentriggered_bodyen", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredBodyEn;

    /** 触发后发送的邮件的中文正文是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_bodyzh_state")
    private Short messageToSendWhenTriggeredBodyZhState;

    /** 触发后发送的邮件的中文正文。 */
    @Column(name = "messagetosendwhentriggered_bodyzh", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredBodyZh;

    /** 触发后发送的邮件的带数量的附件列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_countedattachments_state")
    private Short messageToSendWhenTriggeredCountedAttachmentsState;

    /** 触发后发送的邮件的是否高优先级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_highpriority_state")
    private Short messageToSendWhenTriggeredHighPriorityState;

    /** 触发后发送的邮件的是否高优先级。 */
    @Column(name = "messagetosendwhentriggered_highpriority")
    private Boolean messageToSendWhenTriggeredHighPriority;

    /** 触发后发送的邮件的图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_icon_state")
    private Short messageToSendWhenTriggeredIconState;

    /** 触发后发送的邮件的图标。 */
    @Column(name = "messagetosendwhentriggered_icon", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredIcon;

    /** 触发后发送的邮件的图标资源是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_iconasset_state")
    private Short messageToSendWhenTriggeredIconAssetState;

    /** 触发后发送的邮件的图标资源。 */
    @Column(name = "messagetosendwhentriggered_iconasset", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredIconAsset;

    /** 触发后发送的邮件的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_iconstatus_state")
    private Short messageToSendWhenTriggeredIconStatusState;

    /** 触发后发送的邮件的图标状态。 */
    @Column(name = "messagetosendwhentriggered_iconstatus", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredIconStatus;

    /** 触发后发送的邮件的图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_iconurl_state")
    private Short messageToSendWhenTriggeredIconUrlState;

    /** 触发后发送的邮件的图标地址。 */
    @Column(name = "messagetosendwhentriggered_iconurl", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredIconUrl;

    /** 触发后发送的邮件的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_itemreferences_state")
    private Short messageToSendWhenTriggeredItemReferencesState;

    /** 触发后发送的邮件的发件人是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_sender_state")
    private Short messageToSendWhenTriggeredSenderState;

    /** 触发后发送的邮件的发件人。 */
    @Column(name = "messagetosendwhentriggered_sender", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredSender;

    /** 触发后发送的邮件的发件人英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_senderen_state")
    private Short messageToSendWhenTriggeredSenderEnState;

    /** 触发后发送的邮件的发件人英文名称。 */
    @Column(name = "messagetosendwhentriggered_senderen", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredSenderEn;

    /** 触发后发送的邮件的发件人中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_senderzh_state")
    private Short messageToSendWhenTriggeredSenderZhState;

    /** 触发后发送的邮件的发件人中文名称。 */
    @Column(name = "messagetosendwhentriggered_senderzh", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredSenderZh;

    /** 触发后发送的邮件的标题是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_title_state")
    private Short messageToSendWhenTriggeredTitleState;

    /** 触发后发送的邮件的标题。 */
    @Column(name = "messagetosendwhentriggered_title", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredTitle;

    /** 触发后发送的邮件的英文标题是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_titleen_state")
    private Short messageToSendWhenTriggeredTitleEnState;

    /** 触发后发送的邮件的英文标题。 */
    @Column(name = "messagetosendwhentriggered_titleen", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredTitleEn;

    /** 触发后发送的邮件的中文标题是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_titlezh_state")
    private Short messageToSendWhenTriggeredTitleZhState;

    /** 触发后发送的邮件的中文标题。 */
    @Column(name = "messagetosendwhentriggered_titlezh", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredTitleZh;

    /** 触发后发送的邮件的通讯是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "messagetosendwhentriggered_transmission_state")
    private Short messageToSendWhenTriggeredTransmissionState;

    /** 触发后发送的邮件的通讯。 */
    @Column(name = "messagetosendwhentriggered_transmission", columnDefinition = "TEXT")
    private String messageToSendWhenTriggeredTransmission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialKeysRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysChainStagesItemsItemReferencesItems> officialKeysChainStagesItemsItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems> officialKeysChainStagesItemsItemsToGiveWhenTriggeredItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems> officialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems> officialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems> officialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItemsRows = new ArrayList<>();
}
