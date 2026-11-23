package com.nnm.bot.warframe.controller.runtime.worldstate;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateFieldInspectionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.DescendiaRewardSelectionDTO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeDescendiaService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理端动态世界状态的 HTTP 入口。 */
@RestController
@RequestMapping("/api/admin/warframe/world-state")
public class WarframeWorldStateManagementController {

    @Resource
    private WarframeWorldStateManagementService worldStateManagementService;

    /**
     * 查询当前已解析动态数据的管理端视图。
     *
     * @return 当前世界状态各分区及刷新状态的管理视图。
     */
    @PostMapping("/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeWorldStateManagementVO> worldState() {
        return ApiResponse.ok(worldStateManagementService.current());
    }

    /**
     * 查询当前快照的协议模型、业务模型和字段级待处理解析状态。
     *
     * @return 用于管理端展开检查的 Java 字段模型。
     */
    @PostMapping("/fields/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeWorldStateFieldInspectionVO> fields() {
        return ApiResponse.ok(worldStateManagementService.fields());
    }

    @Resource
    private WarframeDescendiaService descendiaService;

    /**
     * @param selection 当前周的楼层奖励确认。
     * @return 保存成功响应。
     */
    @PostMapping("/descendia/reward")
    @SaCheckPermission("warframe:world:refresh")
    public ApiResponse<Void> confirmReward(@RequestBody DescendiaRewardSelectionDTO selection) {
        descendiaService.confirm(selection);
        return ApiResponse.ok(null);
    }
}
