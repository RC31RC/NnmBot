package com.nnm.bot.warframe.runtime.rawdata.kuva;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 10o.io/kuvalog.json 外部任务日志条目，时间为 ISO-8601，非官方 PHP 数据。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KuvaLogRawData(String start, String end, String missiontype, String solnode, SolNodeRawData solnodedata) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SolNodeRawData(String type) {
    }
}
