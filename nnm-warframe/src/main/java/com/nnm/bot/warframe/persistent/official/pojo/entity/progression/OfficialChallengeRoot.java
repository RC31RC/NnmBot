package com.nnm.bot.warframe.persistent.official.pojo.entity.progression;

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
import java.math.BigDecimal;

/** 源 CHALLENGE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_challenge_root")
public class OfficialChallengeRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 带数量的奖励列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "countedrewards_state")
    private Short countedRewardsState;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String descriptionValue;

    /** 英文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionen_state")
    private Short descriptionEnState;

    /** 英文描述。 */
    @Column(name = "descriptionen", columnDefinition = "TEXT")
    private String descriptionEn;

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionzh_state")
    private Short descriptionZhState;

    /** 中文描述。 */
    @Column(name = "descriptionzh", columnDefinition = "TEXT")
    private String descriptionZh;

    /** 背景说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "flavour_state")
    private Short flavourState;

    /** 背景说明。 */
    @Column(name = "flavour", columnDefinition = "TEXT")
    private String flavour;

    /** 英文背景说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "flavouren_state")
    private Short flavourEnState;

    /** 英文背景说明。 */
    @Column(name = "flavouren", columnDefinition = "TEXT")
    private String flavourEn;

    /** 中文背景说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "flavourzh_state")
    private Short flavourZhState;

    /** 中文背景说明。 */
    @Column(name = "flavourzh", columnDefinition = "TEXT")
    private String flavourZh;

    /** 提示是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hint_state")
    private Short hintState;

    /** 提示。 */
    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;

    /** 英文提示是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hinten_state")
    private Short hintEnState;

    /** 英文提示。 */
    @Column(name = "hinten", columnDefinition = "TEXT")
    private String hintEn;

    /** 中文提示是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hintzh_state")
    private Short hintZhState;

    /** 中文提示。 */
    @Column(name = "hintzh", columnDefinition = "TEXT")
    private String hintZh;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    /** 图标资源是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconasset_state")
    private Short iconAssetState;

    /** 图标资源。 */
    @Column(name = "iconasset", columnDefinition = "TEXT")
    private String iconAsset;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconurl_state")
    private Short iconUrlState;

    /** 图标地址。 */
    @Column(name = "iconurl", columnDefinition = "TEXT")
    private String iconUrl;

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

    /** 名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "name_state")
    private Short nameState;

    /** 名称。 */
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    /** 英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nameen_state")
    private Short nameEnState;

    /** 英文名称。 */
    @Column(name = "nameen", columnDefinition = "TEXT")
    private String nameEn;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 需求数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "requiredcount_state")
    private Short requiredCountState;

    /** 需求数量。 */
    @Column(name = "requiredcount", columnDefinition = "NUMERIC")
    private BigDecimal requiredCount;

    /** 声望是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "standing_state")
    private Short standingState;

    /** 声望。 */
    @Column(name = "standing", columnDefinition = "NUMERIC")
    private BigDecimal standing;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialChallengeCountedRewardsItems> officialChallengeCountedRewardsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialChallengeMessageAttachmentsItems> officialChallengeMessageAttachmentsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialChallengeMessageCountedAttachmentsItems> officialChallengeMessageCountedAttachmentsItemsRows = new ArrayList<>();
}
