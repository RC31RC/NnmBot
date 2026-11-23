package com.nnm.bot.warframe.controller.runtime.vendor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.runtime.pojo.dto.vendor.WarframeVendorObservationDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.vendor.WarframeWeaponVendorVO;
import com.nnm.bot.warframe.runtime.service.vendor.WarframeVendorRotationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 玄骸商人当前轮换与人工确认的管理接口。 */
@RestController
@RequestMapping("/api/admin/warframe/runtime/weapon-vendors")
public class WarframeWeaponVendorController {
    @Resource
    private WarframeVendorRotationService vendors;

    /** @return 当前四日轮换及确认的属性。 */
    @PostMapping("/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeWeaponVendorVO> query() {
        return ApiResponse.ok(vendors.weapons());
    }

    /**
     * @param observation 当前轮次武器的元素与数值。
     * @return 保存后的当前轮换。
     */
    @PostMapping("/observation")
    @SaCheckPermission("warframe:world:refresh")
    public ApiResponse<WarframeWeaponVendorVO> observe(@RequestBody WarframeVendorObservationDTO observation) {
        return ApiResponse.ok(vendors.observe(observation));
    }
}
