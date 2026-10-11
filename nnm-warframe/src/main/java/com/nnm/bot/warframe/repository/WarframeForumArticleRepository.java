package com.nnm.bot.warframe.repository;

import java.util.Optional;
import com.nnm.bot.warframe.model.entity.WarframeForumArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WarframeForumArticleRepository
        extends JpaRepository<WarframeForumArticle, Long>,
        JpaSpecificationExecutor<WarframeForumArticle> {

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    List<WarframeForumArticle> findAllByIdNot(Long id);

    List<WarframeForumArticle> findAllByEnabledTrue();

    Optional<WarframeForumArticle> findFirstByOrderByIdDesc();
}
