package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

import java.time.LocalDateTime;

/** 文章内容的只读业务合同，供写入校验和展示共同读取。 */
public interface ForumArticleContentDTO {
    /**
     * 读取标识。
     *
     * @return 本地记录主键。
     */
    Long getId();
    /**
     * 读取地址标识。
     *
     * @return 地址标识对应的文本。
     */
    String getSlug();
    /**
     * 读取标题。
     *
     * @return 标题对应的文本。
     */
    String getTitle();
    /**
     * 读取摘要。
     *
     * @return 摘要对应的文本。
     */
    String getSummary();
    /**
     * 读取标签。
     *
     * @return 标签对应的文本。
     */
    String getTags();
    /**
     * 读取分类。
     *
     * @return 分类对应的文本。
     */
    String getCategory();
    /**
     * 读取封面对象标识。
     *
     * @return 封面对象标识对应的文本。
     */
    String getCoverObjectId();
    /**
     * 读取内容页面。
     *
     * @return 内容页面对应的文本。
     */
    String getContentHtml();
    /**
     * 读取内容文档。
     *
     * @return 内容文档对应的文本。
     */
    String getContentMarkdown();
    /**
     * 读取分页地址。
     *
     * @return 分页地址对应的文本。
     */
    String getPageUrl();
    /**
     * 判断当前功能或记录是否已经启用。
     *
     * @return 满足当前功能或记录是否已经启用时为真，否则为假。
     */
    boolean isEnabled();
    /**
     * 读取排序订单。
     *
     * @return 展示排序值。
     */
    int getSortOrder();
    /**
     * 读取创建时间按用户标识。
     *
     * @return 当前操作计算或读取的数值。
     */
    Long getCreatedByUserId();
    /**
     * 读取创建时间时刻。
     *
     * @return 本地日期时间对象。
     */
    LocalDateTime getCreatedAt();
    /**
     * 读取更新时间时刻。
     *
     * @return 本地日期时间对象。
     */
    LocalDateTime getUpdatedAt();
}
