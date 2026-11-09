package com.nnm.bot.warframe.controller.runtime;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.runtime.pojo.query.arbitration.WarframeArbitrationQuery;
import com.nnm.bot.warframe.runtime.pojo.query.riven.WarframeRivenWeeklyQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.arbitration.WarframeArbitrationManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyManagementVO;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 世界数据菜单中独立动态来源的只读查询入口。 */
@RestController
@RequestMapping("/api/admin/warframe/runtime")
public class WarframeRuntimeDataController {
    @Resource
    private WarframeArbitrationService arbitrationService;
    @Resource
    private WarframeRivenWeeklyService weeklyService;

    /**
     * 查询本实例已解析的仲裁排期。
     *
     * @param query 筛选和分页条件。
     * @return 快照状态与排期列表。
     */
    @PostMapping("/arbitration/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeArbitrationManagementVO> arbitration(@RequestBody WarframeArbitrationQuery query) {
        return ApiResponse.ok(arbitrationService.page(query));
    }

    /**
     * 查询本实例已解析的紫卡周成交。
     *
     * @param query 武器、洗练状态和分页条件。
     * @return 快照状态与成交统计。
     */
    @PostMapping("/riven-weekly/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeRivenWeeklyManagementVO> weekly(@RequestBody WarframeRivenWeeklyQuery query) {
        return ApiResponse.ok(weeklyService.page(query));
    }
}
