package com.nnm.bot.warframe.controller;

import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.application.WarframeForumArticleService;
import com.nnm.bot.warframe.dto.WarframeForumArticlePublicItem;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/warframe/forum/articles")
public class WarframeForumPublicArticleController {

    @Resource
    private WarframeForumArticleService service;

    @GetMapping("/{id}")
    public ApiResponse<WarframeForumArticlePublicItem> detail(@PathVariable long id) {
        return ApiResponse.ok(service.publicDetail(id));
    }
}
