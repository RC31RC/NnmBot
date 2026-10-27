package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.command.BotTemplateVO;
import com.nnm.bot.admin.controller.assembler.bot.command.AdminBotCommandViewAssembler;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateSaveDTO;
import com.nnm.bot.admin.service.bot.command.BotTemplateManagementService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.web.EmptyRequest;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/bot-templates")
public class BotTemplateManagementController {

    @Resource
    private BotTemplateManagementService service;

    @PostMapping("/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<BotTemplateVO>> list(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotCommandViewAssembler.toBotTemplateVO(service.list()));
    }

    @PostMapping
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Long> create(
            @Valid
            @RequestBody
    BotTemplateSaveDTO request) {
        return ApiResponse.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> update(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotTemplateSaveDTO request) {
        service.update(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> delete(
            @PathVariable
            long id) {
        service.delete(id);
        return ApiResponse.ok();
    }
}
