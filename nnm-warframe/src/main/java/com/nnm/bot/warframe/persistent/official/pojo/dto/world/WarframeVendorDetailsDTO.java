package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import java.math.BigDecimal;

public sealed interface WarframeVendorDetailsDTO {
    record AmountDTO(Long fixed, Long minimum, Long maximum, Long step) {
        /**
         * 生成当前数据对应的文本内容。
         *
         * @return 按当前对象字段组成的文本内容。
         */
        public String text() {
            if (fixed != null) return fixed.toString();
            if (minimum == null || maximum == null) return "未提供";
            return minimum + " - " + maximum + (step == null ? "" : "，步长 " + step);
        }
    }

    record VendorDTO(Boolean dynamic, Boolean oneBinPerCycle, Long numItemsMin, Long numItemsMax,
            String numItemsPerBinJson, String randomSeedType, String requiredGoalTag)
            implements WarframeVendorDetailsDTO {
    }

    record ItemDTO(Long parentId, String parentBkey, String vendorBkey, Integer recordOrder, String storeItem,
            Long quantity, Boolean alwaysOffered, Integer binNumber, Integer duplicates, BigDecimal probability,
            Integer numRandomItemPrices, Long purchaseLimit, Boolean rotatedWeekly, AmountDTO durationHours,
            AmountDTO credits, AmountDTO platinum, String syndicateTag, Integer syndicateMinRank, Long syndicateStandingCost,
            BigDecimal reductionPerPositiveRank, BigDecimal increasePerNegativeRank, String focusPolarity, Long focusCost)
            implements WarframeVendorDetailsDTO {
    }

    record PriceDTO(Long parentId, String parentBkey, String vendorBkey, Integer recordOrder, String itemType,
            Long itemCount) implements WarframeVendorDetailsDTO {
    }

    record RandomPriceDTO(Long parentId, String parentBkey, String vendorBkey, Integer recordOrder, Integer binNumber,
            String itemType, Long countMin, Long countMax) implements WarframeVendorDetailsDTO {
    }
}
