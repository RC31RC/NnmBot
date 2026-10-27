package com.nnm.bot.warframe.persistent.curated.service.forum;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleIdQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleDeleteDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleOptionsVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticlePublicVO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleSaveDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleSearchQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumOperatorDTO;
import java.util.List;

public interface ForumArticleService {

    /**
     * 按查询条件筛选论坛文章并返回分页结果。
     *
     * @param request 本次查询的筛选与分页参数。
     * @param operator 操作者。
     * @return 包含当前页记录及总数的分页结果。
     */
    PageResult<ForumArticleVO> query(
            ForumArticleQuery request,
            ForumOperatorDTO operator);

    /**
     * 查询管理端使用的分类和标签选项。
     *
     * @return 文章可选栏目、标签及当前用户的论坛权限。
     */
    ForumArticleOptionsVO options();

    /**
     * 在已启用文章中按关键词搜索，并按相关度返回展示条目。
     *
     *    <p>服务层负责综合字段匹配和排序，最多返回五条候选，避免把整篇文章内容
     *    或大量候选发送到聊天渠道。</p>
     *
     * @param query 当前业务的查询条件。
     * @return 文章详情及当前用户可用的操作组成的列表。
     */
    List<ForumArticleVO> searchEnabled(ForumArticleSearchQuery query);

    /**
     * 读取指定论坛文章的完整详情。
     *
     * @param request 本次查询的筛选与分页参数。
     * @param operator 操作者。
     * @return 文章详情及当前用户可用的操作。
     */
    ForumArticleVO detail(
            ForumArticleIdQuery request,
            ForumOperatorDTO operator);

    /**
     * 读取已发布文章的公开详情。
     *
     * @param id 目标记录的本地主键。
     * @return 已发布文章的公开展示内容。
     */
    ForumArticlePublicVO publicDetail(long id);

    /**
     * 校验输入并创建论坛文章。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param operator 操作者。
     * @return 当前操作计算或读取的数值。
     */
    long create(
            ForumArticleSaveDTO request,
            ForumOperatorDTO operator);

    /**
     * 校验权限与输入后更新论坛文章。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param operator 操作者。
     */
    void update(
            ForumArticleSaveDTO request,
            ForumOperatorDTO operator);

    /**
     * 校验权限后删除论坛文章，并维护关联资源。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param operator 操作者。
     */
    void delete(
            ForumArticleDeleteDTO request,
            ForumOperatorDTO operator);
}
