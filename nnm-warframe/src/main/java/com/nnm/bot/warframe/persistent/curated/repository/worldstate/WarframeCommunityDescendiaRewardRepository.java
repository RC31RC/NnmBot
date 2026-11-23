package com.nnm.bot.warframe.persistent.curated.repository.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardRowDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate.WarframeCommunityDescendiaReward;
import com.nnm.bot.warframe.persistent.curated.repository.reference.CommunitySpecifications;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** 数据库人工维护的奖池按楼层批量读取。 */
public interface WarframeCommunityDescendiaRewardRepository extends
    JpaRepository<WarframeCommunityDescendiaReward, Long>, JpaSpecificationExecutor<WarframeCommunityDescendiaReward> {
    List<DescendiaRewardRowDTO> findAllProjectedByOrderByFloorNumberAsc();

    List<DescendiaRewardRowDTO> findAllProjectedByRecordKeyIn(Collection<String> keys);

    List<WarframeCommunityDescendiaReward> findAllByRecordKeyIn(Collection<String> keys);

    default Page<DescendiaRewardRowDTO> query(String keyword, Pageable page) {
        return findBy(
            CommunitySpecifications.contains(keyword,
                List.of("recordKey", "label", "officialPoolKey", "expectedPoolName", "difficulty")),
            query -> query.as(DescendiaRewardRowDTO.class).page(page));
    }
}
