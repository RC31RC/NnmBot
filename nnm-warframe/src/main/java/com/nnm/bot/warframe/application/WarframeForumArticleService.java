package com.nnm.bot.warframe.application;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.dto.WarframeForumArticleIdRequest;
import com.nnm.bot.warframe.dto.WarframeForumArticleItem;
import com.nnm.bot.warframe.dto.WarframeForumArticlePublicItem;
import com.nnm.bot.warframe.dto.WarframeForumArticleQueryRequest;
import com.nnm.bot.warframe.dto.WarframeForumArticleSaveRequest;
import com.nnm.bot.warframe.dto.WarframeForumArticleUpdateRequest;

import java.util.List;

public interface WarframeForumArticleService {

    PageResult<WarframeForumArticleItem> query(
            WarframeForumArticleQueryRequest request,
            WarframeForumOperator operator);

    /**
     * 查询可被机器人使用的文章，只返回启用记录和有限数量的候选。
     */
    List<WarframeForumArticleItem> searchEnabled(String keyword);

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
