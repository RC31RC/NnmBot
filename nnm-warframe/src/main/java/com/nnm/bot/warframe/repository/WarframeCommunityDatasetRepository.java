package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.domain.entity.WarframeCommunityDatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 社区数据集仓储的统一标记接口，便于服务层按数据集路由仓储。
 */
public interface WarframeCommunityDatasetRepository<T extends WarframeCommunityDatasetRecord>
        extends JpaRepository<T, Long> {

    List<T> findAllByEnabledTrue();
}
