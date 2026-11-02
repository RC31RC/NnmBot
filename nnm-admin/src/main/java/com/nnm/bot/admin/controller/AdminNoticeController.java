package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.dto.system.notification.AdminNoticePublishDTO;
import com.nnm.bot.admin.pojo.query.system.notification.AdminNoticeQuery;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.admin.pojo.vo.system.notification.AdminNoticeVO;
import com.nnm.bot.admin.service.system.notification.AdminNoticeService;
import com.nnm.bot.common.response.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理员个人通知查询与超级管理员公告发布。 */
@RestController
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {
    @Resource
    private AdminNoticeService notices;

    @PostMapping("/list")
    @SaCheckPermission("system:notification:view")
    public ApiResponse<List<AdminNoticeVO>> list() {
        return ApiResponse.ok(notices.list(StpUtil.getLoginIdAsLong()));
    }

    @PostMapping("/history")
    @SaCheckPermission("system:notification:view")
    public ApiResponse<PageResult<AdminNoticeVO>> history(@RequestBody AdminNoticeQuery query) {
        return ApiResponse.ok(notices.history(StpUtil.getLoginIdAsLong(), query.current() == null ? 1 : query.current(),
            query.size() == null ? 20 : query.size()));
    }

    @PostMapping("/publish")
    @SaCheckPermission("system:notification:publish")
    @SaCheckRole("R_SUPER")
    public ApiResponse<Void> publish(@Valid @RequestBody AdminNoticePublishDTO request) {
        notices.publish(StpUtil.getLoginIdAsLong(), request);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/dismiss")
    @SaCheckPermission("system:notification:view")
    public ApiResponse<Void> dismiss(@PathVariable String id) {
        notices.dismiss(StpUtil.getLoginIdAsLong(), id);
        return ApiResponse.ok();
    }

    @PostMapping("/dismiss-all")
    @SaCheckPermission("system:notification:view")
    public ApiResponse<Void> dismissAll() {
        notices.dismissAll(StpUtil.getLoginIdAsLong());
        return ApiResponse.ok();
    }
}
