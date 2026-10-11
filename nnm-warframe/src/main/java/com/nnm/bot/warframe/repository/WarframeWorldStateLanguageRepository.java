package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeWorldStateLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** 读取 wf_community_worldstate_language 的启用词条，供代码簿构建大小写无关的引用索引。 */
public interface WarframeWorldStateLanguageRepository extends JpaRepository<WarframeWorldStateLanguage, Long> {
    List<WarframeWorldStateLanguage> findAllByEnabledTrue();
}
