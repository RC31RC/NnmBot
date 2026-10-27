package com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** 正式属性定义来自 Nnm；人工别名和组合由纯自维护目录另行提供。 */
public record WarframeRivenAttributeDTO(String code, String effect, String effectEn, String attributeGroup,
        Boolean negativeOnly, Boolean positiveIsNegative, String prefix, Boolean searchOnly,
        String suffix, String units, Set<String> exclusiveTo, List<ParameterDTO> parameters) {
    public record ParameterDTO(String rivenUniqueName, String tag, BigDecimal value, String localization,
                            String description, Boolean reverseValueSymbol, String semantics) {
    }
}
