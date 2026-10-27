package com.nnm.bot.warframe.runtime.rawdata.arbitration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/** 仲裁排期 JSON 协议；文本排期仍按实际文本格式处理。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbitrationScheduleRawData {
    /** 按时间排列的仲裁排期记录。 */
    @JsonProperty("entries")
    private List<EntryRawData> entries;

    /** 仲裁源提供的一条排期。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EntryRawData {
        /** 排期的上游唯一标识。 */
        @JsonProperty("id")
        private String id;

        /** 排期开始时间的标准时间字符串。 */
        @JsonProperty("activation")
        private String activation;

        /** 排期结束时间的标准时间字符串。 */
        @JsonProperty("expiry")
        private String expiry;

        /** 任务节点名称。 */
        @JsonProperty("node")
        private String node;

        /** 任务节点中文名称。 */
        @JsonProperty("nodeZh")
        private String nodeZh;

        /** 任务类型名称。 */
        @JsonProperty("missionType")
        private String missionType;

        /** 任务类型中文名称。 */
        @JsonProperty("missionTypeZh")
        private String missionTypeZh;

        /** 敌方阵营名称。 */
        @JsonProperty("faction")
        private String faction;

        /** 敌方阵营中文名称。 */
        @JsonProperty("factionZh")
        private String factionZh;

        /** 所属星球名称。 */
        @JsonProperty("planet")
        private String planet;

        /** 所属星球中文名称。 */
        @JsonProperty("planetZh")
        private String planetZh;

        /** 源提供的附加排期边界，仅作原文审计，不参与业务字段解析。 */
        @JsonProperty("bounds")
        private Map<String, Object> bounds;

    }

}
