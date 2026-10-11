package com.nnm.bot.warframe.application;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.pojo.WarframeForumArticleIdRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleItem;
import com.nnm.bot.warframe.pojo.WarframeForumArticleOptions;
import com.nnm.bot.warframe.pojo.WarframeForumArticlePublicItem;
import com.nnm.bot.warframe.pojo.WarframeForumArticleQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleSaveRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleSearchQuery;
import com.nnm.bot.warframe.pojo.WarframeForumArticleUpdateRequest;

import java.util.List;

public interface WarframeForumArticleService {

    PageResult<WarframeForumArticleItem> query(
            WarframeForumArticleQueryRequest request,
            WarframeForumOperator operator);

    /** 查询管理端使用的分类和标签选项。 */
    WarframeForumArticleOptions options();

    /**
     * 查询可被机器人使用的文章。
     *
     * <p>服务层负责综合字段匹配和排序，最多返回五条候选，避免把整篇文章内容
     * 或大量候选发送到聊天渠道。</p>
     */
    List<WarframeForumArticleItem> searchEnabled(WarframeForumArticleSearchQuery query);

    WarframeForumArticleItem detail(
            WarframeForumArticleIdRequest request,
            WarframeForumOperator operator);

    WarframeForumArticlePublicItem publicDetail(long id);

    long create(
            WarframeForumArticleSaveRequest request,
            WarframeForumOperator operator);

    void update(
            WarframeForumArticleUpdateRequest request,
            WarframeForumOperator operator);

    void delete(
            WarframeForumArticleIdRequest request,
            WarframeForumOperator operator);
}
