package com.nnm.bot.admin.adapter.notification;

import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import java.util.List;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import com.nnm.bot.admin.repository.bot.notification.BotNotificationJobRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.plugin.response.NotificationDeliveryGuard;
import jakarta.annotation.Resource;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DatabaseNotificationDeliveryGuard implements NotificationDeliveryGuard {
    @Resource
    private BotNotificationJobRepository jobs;
    @Resource
    private SysUserRepository users;
    @Resource
    private BotChannelInstanceRepository instances;
    @Resource
    private BotGroupRepository groups;
    @Resource
    private BotGroupBotRepository bindings;

    @Override
    public void validate(UUID submissionId, BotProactiveTarget target) {
        BotNotificationJob job = jobs.findById(submissionId).orElseThrow(() -> new BotMessageNotSentException("通知任务已不存在"));
        if (job.getStatus().terminal() || job.getStatus() == JobStatus.CANCEL_REQUESTED
                || !users.existsByIdAndEnabledTrue(job.getOwnerId())
                || !StpUtil.hasPermission(job.getOwnerId(), "bot:notification:send")) {
            throw new BotMessageNotSentException("通知任务已结束、取消或发起人已无发送权限");
        }
        BotChannelInstance bot = instances.findByPlatformAndBotAccountId(target.platform(), target.botAccountId())
                .filter(instance -> instance.isEnabled()).orElseThrow(() -> new BotMessageNotSentException("机器人已停用或删除"));
        List<Long> groupIds = groups.findAllById(job.getRequest().groupIds()).stream().filter(group -> group.isEnabled())
                .map(group -> group.getId()).toList();
        boolean bound = bindings.findAllByGroupIdInAndEnabledTrue(groupIds).stream().anyMatch(binding ->
                binding.getBotInstanceId().equals(bot.getId()) && "GROUP".equals(binding.getScopeType())
                        && target.platform().name().equals(binding.getPlatform()) && target.conversationId().equals(binding.getScopeKey()));
        if (!bound) throw new BotMessageNotSentException("接收群组已停用或机器人绑定已移除");
    }
}
