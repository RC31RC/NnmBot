package com.nnm.bot.warframe.persistent.official.pojo.query.reference;

/** 官方定义目录命令的检索词、页码及语法校验结果。 */
public record WarframeDefinitionQuery(String keyword, int page, String error) {
}
