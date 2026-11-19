package com.nnm.bot.warframe.persistent.curated.repository.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.SteelPathRotationRowDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate.WarframeCommunitySteelPathRotation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 按人工审核的轮换次序读取八个商品身份，不复制官方价格和图片。 */
public interface WarframeCommunitySteelPathRotationRepository
    extends JpaRepository<WarframeCommunitySteelPathRotation, Long> {
    List<SteelPathRotationRowDTO> findAllProjectedByOrderByRotationIndexAsc();
}
