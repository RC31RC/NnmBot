package com.nnm.bot.warframe.controller.runtime.worldstate;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateRawVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateManagementService;
import jakarta.annotation.Resource;
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
     * 查询最近成功快照的原始文本、字段目录与采集信息。
     *
     * @return 最近一次成功采集的原文及字段信息。
     */
    @PostMapping("/raw/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeWorldStateRawVO> worldStateRaw() {
        return ApiResponse.ok(worldStateManagementService.raw());
    }

}
