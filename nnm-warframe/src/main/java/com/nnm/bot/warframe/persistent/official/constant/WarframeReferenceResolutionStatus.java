package com.nnm.bot.warframe.persistent.official.constant;

/** 引用解析状态：唯一身份且有可读名称为 RESOLVED；歧义或确实缺少可展示文本时才保留其他状态，英文不等于未解析。 */
public enum WarframeReferenceResolutionStatus {
    RESOLVED, AMBIGUOUS, UNRESOLVED
}
