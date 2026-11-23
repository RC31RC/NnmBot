package com.nnm.bot.warframe.runtime.rawdata.invasion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Oracle 当轮入侵增量协议。核对秒级时段、官方入侵身份、双方阵营和奖励后，用于补全双方任务类型。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OracleInvasionsRawData(Long activation, Long expiry, List<Invasion> invasions) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Invasion(String id, String node, String ally, List<Reward> allyPay, List<String> missions) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Reward(String ItemType, Integer ItemCount) {
    }
}
