package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

import org.springframework.core.io.Resource;

public record ForumStoredFileDTO(
        Resource resource,
        String contentType,
        long size
) {
}
