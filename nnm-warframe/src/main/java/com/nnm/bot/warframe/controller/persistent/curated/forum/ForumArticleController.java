package com.nnm.bot.warframe.controller.persistent.curated.forum;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleIdQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleDeleteDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleOptionsVO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleSaveDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumOperatorDTO;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumArticleService;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumUserAuthorityProvider;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1/warframe/forum/articles")
public class ForumArticleController {

    @Resource
    private ForumArticleService service;

    @Resource
    private ForumUserAuthorityProvider authorityProvider;

    /**
     * 执行当前业务域的查询并组织结果。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/query")
    @SaCheckPermission("warframe:forum:view")
    public ApiResponse<PageResult<ForumArticleVO>> query(
            @RequestBody
            ForumArticleQuery request) {
        return ApiResponse.ok(service.query(request, currentOperator()));
    }

    /**
     * 读取文章栏目、标签与当前操作者的论坛权限选项。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/options")
    @SaCheckPermission("warframe:forum:view")
    public ApiResponse<ForumArticleOptionsVO> options() {
        return ApiResponse.ok(service.options());
    }

    /**
     * 读取指定对象的业务详情。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/detail")
    @SaCheckPermission("warframe:forum:view")
    public ApiResponse<ForumArticleVO> detail(
            @Valid
            @RequestBody
            ForumArticleIdQuery request) {
        return ApiResponse.ok(service.detail(request, currentOperator()));
    }

    /**
     * 创建新的业务记录。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/create")
    @SaCheckPermission("warframe:forum:create")
    public ApiResponse<String> create(
            @Valid
            @RequestBody
            ForumArticleSaveDTO request) {
        return ApiResponse.ok(String.valueOf(service.create(request, currentOperator())));
    }

    /**
     * 更新已有业务记录。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/update")
    @SaCheckPermission("warframe:forum:update")
    public ApiResponse<Void> update(
            @Valid
            @RequestBody
            ForumArticleSaveDTO request) {
        service.update(request, currentOperator());
        return ApiResponse.ok();
    }

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/delete")
    @SaCheckPermission("warframe:forum:delete")
    public ApiResponse<Void> delete(
            @Valid
            @RequestBody
            ForumArticleDeleteDTO request) {
        service.delete(request, currentOperator());
        return ApiResponse.ok();
    }

    /**
     * 从已认证会话构造本次论坛操作显式传递的用户上下文。
     * @return 论坛操作者对象。
     */
    private ForumOperatorDTO currentOperator() {
        long userId = StpUtil.getLoginIdAsLong();
        return new ForumOperatorDTO(
                userId,
                authorityProvider.effectiveLevel(userId),
                StpUtil.hasPermission("warframe:forum:update"),
                StpUtil.hasPermission("warframe:forum:delete"));
    }
}
