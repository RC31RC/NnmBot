package com.nnm.bot.warframe.runtime.rawdata.bounty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/** 补充赏金接口的原始响应；轮次判断与静态引用解析由业务服务完成。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BountyCycleRawData {
    /** 当前轮次的结束时间，单位为 Unix 毫秒。 */
    @JsonProperty("expiry")
    private Long expiry;

    /** 各集团当前轮次的任务列表。 */
    @JsonProperty("bounties")
    private BountiesRawData bounties;

    /** 接口实际提供的三个集团分区。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BountiesRawData {
        /** 六人组当前轮次的赏金任务。 */
        @JsonProperty("HexSyndicate")
        private List<JobRawData> hex;

        /** 扎里曼当前轮次的赏金任务。 */
        @JsonProperty("ZarimanSyndicate")
        private List<JobRawData> zariman;

        /** 英择谛研究所当前轮次的赏金任务。 */
        @JsonProperty("EntratiLabSyndicate")
        private List<JobRawData> entratiLab;
    }

    /** 尚未关联静态名称的原始任务。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JobRawData {
        /** 任务节点的上游标识。 */
        @JsonProperty("node")
        private String node;

        /** 任务附加挑战的上游标识。 */
        @JsonProperty("challenge")
        private String challenge;

        /** 友军代理的上游标识，未提供时为空。 */
        @JsonProperty("ally")
        private String ally;
    }
}
