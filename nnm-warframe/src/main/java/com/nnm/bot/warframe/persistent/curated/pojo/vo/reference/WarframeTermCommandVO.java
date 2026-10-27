package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import java.util.List;

/** 人工维护术语查询的展示协议。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeTermCommandVO(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("资料")
        List<TermVO> items,
        @JsonPropertyDescription("提示")
        String notice) {

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record TermVO(
            @JsonPropertyDescription("中文")
            String chineseText,
            @JsonPropertyDescription("英文")
            String englishText) {
        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param details 详情。
         * @return 单条术语及解释的展示内容。
         */
        public static TermVO from(WarframeCommunityRecordDTO.TermDTO details) {
            return new TermVO(details.chineseText(), details.englishText());
        }
    }
}
