package com.nnm.bot.warframe.schedule.persistent.official.apply;

import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyWorkService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 每个实例本地轮询一个文件；不使用集群独占调度，跨节点由数据库认领。 */
@Slf4j
@Component
public class WarframeOfficialApplyPollTask {
    @Resource
    private WarframeOfficialApplyWorkService work;

    @Scheduled(fixedDelayString = "${nnm.warframe.pull.dataset.apply-poll-interval:5s}")
    public void poll() {
        try {
            work.processAvailable();
        } catch (RuntimeException exception) {
            log.warn("官方审批工作项轮询将在下次调度重试", exception);
        }
    }
}
