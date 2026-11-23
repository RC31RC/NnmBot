package com.nnm.bot.warframe.runtime.pojo.vo.vendor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** 四日轮换时间、可选元素和按商人组织的武器资料。 */
public record WarframeWeaponVendorVO(List<ElementVO> elements, List<VendorVO> vendors) {
    /** 可填写的元素代码和中文标签。 */
    public record ElementVO(String code, String label) {
    }

    /** 当前批次及官方 Vendor 中的武器。 */
    public record VendorVO(String code, String label, String round, Instant expiry, String batch,
        List<WeaponVO> weapons) {
    }

    /** 商人商品及可选的本轮人工核对结果。 */
    public record WeaponVO(String key, String name, String imageUrl, String price, String element,
        BigDecimal bonusPercent) {
    }
}
