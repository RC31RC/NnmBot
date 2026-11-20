package com.nnm.bot.warframe.runtime.pojo.dto.kuva;

import java.time.Instant;

/** 独立赤毒任务快照中的任务展示行。 */
public record KuvaMissionDTO(String node, String nodeReference, String missionType, Instant activation,
    Instant expiry) {
}
