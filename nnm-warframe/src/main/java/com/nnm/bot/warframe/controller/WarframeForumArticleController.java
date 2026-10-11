package com.nnm.bot.warframe.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.application.WarframeForumArticleService;
import com.nnm.bot.warframe.application.WarframeForumOperator;
import com.nnm.bot.warframe.application.WarframeForumUserAuthorityProvider;
import com.nnm.bot.warframe.pojo.WarframeForumArticleIdRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleItem;
import com.nnm.bot.warframe.pojo.WarframeForumArticleOptions;
import com.nnm.bot.warframe.pojo.WarframeForumArticleQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleSaveRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleUpdateRequest;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1/warframe/forum/articles")
public class WarframeForumArticleController {

    @Resource
    private WarframeForumArticleService service;

    @Resource
    private WarframeForumUserAuthorityProvider authorityProvider;

    @PostMapping("/query")
    @SaCheckPermission("warframe:forum:view")
    public ApiResponse<PageResult<WarframeForumArticleItem>> query(
            @RequestBody
            WarframeForumArticleQueryRequest request) {
        return ApiResponse.ok(service.query(request, currentOperator()));
    }

    @PostMapping("/options")
    @SaCheckPermission("warframe:forum:view")
    public ApiResponse<WarframeForumArticleOptions> options() {
        return ApiResponse.ok(service.options());
    }

    @PostMapping("/detail")
    @SaCheckPermission("warframe:forum:view")
    public ApiResponse<WarframeForumArticleItem> detail(
            @Valid
            @RequestBody
            WarframeForumArticleIdRequest request) {
        return ApiResponse.ok(service.detail(request, currentOperator()));
    }

    @PostMapping("/create")
    @SaCheckPermission("warframe:forum:create")
    public ApiResponse<String> create(
            @Valid
            @RequestBody
            WarframeForumArticleSaveRequest request) {
        return ApiResponse.ok(String.valueOf(service.create(request, currentOperator())));
    }

    @PostMapping("/update")
    @SaCheckPermission("warframe:forum:update")
    public ApiResponse<Void> update(
            @Valid
            @RequestBody
            WarframeForumArticleUpdateRequest request) {
        service.update(request, currentOperator());
        return ApiResponse.ok();
    }

    @PostMapping("/delete")
    @SaCheckPermission("warframe:forum:delete")
    public ApiResponse<Void> delete(
            @Valid
            @RequestBody
            WarframeForumArticleIdRequest request) {
        service.delete(request, currentOperator());
        return ApiResponse.ok();
    }

    private WarframeForumOperator currentOperator() {
        long userId = StpUtil.getLoginIdAsLong();
        return new WarframeForumOperator(
                userId,
                authorityProvider.effectiveLevel(userId),
                StpUtil.hasPermission("warframe:forum:update"),
                StpUtil.hasPermission("warframe:forum:delete"));
    }
}
