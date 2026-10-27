package com.nnm.bot.warframe.persistent.official.service.items.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialKeysDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialInboxDTO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.items.WarframeKeyService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeKeyVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeEmailVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeMailVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

@Service
public class WarframeKeyServiceImpl implements WarframeKeyService {
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 读取本业务直接使用的已采纳钥匙及邮件定义。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> selectedRecords() {
        return data.records(Category.KEY);
    }

    /**
     * 按关键词分页查询钥匙及任务入口定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 钥匙及任务入口定义的分页视图。
     */
    @Override
    public WarframeKeyVO keys(String keyword, int page) {
        List<WarframeKeyVO.EntryVO> result = new ArrayList<>();
        for (var root : data.find(Category.KEY, keyword)) {
            var value = (OfficialKeysDTO) root.data();
            List<WarframeKeyVO.StageVO> stages = new ArrayList<>();
            int order = 0;
            for (var child : values(value.getChainStages())) {
                List<WarframeMailVO.AttachmentVO> items = attachments(child.getItemsToGiveWhenTriggered());
                stages.add(new WarframeKeyVO.StageVO(root.bkey(), order++, first(child.getKey()), first(child.getKey()),
                        items, child.getMessageToSendWhenTriggeredSender() == null ? null : mail(child)));
            }
            List<WarframeKeyVO.RewardVO> rewards = new ArrayList<>();
            int rewardOrder = 0;
            for (var child : values(value.getRewards())) {
                String label = values(child.getItemReferences()).stream().map(item -> first(item.getNameZh(), item.getNameEn(), item.getName())).findFirst().orElse("");
                rewards.add(new WarframeKeyVO.RewardVO(rewardOrder++, "", first(child.getRewardType()), first(child.getItemType()), label, count(child.getAmount())));
            }
            result.add(new WarframeKeyVO.EntryVO(root.bkey(), name(root), description(value), first(value.getIcon()),
                    first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", value.getReplayable(),
                    integer(value.getMissionMinEnemyLevel()), integer(value.getMissionMaxEnemyLevel()), first(value.getMissionRewardDroptable()),
                    first(value.getCacheRewardManifest()), stages, rewards));
        }
        return WarframeKeyVO.page(keyword, result, page);
    }

    /**
     * 按关键词分页查询游戏邮件定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 邮件定义的分页视图。
     */
    @Override
    public WarframeEmailVO emails(String keyword, int page) {
        List<WarframeEmailVO.EntryVO> result = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.INBOX)) {
            if (!data.matches(root, keyword)) continue;
            result.add(new WarframeEmailVO.EntryVO(root.bkey(), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED",
                    ((OfficialInboxDTO) root.data()).getSendOnlyOnce(), mail((OfficialInboxDTO) root.data())));
        }
        return WarframeEmailVO.page(keyword, result, page);
    }

    /**
     * 从固定邮件字段生成展示内容。
     *
     * @param value 待处理的字段值。
     * @return 单封游戏邮件的展示内容。
     */
    private WarframeMailVO mail(OfficialInboxDTO value) {
        List<WarframeMailVO.AttachmentVO> attachments = attachments(value.getMessageAttachments());
        return new WarframeMailVO(first(value.getMessageSender()), first(value.getMessageSenderZh(), value.getMessageSenderEn(), value.getMessageSender()), first(value.getMessageTitle()), first(value.getMessageTitleZh(), value.getMessageTitleEn(), value.getMessageTitle()), first(value.getMessageBody()), first(value.getMessageBodyZh(), value.getMessageBodyEn(), value.getMessageBody()), first(value.getMessageIcon()), first(value.getMessageIconUrl()), first(value.getMessageTransmission()), bool(value.getMessageHighPriority()), attachments, first(value.getMessageCustomData()), first(value.getMessageCinematic()));
    }

    /**
     * 从固定邮件字段生成展示内容。
     *
     * @param value 待处理的字段值。
     * @return 单封游戏邮件的展示内容。
     */
    private WarframeMailVO mail(OfficialKeysDTO.ChainStagesDTO value) {
        List<WarframeMailVO.AttachmentVO> attachments = attachments(value.getMessageToSendWhenTriggeredAttachments());
        for (var item : values(value.getMessageToSendWhenTriggeredCountedAttachments())) {
            String label = values(item.getItemReferences()).stream().map(row -> first(row.getNameZh(), row.getNameEn(), row.getName())).findFirst().orElse(first(item.getItemType()));
            attachments.add(new WarframeMailVO.AttachmentVO(first(item.getItemType()), label, count(item.getItemCount()), true));
        }
        return new WarframeMailVO(first(value.getMessageToSendWhenTriggeredSender()), first(value.getMessageToSendWhenTriggeredSenderZh(), value.getMessageToSendWhenTriggeredSenderEn(), value.getMessageToSendWhenTriggeredSender()), first(value.getMessageToSendWhenTriggeredTitle()), first(value.getMessageToSendWhenTriggeredTitleZh(), value.getMessageToSendWhenTriggeredTitleEn(), value.getMessageToSendWhenTriggeredTitle()), first(value.getMessageToSendWhenTriggeredBody()), first(value.getMessageToSendWhenTriggeredBodyZh(), value.getMessageToSendWhenTriggeredBodyEn(), value.getMessageToSendWhenTriggeredBody()), first(value.getMessageToSendWhenTriggeredIcon()), first(value.getMessageToSendWhenTriggeredIconUrl()), first(value.getMessageToSendWhenTriggeredTransmission()), bool(value.getMessageToSendWhenTriggeredHighPriority()), attachments, "", "");
    }

    /**
     * 官方普通附件是字符串列表；计数附件有独立具体模型。
     *
     * @param values 待处理的值集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WarframeMailVO.AttachmentVO> attachments(List<String> values) {
        List<WarframeMailVO.AttachmentVO> result = new ArrayList<>();
        for (String item : values(values)) result.add(new WarframeMailVO.AttachmentVO(item, item, null, false));
        return result;
    }
}
