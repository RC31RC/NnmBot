package com.nnm.bot.warframe.controller.persistent.curated.watch;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.watch.WarframeMemberSubscriptionVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeSubscriptionUpdateDTO;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeSubscriptionManagementService;
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
@RequestMapping("/api/admin/v1/warframe/groups/{groupId}/members/{memberId}/subscriptions")
public class WarframeSubscriptionController {
    @Resource
    private WarframeSubscriptionManagementService service;

    /**
     * 查询并返回当前条件下的业务列表。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<WarframeMemberSubscriptionVO>> list(
            @PathVariable
            long groupId,
            @PathVariable
            long memberId) {
        return ApiResponse.ok(service.list(groupId, memberId));
    }

    /**
     * 更新已有业务记录。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PutMapping("/{ruleId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> update(
            @PathVariable
            long groupId,
            @PathVariable
            long memberId,
            @PathVariable
            long ruleId,
            @Valid
            @RequestBody
            WarframeSubscriptionUpdateDTO request) {
        service.update(groupId, memberId, ruleId, request);
        return ApiResponse.ok();
    }

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @DeleteMapping("/{ruleId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> delete(
            @PathVariable
            long groupId,
            @PathVariable
            long memberId,
            @PathVariable
            long ruleId) {
        service.delete(groupId, memberId, ruleId);
        return ApiResponse.ok();
    }
}
