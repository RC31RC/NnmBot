package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandGroupVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandGroupPreviewVO;
import com.nnm.bot.admin.controller.assembler.bot.command.AdminBotCommandViewAssembler;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupPreviewDTO;
import com.nnm.bot.admin.pojo.query.bot.command.BotCommandGroupPreviewQuery;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupSaveDTO;
import com.nnm.bot.admin.service.bot.command.BotCommandGroupService;
import com.nnm.bot.common.response.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1/bot-command-groups")
public class BotCommandGroupController {
    @Resource
    private BotCommandGroupService service;

    @GetMapping
    @SaCheckPermission("bot:command-group:view")
    public ApiResponse<List<BotCommandGroupVO>> list() {
        return ApiResponse.ok(AdminBotCommandViewAssembler.toBotCommandGroupVO(service.list()));
    }

    @PostMapping("/{id}/preview")
    @SaCheckPermission("bot:command-group:view")
    public ApiResponse<List<BotCommandGroupPreviewVO>> preview(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotCommandGroupPreviewQuery request) {
        return ApiResponse.ok(AdminBotCommandViewAssembler.toBotCommandGroupPreviewVO(service.preview(id, request)));
    }

    @PostMapping
    @SaCheckPermission("bot:command-group:update")
    public ApiResponse<Long> create(
            @Valid
            @RequestBody
            BotCommandGroupSaveDTO request) {
        return ApiResponse.ok(service.save(request));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:command-group:update")
    public ApiResponse<Long> update(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotCommandGroupSaveDTO request) {
        if (request.id() == null || request.id() != id) throw new IllegalArgumentException("关联命令组 ID 不一致");
        return ApiResponse.ok(service.save(request));
    }

    @DeleteMapping("/{id}/{version}")
    @SaCheckPermission("bot:command-group:update")
    public ApiResponse<Void> delete(
            @PathVariable
            long id,
            @PathVariable
            int version) {
        service.delete(id, version);
        return ApiResponse.ok();
    }
}
