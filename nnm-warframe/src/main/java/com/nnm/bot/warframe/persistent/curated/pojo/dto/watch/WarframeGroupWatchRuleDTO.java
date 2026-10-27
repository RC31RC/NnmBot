package com.nnm.bot.warframe.persistent.curated.pojo.dto.watch;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.watch.WarframeGroupWatchRule;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WarframeGroupWatchRuleDTO {
    private final Long id;
    private final Long groupId;
    private final Long memberId;
    private final String watchType;
    private final String watchKey;
    private final String watchValue;
    private final boolean enabled;
    private final boolean notifyEnabled;
    private final String matchedEventsJson;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param rule 规则。
     * @return 游戏资料分组订阅监控规则对象。
     */
    public static WarframeGroupWatchRuleDTO from(WarframeGroupWatchRule rule) {
        return new WarframeGroupWatchRuleDTO(rule.getId(), rule.getGroupId(), rule.getMemberId(), rule.getWatchType(), rule.getWatchKey(), rule.getWatchValue(), rule.isEnabled(), rule.isNotifyEnabled(), rule.getMatchedEventsJson(), rule.getCreatedAt(), rule.getUpdatedAt());
    }
}
