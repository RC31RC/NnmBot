package com.nnm.bot.warframe.controller.runtime;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.runtime.pojo.query.arbitration.WarframeArbitrationQuery;
import com.nnm.bot.warframe.runtime.pojo.query.riven.WarframeRivenWeeklyQuery;
import com.nnm.bot.warframe.runtime.pojo.query.kuva.WarframeKuvaMissionQuery;
import com.nnm.bot.warframe.runtime.pojo.query.steelincursion.WarframeSteelIncursionQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.arbitration.WarframeArbitrationManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.kuva.WarframeKuvaMissionManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.steelincursion.WarframeSteelIncursionManagementVO;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import com.nnm.bot.warframe.runtime.service.kuva.WarframeKuvaMissionService;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import com.nnm.bot.warframe.runtime.service.steelincursion.WarframeSteelIncursionService;
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
    @Resource
    private WarframeKuvaMissionService kuvaMissionService;
    @Resource
    private WarframeSteelIncursionService steelIncursionService;

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
     * 查询独立赤毒任务快照。
     *
     * @param query 关键词和分页条件。
     * @return 采集状态及当前任务。
     */
    @PostMapping("/kuva-missions/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeKuvaMissionManagementVO> kuvaMissions(@RequestBody WarframeKuvaMissionQuery query) {
        return ApiResponse.ok(kuvaMissionService.page(query));
    }

    /**
     * 查询独立钢铁侵袭排期快照。
     *
     * @param query 时间范围、关键词和分页条件。
     * @return 采集状态及每日侵袭节点。
     */
    @PostMapping("/steel-incursions/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeSteelIncursionManagementVO>
        steelIncursions(@RequestBody WarframeSteelIncursionQuery query) {
        return ApiResponse.ok(steelIncursionService.page(query));
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
