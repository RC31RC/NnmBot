package com.nnm.bot.warframe.persistent.official.pojo.vo.reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeTranslationDetailsDTO;
import java.util.List;

/** 官方语言字典查询的展示协议。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeTranslationCommandVO(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("资料")
        List<TranslationVO> items,
        @JsonPropertyDescription("提示")
        String notice) {

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record TranslationVO(
            @JsonPropertyDescription("完整键")
            String recordKey,
            @JsonPropertyDescription("语言")
            String locale,
            @JsonPropertyDescription("翻译")
            String displayName) {
        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param recordKey 记录键。
         * @param details 详情。
         * @return 单条译名的展示内容。
         */
        public static TranslationVO from(String recordKey, WarframeTranslationDetailsDTO details) {
            return new TranslationVO(recordKey, details.locale(), details.displayName());
        }
    }
}
