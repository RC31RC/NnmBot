package com.nnm.bot.warframe.runtime.pojo.dto.vendor;

import java.math.BigDecimal;

/** 某商人本轮武器的确认值；元素和数值均为空时清除。 */
public record WarframeVendorObservationDTO(String vendor, String round, String weaponKey, String element,
    BigDecimal bonusPercent) {
}
