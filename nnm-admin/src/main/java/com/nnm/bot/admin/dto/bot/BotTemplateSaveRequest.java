package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 保存数据库 HTML 模板的请求。 */
public record BotTemplateSaveRequest(
        /** 模板稳定编码。 */
        @NotBlank
        String templateCode,
        /** 模板名称。 */
        @NotBlank
        String name,
        /** 所属命令主键，允许为空表示暂不归属命令。 */
        Long commandId,
        /** 命令内展示顺序。 */
        Integer sortOrder,
        /** 完整 HTML 内容。 */
        @NotBlank
        String htmlContent,
        /** 是否启用。 */
        @NotNull
        Boolean enabled) {
}
