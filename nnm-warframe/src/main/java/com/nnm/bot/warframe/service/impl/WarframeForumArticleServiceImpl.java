package com.nnm.bot.warframe.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
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
import com.nnm.bot.warframe.model.entity.WarframeForumArticle;
import com.nnm.bot.warframe.pojo.WarframeForumArticleIdRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleItem;
import com.nnm.bot.warframe.pojo.WarframeForumArticleOptions;
import com.nnm.bot.warframe.pojo.WarframeForumArticlePublicItem;
import com.nnm.bot.warframe.pojo.WarframeForumArticleQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleSaveRequest;
import com.nnm.bot.warframe.pojo.WarframeForumArticleSearchQuery;
import com.nnm.bot.warframe.pojo.WarframeForumArticleUpdateRequest;
import com.nnm.bot.warframe.repository.WarframeForumArticleRepository;
import com.nnm.bot.warframe.service.DistributedForumIds;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.ObjectProvider;
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
    private ObjectProvider<DistributedForumIds> distributedIds;

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
    public WarframeForumArticleOptions options() {
        List<WarframeForumArticle> articles = repository.findAll();
        Set<String> categories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (WarframeForumArticle article : articles) {
            if (StrUtil.isNotBlank(article.getCategory())) {
                categories.add(article.getCategory().trim());
            }
            splitTags(article.getTags()).forEach(tags::add);
        }
        return new WarframeForumArticleOptions(
                List.copyOf(categories),
                List.copyOf(tags),
                articles.isEmpty());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarframeForumArticleItem> searchEnabled(WarframeForumArticleSearchQuery query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return repository.findAllByEnabledTrue().stream()
                .map(article -> new ScoredArticle(article, score(article, query)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator
                        .comparingInt(ScoredArticle::score).reversed()
                        .thenComparing(scored -> scored.article().getSortOrder())
                        .thenComparing(scored -> scored.article().getId()))
                .limit(5)
                .map(ScoredArticle::article)
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
        ForumMetadata metadata = normalizeMetadata(request.category(), request.tags());
        WarframeForumFileService.PreparedContent content =
                fileService.promoteReferencedFiles(
                        request.coverUrl(),
                        request.contentHtml(),
                        request.contentMarkdown());
        WarframeForumArticle article = new WarframeForumArticle();
        var ids = distributedIds == null ? null : distributedIds.getIfAvailable();
        long articleId = ids == null ? warframeForumSnowflake.nextId() : ids.next();
        article.setId(articleId);
        article.setPageUrl(forumProperties.publicArticleUri(articleId));
        apply(article, request, metadata);
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
        ForumMetadata metadata = normalizeMetadata(request.category(), request.tags());
        WarframeForumFileService.PreparedContent content =
                fileService.promoteReferencedFiles(
                        request.coverUrl(),
                        request.contentHtml(),
                request.contentMarkdown());
        apply(article, request, metadata);
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
            for (String tag : request.tags()) {
                if (StrUtil.isBlank(tag)) {
                    continue;
                }
                // 历史表结构把标签保存为逗号字符串。统一分隔符并在首尾补逗号，
                // 让“p”只匹配完整标签“p”，不会误命中“prime”。
                Expression<String> normalizedTags = normalizedTags(root.get("tags"), criteriaBuilder);
                Expression<String> surroundedTags = criteriaBuilder.concat(
                        criteriaBuilder.concat(criteriaBuilder.literal(","), normalizedTags),
                        criteriaBuilder.literal(","));
                predicates.add(criteriaBuilder.like(
                        surroundedTags,
                        likePattern("," + normalizeTag(tag) + ",")));
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

    private static Expression<String> normalizedTags(
            Expression<String> tags,
            CriteriaBuilder criteriaBuilder) {
        Expression<String> normalized = criteriaBuilder.lower(
                criteriaBuilder.coalesce(tags, ""));
        normalized = criteriaBuilder.function(
                "replace",
                String.class,
                normalized,
                criteriaBuilder.literal("，"),
                criteriaBuilder.literal(","));
        normalized = criteriaBuilder.function(
                "replace",
                String.class,
                normalized,
                criteriaBuilder.literal("、"),
                criteriaBuilder.literal(","));
        return criteriaBuilder.function(
                "replace",
                String.class,
                normalized,
                criteriaBuilder.literal(" "),
                criteriaBuilder.literal(""));
    }

    private static String normalizeTag(String value) {
        return normalize(value)
                .replace(",", "")
                .replace("，", "")
                .replace("、", "");
    }

    private static int score(
            WarframeForumArticle article,
            WarframeForumArticleSearchQuery query) {
        String title = normalize(article.getTitle());
        String slug = normalize(article.getSlug());
        String category = normalize(article.getCategory());
        String summary = normalize(article.getSummary());
        List<String> tags = splitTags(article.getTags()).stream()
                .map(WarframeForumArticleServiceImpl::normalize)
                .toList();

        int score = 0;
        if (!query.title().isBlank()) {
            String requested = normalize(query.title());
            if (title.equals(requested)) {
                score += 100;
            } else if (title.contains(requested)) {
                score += 60;
            } else {
                return 0;
            }
        }
        if (!query.category().isBlank()) {
            String requested = normalize(query.category());
            if (category.equals(requested)) {
                score += 45;
            } else if (category.contains(requested)) {
                score += 25;
            } else {
                return 0;
            }
        }
        for (String requestedTag : query.tags()) {
            String normalizedTag = normalize(requestedTag);
            boolean matched = tags.stream().anyMatch(tag -> tag.equals(normalizedTag));
            if (!matched) {
                return 0;
            }
            score += 35;
        }
        for (String keyword : query.keywords()) {
            String requested = normalize(keyword);
            if (title.equals(requested)) {
                score += 80;
            } else if (title.contains(requested)) {
                score += 40;
            } else if (slug.contains(requested)) {
                score += 30;
            } else if (category.contains(requested)) {
                score += 25;
            } else if (tags.stream().anyMatch(tag -> tag.contains(requested))) {
                score += 25;
            } else if (summary.contains(requested)) {
                score += 10;
            } else {
                return 0;
            }
        }
        return score;
    }

    private static List<String> splitTags(String tags) {
        if (StrUtil.isBlank(tags)) {
            return List.of();
        }
        return Arrays.stream(tags.split("[,，、\\s]+"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    /**
     * 将分类和标签归一化为数据库中的标准值。
     *
     * <p>选项来自已有文章，因此管理端即使绕过下拉框直接调用接口，也不能写入
     * 一套新的分类或近似标签。数据库为空时保留初始化能力，允许第一篇文章建立
     * 初始分类和标签选项。</p>
     */
    private ForumMetadata normalizeMetadata(String category, String tags) {
        List<WarframeForumArticle> existingArticles = repository.findAll();
        boolean allowInitialization = existingArticles.isEmpty();
        Map<String, String> categoryOptions = existingArticles.stream()
                .map(WarframeForumArticle::getCategory)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toMap(
                        value -> normalize(value),
                        value -> value,
                        (first, ignored) -> first));
        Map<String, String> tagOptions = existingArticles.stream()
                .flatMap(article -> splitTags(article.getTags()).stream())
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toMap(
                        value -> normalize(value),
                        value -> value,
                        (first, ignored) -> first));

        String normalizedCategory = normalizeOption(
                category,
                categoryOptions,
                "分类",
                allowInitialization);
        List<String> normalizedTags = normalizeTagOptions(
                tags,
                tagOptions,
                allowInitialization);
        return new ForumMetadata(
                normalizedCategory,
                normalizedTags.isEmpty() ? null : String.join(", ", normalizedTags));
    }

    private static String normalizeOption(
            String value,
            Map<String, String> options,
            String fieldName,
            boolean allowInitialization) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
        if (allowInitialization) {
            return trimmed;
        }
        String standardValue = options.get(normalize(trimmed));
        BusinessAssert.isTrue(
                standardValue != null,
                CommonErrorCode.PARAM_INVALID,
                fieldName + "必须从已有选项中选择");
        return standardValue;
    }

    private static List<String> normalizeTagOptions(
            String rawTags,
            Map<String, String> options,
            boolean allowInitialization) {
        List<String> requestedTags = splitTags(rawTags);
        if (requestedTags.isEmpty()) {
            return List.of();
        }
        if (allowInitialization) {
            return requestedTags;
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String requestedTag : requestedTags) {
            String standardValue = options.get(normalize(requestedTag));
            BusinessAssert.isTrue(
                    standardValue != null,
                    CommonErrorCode.PARAM_INVALID,
                    "标签必须从已有选项中选择：" + requestedTag);
            normalized.add(standardValue);
        }
        return List.copyOf(normalized);
    }

    private static String normalize(String value) {
        return StrUtil.emptyIfNull(value).trim().toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");
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
            WarframeForumArticleSaveRequest request,
            ForumMetadata metadata) {
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setTags(metadata.tags());
        article.setCategory(metadata.category());
        article.setCoverUrl(request.coverUrl());
        article.setContentHtml(request.contentHtml());
        article.setContentMarkdown(request.contentMarkdown());
        article.setEnabled(request.enabled());
        article.setSortOrder(request.sortOrder());
    }

    private static void apply(
            WarframeForumArticle article,
            WarframeForumArticleUpdateRequest request,
            ForumMetadata metadata) {
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setTags(metadata.tags());
        article.setCategory(metadata.category());
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

    private record ScoredArticle(WarframeForumArticle article, int score) {
    }

    private record ForumMetadata(String category, String tags) {
    }
}
