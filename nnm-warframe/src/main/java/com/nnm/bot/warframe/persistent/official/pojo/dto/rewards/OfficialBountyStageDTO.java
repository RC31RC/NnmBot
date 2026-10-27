package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialBountyStageDeserializer;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialBountyStageSerializer;
import java.util.List;

/** 赏金阶段的两种明确协议形式：奖励池对象，或旧阶段引用字符串列表。 */
@JsonDeserialize(using = OfficialBountyStageDeserializer.class)
@JsonSerialize(using = OfficialBountyStageSerializer.class)
public record OfficialBountyStageDTO(OfficialBountyDTO.RewardPoolDTO rewardPool, List<String> references) {
}
