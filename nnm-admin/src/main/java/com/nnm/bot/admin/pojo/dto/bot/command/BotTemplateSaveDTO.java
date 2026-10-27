package com.nnm.bot.admin.pojo.dto.bot.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** HTML 图片与 Markdown 数据视图模板独立保存，不互相覆盖。 */
public record BotTemplateSaveDTO(
        /** 模板稳定编码。 */
        @NotBlank
        String templateCode,
        /** 必须与所属插件声明的视图一致。 */
        @NotBlank
        @Pattern(regexp = "[a-z][a-z0-9_.-]{0,79}")
        String viewKey,
        /** 模板名称。 */
        @NotBlank
        String name,
        /** 所属命令主键，允许为空表示暂不归属命令。 */
        Long commandId,
        /** 命令内展示顺序。 */
        Integer sortOrder,
        /** 完整 HTML 内容。 */
        String htmlContent,
        String markdownContent,
        String plainTextContent,
        /** 是否启用。 */
        @NotNull
        Boolean enabled) {
}
