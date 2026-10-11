package com.nnm.bot.warframe.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Warframe 物品别名新增或修改参数。 */
public record WarframeItemAliasSaveRequest(
        @NotNull
        @Positive
        Long itemId,
        @NotBlank
        @Size(max = 200)
        String aliasName,
        @NotBlank
        @Size(max = 40)
        String sourceType
) {
}
