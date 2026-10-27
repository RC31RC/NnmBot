package com.nnm.bot.warframe.persistent.official.service.image;

public interface WarframeImageResolver {
    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param imageReference 图片引用。
     * @param sourceCode 来源代码。
     * @return 解析引用或输入对应的实际业务对象对应的文本。
     */
    String resolve(String imageReference, String sourceCode);

    /**
     * 解析文本图标。
     *
     * @param token 凭据。
     * @param platform 市场或官方接口使用的平台代码。
     * @return 解析文本图标对应的文本。
     */
    String resolveTextIcon(String token, String platform);

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    void refresh();
}
