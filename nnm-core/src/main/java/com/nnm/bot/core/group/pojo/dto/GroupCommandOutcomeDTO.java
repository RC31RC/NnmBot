package com.nnm.bot.core.group.pojo.dto;

import com.nnm.bot.core.group.service.PointBalance;
import java.util.Objects;

/** 群组命令的业务结果，不含渲染或渠道消息模型。 */
public record GroupCommandOutcomeDTO(String text, PointBalance balance, boolean rejected) {
    public GroupCommandOutcomeDTO {
        Objects.requireNonNull(text, "text");
    }

    public static GroupCommandOutcomeDTO rejected(String text) {
        return new GroupCommandOutcomeDTO(text, null, true);
    }

    public static GroupCommandOutcomeDTO accepted(String text) {
        return new GroupCommandOutcomeDTO(text, null, false);
    }

    public static GroupCommandOutcomeDTO withBalance(String text, PointBalance balance) {
        return new GroupCommandOutcomeDTO(text, Objects.requireNonNull(balance, "balance"), false);
    }
}
