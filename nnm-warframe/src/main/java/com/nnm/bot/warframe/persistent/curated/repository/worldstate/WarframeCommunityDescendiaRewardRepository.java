package com.nnm.bot.warframe.persistent.curated.repository.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardRowDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate.WarframeCommunityDescendiaReward;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 数据库人工维护的奖池按楼层批量读取。 */
public interface WarframeCommunityDescendiaRewardRepository
    extends JpaRepository<WarframeCommunityDescendiaReward, Long> {
    List<DescendiaRewardRowDTO> findAllProjectedByOrderByFloorNumberAsc();
}
