package com.nnm.bot.admin.pojo.dto.bot.command;

import java.time.LocalDateTime;

/** 数据库 HTML 模板展示对象。 */
public record BotTemplateDTO(
        /** 模板主键。 */
        Long id,
        /** 模板稳定编码。 */
        String templateCode,
        /** 模板适用的数据视图键。 */
        String viewKey,
        /** 模板名称。 */
        String name,
        /** 所属命令主键。 */
        Long commandId,
        /** 模板在命令下的排序。 */
        int sortOrder,
        /** 完整 HTML 内容。 */
        String htmlContent,
        String markdownContent,
        String plainTextContent,
        /** 是否启用。 */
        boolean enabled,
        /** 引用该模板的命令数量。 */
        long commandCount,
        /** 创建时间。 */
        LocalDateTime createdAt,
        /** 最后更新时间。 */
        LocalDateTime updatedAt) {
}
