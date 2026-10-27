package com.nnm.bot.warframe.schedule.persistent.curated.forum;

import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 清理当前实例本地目录中的临时文件。
 *
 * <p>各实例的临时目录相互独立，不能改成只在一个节点执行的集群任务，否则其他节点的
 * 文件不会被清理。</p>
 */
@Slf4j
@Component
public class ForumTemporaryFileCleanupTask {

    @Resource
    private ForumFileService fileService;

    /**
     * 清理已过期文件。
     */
    @Scheduled(fixedDelayString = "#{@warframeForumStorageProperties.cleanupInterval.toString()}")
    public void cleanExpiredFiles() {
        int deleted = fileService.cleanExpiredTemporaryFiles();
        if (deleted > 0) {
            log.info("Cleaned {} expired Warframe forum temporary files", deleted);
        }
    }
}
