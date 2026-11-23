package com.nnm.bot.warframe.runtime.constant;

import java.time.Instant;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 已采纳 Vendor 清单中可人工核对四日元素加成的商人。 */
@Getter
@RequiredArgsConstructor
public enum WarframeWeaponVendor {
    // 官方 38.5 公告明确该日进入 B 批；Vendor 清单中 B 批是 bin 0。
    CODA("埃莉诺 · 终幕", "/Lotus/Types/Game/VendorManifests/TheHex/InfestedLichWeaponVendorManifest",
        Instant.parse("2025-03-23T00:00:00Z")),
    // 游戏 Wiki Reset 的信条计时器以此日 UTC 午夜作为四日周期起点。
    TENET("埃尔戈·格拉斯特 · 信条", "/Lotus/Types/Game/VendorManifests/Hubs/PerrinSequenceWeaponVendorManifest",
        Instant.parse("2015-12-03T00:00:00Z"));

    private final String label;
    private final String manifest;
    private final Instant anchor;

    /** @param code 前端商人代码；@return 商人或空。 */
    public static WarframeWeaponVendor find(String code) {
        return Arrays.stream(values()).filter(vendor -> vendor.name().equals(code)).findFirst().orElse(null);
    }
}
