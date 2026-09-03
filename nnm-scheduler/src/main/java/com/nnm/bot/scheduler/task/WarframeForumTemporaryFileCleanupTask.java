package com.nnm.bot.scheduler.task;

import com.nnm.bot.warframe.application.WarframeForumFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarframeForumTemporaryFileCleanupTask {

    @Resource
    private WarframeForumFileService fileService;

    @Scheduled(fixedDelayString = "${nnm.warframe.forum-storage.cleanup-interval:2h}")
    public void cleanExpiredFiles() {
        try {
            int deleted = fileService.cleanExpiredTemporaryFiles();
            if (deleted > 0) {
                log.info("Cleaned {} expired Warframe forum temporary files", deleted);
            }
        } catch (Exception exception) {
            log.error("Warframe forum temporary file cleanup failed", exception);
        }
    }
}
