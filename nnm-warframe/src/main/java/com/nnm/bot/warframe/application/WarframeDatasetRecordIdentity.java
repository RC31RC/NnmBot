package com.nnm.bot.warframe.application;

/** 为管理页面和同步预览生成稳定记录身份，不改变数据库主键或业务记录键。 */
public final class WarframeDatasetRecordIdentity {

    private WarframeDatasetRecordIdentity() {
    }

    /**
     * 预览合并的身份必须包含来源和数据集。
     *
     * <p>同一个业务键可以由多个社区项目提供；只用 recordKey 会把一个来源的删除
     * 误判成另一个来源的删除。</p>
     */
    public static String of(String sourceCode, String dataset, String recordKey, String locale) {
        String source = sourceCode == null ? "" : sourceCode.trim();
        String data = dataset == null ? "" : dataset.trim();
        String key = recordKey == null ? "" : recordKey.trim();
        String language = locale == null ? "" : locale.trim();
        return source + '\u0000' + data + '\u0000' + key + '\u0000' + language;
    }
}
