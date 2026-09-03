package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.application.WarframeForumArticleService;
import com.nnm.bot.warframe.application.WarframeForumFileService;
import com.nnm.bot.warframe.application.WarframeForumOperator;
import com.nnm.bot.warframe.application.WarframeForumUserAuthorityProvider;
import com.nnm.bot.warframe.config.properties.WarframeForumProperties;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.domain.entity.WarframeForumArticle;
import com.nnm.bot.warframe.dto.WarframeForumArticleIdRequest;
import com.nnm.bot.warframe.dto.WarframeForumArticleItem;
import com.nnm.bot.warframe.dto.WarframeForumArticlePublicItem;
import com.nnm.bot.warframe.dto.WarframeForumArticleQueryRequest;
import com.nnm.bot.warframe.dto.WarframeForumArticleSaveRequest;
import com.nnm.bot.warframe.dto.WarframeForumArticleUpdateRequest;
import com.nnm.bot.warframe.repository.WarframeForumArticleRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WarframeForumArticleServiceImpl implements WarframeForumArticleService {

    private static final int MAX_PAGE_SIZE = 100;

    @Resource
    private WarframeForumArticleRepository repository;

    @Resource
    private WarframeForumFileService fileService;

    @Resource
    private WarframeForumUserAuthorityProvider authorityProvider;

    @Resource
    private Snowflake warframeForumSnowflake;

    @Resource
    private WarframeForumProperties forumProperties;

    @Override
    @Transactional(readOnly = true)
    public PageResult<WarframeForumArticleItem> query(
            WarframeForumArticleQueryRequest request,
            WarframeForumOperator operator) {
        int current = normalizeCurrent(request.current());
        int size = normalizeSize(request.size());
        PageRequest pageable = PageRequest.of(
                current - 1,
                size,
                Sort.by(
                        Sort.Order.asc("sortOrder"),
                        Sort.Order.desc("updatedAt"),
                        Sort.Order.asc("id")));
        Page<WarframeForumArticle> page = repository.findAll(specification(request), pageable);
        List<WarframeForumArticleItem> records = toItems(page.getContent(), operator);
        return PageResult.of(records, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarframeForumArticleItem> searchEnabled(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return List.of();
        }
        WarframeForumArticleQueryRequest request =
                new WarframeForumArticleQueryRequest(1, 10, keyword.trim(), null, true);
        return repository.findAll(
                        specification(request),
                        PageRequest.of(
                                0,
                                10,
                                Sort.by(
                                        Sort.Order.asc("sortOrder"),
                                        Sort.Order.asc("id"))))
                .getContent()
                .stream()
                .map(WarframeForumArticleItem::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WarframeForumArticleItem detail(
            WarframeForumArticleIdRequest request,
            WarframeForumOperator operator) {
        WarframeForumArticle article = findArticle(request.id());
        return toItem(article, operator, assignedLevel(article.getCreatedByUserId()));
    }

    @Override
    @Transactional(readOnly = true)
    public WarframeForumArticlePublicItem publicDetail(long id) {
        WarframeForumArticle article = findArticle(id);
        BusinessAssert.isTrue(
                article.isEnabled(),
                CommonErrorCode.DATA_NOT_FOUND,
                "论坛文章不存在");
        return WarframeForumArticlePublicItem.from(article);
    }

    @Override
    @Transactional
    public long create(
            WarframeForumArticleSaveRequest request,
            WarframeForumOperator operator) {
        String slug = requiredTrimmed(request.slug(), "文章标识不能为空");
        ensureSlugAvailable(slug, null);
        WarframeForumFileService.PreparedContent content =
                fileService.promoteReferencedFiles(
                        request.coverUrl(),
                        request.contentHtml(),
                        request.contentMarkdown());
        WarframeForumArticle article = new WarframeForumArticle();
        long articleId = warframeForumSnowflake.nextId();
        article.setId(articleId);
        article.setPageUrl(forumProperties.publicArticleUri(articleId));
        apply(article, request);
        applyPreparedContent(article, content);
        article.setSlug(slug);
        article.setCreatedByUserId(operator.userId());
        return save(article).getId();
    }

    @Override
    @Transactional
    public void update(
            WarframeForumArticleUpdateRequest request,
            WarframeForumOperator operator) {
        WarframeForumArticle article = findArticle(request.id());
        checkCanUpdate(article, operator);
        String slug = requiredTrimmed(request.slug(), "文章标识不能为空");
        ensureSlugAvailable(slug, request.id());
        WarframeForumFileService.PreparedContent content =
                fileService.promoteReferencedFiles(
                        request.coverUrl(),
                        request.contentHtml(),
                        request.contentMarkdown());
        apply(article, request);
        applyPreparedContent(article, content);
        article.setSlug(slug);
        article.setPageUrl(forumProperties.publicArticleUri(article.getId()));
        save(article);
    }

    @Override
    @Transactional
    public void delete(
            WarframeForumArticleIdRequest request,
            WarframeForumOperator operator) {
        WarframeForumArticle article = findArticle(request.id());
        checkCanDelete(article, operator);
        Set<String> filesToDelete = new LinkedHashSet<>(fileService.referencedPermanentFiles(
                article.getCoverUrl(),
                article.getContentHtml(),
                article.getContentMarkdown()));
        if (!filesToDelete.isEmpty()) {
            for (WarframeForumArticle otherArticle : repository.findAllByIdNot(article.getId())) {
                filesToDelete.removeAll(fileService.referencedPermanentFiles(
                        otherArticle.getCoverUrl(),
                        otherArticle.getContentHtml(),
                        otherArticle.getContentMarkdown()));
                if (filesToDelete.isEmpty()) {
                    break;
                }
            }
        }
        repository.delete(article);
        repository.flush();
        fileService.deletePermanentFilesAfterCommit(filesToDelete);
    }

    private List<WarframeForumArticleItem> toItems(
            Collection<WarframeForumArticle> articles,
            WarframeForumOperator operator) {
        Set<Long> ownerIds = articles.stream()
                .map(WarframeForumArticle::getCreatedByUserId)
                .collect(Collectors.toSet());
        Map<Long, Integer> levels = authorityProvider.assignedLevels(ownerIds);
        return articles.stream()
                .map(article -> toItem(
                        article,
                        operator,
                        levels.getOrDefault(
                                article.getCreatedByUserId(),
                                Integer.MAX_VALUE)))
                .toList();
    }

    private static WarframeForumArticleItem toItem(
            WarframeForumArticle article,
            WarframeForumOperator operator,
            int ownerAuthorityLevel) {
        return WarframeForumArticleItem.from(article, operator, ownerAuthorityLevel);
    }

    private Specification<WarframeForumArticle> specification(
            WarframeForumArticleQueryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.enabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.enabled()));
            }
            if (StrUtil.isNotBlank(request.category())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("category")),
                        likePattern(request.category())));
            }
            if (StrUtil.isNotBlank(request.keyword())) {
                String pattern = likePattern(request.keyword());
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("summary")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tags")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), pattern)));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String likePattern(String value) {
        String escaped = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    private WarframeForumArticle findArticle(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "论坛文章不存在"));
    }

    private void ensureSlugAvailable(String slug, Long id) {
        boolean duplicated = id == null
                ? repository.existsBySlugIgnoreCase(slug)
                : repository.existsBySlugIgnoreCaseAndIdNot(slug, id);
        BusinessAssert.isFalse(duplicated, CommonErrorCode.DATA_CONFLICT, "论坛文章 slug 已存在");
    }

    private void checkCanUpdate(
            WarframeForumArticle article,
            WarframeForumOperator operator) {
        BusinessAssert.isTrue(
                operator.canUpdate(
                        article.getCreatedByUserId(),
                        assignedLevel(article.getCreatedByUserId())),
                WarframeManagementErrorCode.FORUM_ARTICLE_OPERATION_DENIED);
    }

    private void checkCanDelete(
            WarframeForumArticle article,
            WarframeForumOperator operator) {
        BusinessAssert.isTrue(
                operator.canDelete(
                        article.getCreatedByUserId(),
                        assignedLevel(article.getCreatedByUserId())),
                WarframeManagementErrorCode.FORUM_ARTICLE_OPERATION_DENIED);
    }

    private int assignedLevel(long userId) {
        return authorityProvider.assignedLevels(Set.of(userId))
                .getOrDefault(userId, Integer.MAX_VALUE);
    }

    private WarframeForumArticle save(WarframeForumArticle article) {
        try {
            return repository.saveAndFlush(article);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(
                    CommonErrorCode.DATA_CONFLICT,
                    "论坛文章数据冲突",
                    exception);
        }
    }

    private static void apply(
            WarframeForumArticle article,
            WarframeForumArticleSaveRequest request) {
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setTags(request.tags());
        article.setCategory(request.category());
        article.setCoverUrl(request.coverUrl());
        article.setContentHtml(request.contentHtml());
        article.setContentMarkdown(request.contentMarkdown());
        article.setEnabled(request.enabled());
        article.setSortOrder(request.sortOrder());
    }

    private static void apply(
            WarframeForumArticle article,
            WarframeForumArticleUpdateRequest request) {
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setTags(request.tags());
        article.setCategory(request.category());
        article.setCoverUrl(request.coverUrl());
        article.setContentHtml(request.contentHtml());
        article.setContentMarkdown(request.contentMarkdown());
        article.setEnabled(request.enabled());
        article.setSortOrder(request.sortOrder());
    }

    private static void applyPreparedContent(
            WarframeForumArticle article,
            WarframeForumFileService.PreparedContent content) {
        article.setCoverUrl(content.coverUrl());
        article.setContentHtml(content.contentHtml());
        article.setContentMarkdown(content.contentMarkdown());
    }

    private static String requiredTrimmed(String value, String message) {
        return BusinessAssert.notBlank(value, CommonErrorCode.PARAM_INVALID, message).trim();
    }

    private static int normalizeCurrent(Integer current) {
        return current == null ? 1 : Math.max(current, 1);
    }

    private static int normalizeSize(Integer size) {
        return size == null ? 20 : Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }
}
