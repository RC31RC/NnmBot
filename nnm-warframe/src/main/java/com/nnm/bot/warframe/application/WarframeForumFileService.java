package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.dto.WarframeForumFileItem;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Set;

public interface WarframeForumFileService {

    WarframeForumFileItem storeTemporary(MultipartFile file);

    StoredFile loadPermanent(String year, String month, String fileName);

    StoredFile loadTemporary(String year, String month, String fileName);

    /**
     * 将文章实际引用的本站临时文件固化，并返回已改写为永久地址的内容。
     */
    PreparedContent promoteReferencedFiles(
            String coverUrl,
            String contentHtml,
            String contentMarkdown);

    /**
     * 提取文章字段中由本站永久存储管理的文件地址。
     */
    Set<String> referencedPermanentFiles(
            String coverUrl,
            String contentHtml,
            String contentMarkdown);

    /**
     * 在当前事务提交后删除永久文件，避免数据库回滚后文章引用失效。
     */
    void deletePermanentFilesAfterCommit(Collection<String> fileUrls);

    int cleanExpiredTemporaryFiles();

    record StoredFile(
            Resource resource,
            String contentType,
            long size
    ) {
    }

    record PreparedContent(
            String coverUrl,
            String contentHtml,
            String contentMarkdown
    ) {
    }
}
