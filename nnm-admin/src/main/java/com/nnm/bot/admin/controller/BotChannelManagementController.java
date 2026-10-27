package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.channel.OneBotAccountProbeVO;
import com.nnm.bot.admin.pojo.vo.bot.channel.BotChannelInstanceVO;
import com.nnm.bot.admin.controller.assembler.bot.channel.AdminBotChannelViewAssembler;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.dto.bot.channel.BotChannelInstanceDTO;
import com.nnm.bot.admin.pojo.query.bot.channel.OneBotAccountProbeQuery;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotAccountProbeDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotInstanceSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.QqOfficialInstanceSaveDTO;
import com.nnm.bot.admin.service.bot.channel.BotChannelManagementService;
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
@RequestMapping("/api/admin/v1/bot-instances")
public class BotChannelManagementController {

    @Resource
    private BotChannelManagementService service;

    @PostMapping("/query")
    @SaCheckPermission("bot:instance:view")
    public ApiResponse<List<BotChannelInstanceVO>> instances(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotChannelViewAssembler.toBotChannelInstanceVO(service.instances()));
    }

    @PostMapping("/onebot")
    @SaCheckPermission("bot:instance:create")
    public ApiResponse<Long> createOneBot(
            @Valid
            @RequestBody
            OneBotInstanceSaveDTO request) {
        return ApiResponse.ok(service.createOneBot(request));
    }

    @PutMapping("/onebot/{id}")
    @SaCheckPermission("bot:instance:update")
    public ApiResponse<Void> updateOneBot(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            OneBotInstanceSaveDTO request) {
        service.updateOneBot(id, request);
        return ApiResponse.ok();
    }

    @PostMapping("/qq-official")
    @SaCheckPermission("bot:instance:create")
    public ApiResponse<Long> createQqOfficial(
            @Valid
            @RequestBody
            QqOfficialInstanceSaveDTO request) {
        return ApiResponse.ok(service.createQqOfficial(request));
    }

    @PutMapping("/qq-official/{id}")
    @SaCheckPermission("bot:instance:update")
    public ApiResponse<Void> updateQqOfficial(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            QqOfficialInstanceSaveDTO request) {
        service.updateQqOfficial(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("bot:instance:delete")
    public ApiResponse<Void> delete(
            @PathVariable
            long id) {
        service.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/start")
    @SaCheckPermission("bot:instance:operate")
    public ApiResponse<Void> start(
            @PathVariable
            long id) {
        service.start(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/stop")
    @SaCheckPermission("bot:instance:operate")
    public ApiResponse<Void> stop(
            @PathVariable
            long id) {
        service.stop(id);
        return ApiResponse.ok();
    }

    @PostMapping("/onebot/probe")
    @SaCheckPermission("bot:instance:probe")
    public ApiResponse<OneBotAccountProbeVO> probeOneBot(
            @Valid
            @RequestBody
            OneBotAccountProbeQuery request) {
        return ApiResponse.ok(AdminBotChannelViewAssembler.toOneBotAccountProbeVO(service.probeOneBot(request)));
    }
}
