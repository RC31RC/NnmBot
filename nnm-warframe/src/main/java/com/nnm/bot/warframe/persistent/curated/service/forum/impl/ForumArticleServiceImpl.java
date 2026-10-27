package com.nnm.bot.warframe.persistent.curated.service.forum.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumPreparedContentDTO;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.config.forum.ForumProperties;
import com.nnm.bot.warframe.persistent.curated.constant.forum.ForumErrorCode;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.forum.ForumArticle;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleContentDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleIdQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleDeleteDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleOptionsVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticlePublicVO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleSaveDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleSearchQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumOperatorDTO;
import com.nnm.bot.warframe.persistent.curated.repository.forum.ForumArticleRepository;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumArticleService;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumIdService;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumUserAuthorityProvider;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ForumArticleServiceImpl implements ForumArticleService {

    private static final int MAX_PAGE_SIZE = 100;

    @Resource
    private ForumArticleRepository repository;

    @Resource
    private ForumFileService fileService;

    @Resource
    private ForumUserAuthorityProvider authorityProvider;

    @Resource
    private ObjectProvider<ForumIdService> distributedIds;

    @Resource
    private ForumProperties forumProperties;

    /**
     * 按查询条件筛选论坛文章并返回分页结果。
     *
     * @param request 本次查询的筛选与分页参数。
     * @param operator 操作者。
     * @return 包含当前页记录及总数的分页结果。
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<ForumArticleVO> query(
            ForumArticleQuery request,
            ForumOperatorDTO operator) {
        int current = normalizeCurrent(request.current());
        int size = normalizeSize(request.size());
        PageRequest pageable = PageRequest.of(
                current - 1,
                size,
                Sort.by(
                        Sort.Order.asc("sortOrder"),
                        Sort.Order.desc("updatedAt"),
                        Sort.Order.asc("id")));
        Page<ForumArticleDTO> page = repository.query(request, pageable);
        List<ForumArticleVO> records = toItems(page.getContent(), operator);
        return PageResult.of(records, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    /**
     * 取得当前业务可选项。
     *
     * @return 文章可选栏目、标签及当前用户的论坛权限。
     */
    @Override
    @Transactional(readOnly = true)
    public ForumArticleOptionsVO options() {
        List<ForumArticleDTO> articles = repository.findAllProjectedBy();
        Set<String> categories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (ForumArticleDTO article : articles) {
            if (StrUtil.isNotBlank(article.getCategory())) {
                categories.add(article.getCategory().trim());
            }
            splitTags(article.getTags()).forEach(tags::add);
        }
        return new ForumArticleOptionsVO(
                List.copyOf(categories),
                List.copyOf(tags),
                CollUtil.isEmpty(articles));
    }

    /**
     * 在已启用文章中按关键词搜索，并按相关度返回展示条目。
     *
     * @param query 当前业务的查询条件。
     * @return 文章详情及当前用户可用的操作组成的列表。
     */
    @Override
    @Transactional(readOnly = true)
    public List<ForumArticleVO> searchEnabled(ForumArticleSearchQuery query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return repository.findAllProjectedByEnabledTrue().stream()
                .map(article -> new ScoredArticle(article, score(article, query)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator
                        .comparingInt(ScoredArticle::score).reversed()
                        .thenComparing(scored -> scored.article().getSortOrder())
                        .thenComparing(scored -> scored.article().getId()))
                .limit(5)
                .map(ScoredArticle::article)
                .map(article -> toItem(article, null, Integer.MAX_VALUE))
                .toList();
    }

    /**
     * 读取指定论坛文章的完整详情。
     *
     * @param request 本次查询的筛选与分页参数。
     * @param operator 操作者。
     * @return 文章详情及当前用户可用的操作。
     */
    @Override
    @Transactional(readOnly = true)
    public ForumArticleVO detail(
            ForumArticleIdQuery request,
            ForumOperatorDTO operator) {
        ForumArticleDTO article = findData(request.id());
        return toItem(article, operator, assignedLevel(article.getCreatedByUserId()));
    }

    /**
     * 读取已发布文章的公开详情。
     *
     * @param id 目标记录的本地主键。
     * @return 已发布文章的公开展示内容。
     */
    @Override
    @Transactional(readOnly = true)
    public ForumArticlePublicVO publicDetail(long id) {
        ForumArticleDTO article = findData(id);
        BusinessAssert.isTrue(
                article.isEnabled(),
                CommonErrorCode.DATA_NOT_FOUND,
                "论坛文章不存在");
        ForumPreparedContentDTO content = publicContent(article);
        return ForumArticlePublicVO.from(article, content);
    }

    /**
     * 校验输入并创建论坛文章。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param operator 操作者。
     * @return 当前操作计算或读取的数值。
     */
    @Override
    @Transactional
    public long create(
            ForumArticleSaveDTO request,
            ForumOperatorDTO operator) {
        String slug = requiredTrimmed(request.slug(), "文章标识不能为空");
        ensureSlugAvailable(slug, null);
        ForumMetadata metadata = normalizeMetadata(request.category(), request.tags());
        ForumIdService ids = distributedIds.getIfAvailable();
        if (ids == null) {
            throw new IllegalStateException("论坛文章写入需要 RDT 分布式 ID 服务");
        }
        // 在提升附件前完成 ID 分配；RDT 不可用时不切换算法，也不产生对象存储副作用。
        long articleId = ids.next();
        ForumPreparedContentDTO content =
                fileService.promoteReferencedFiles(
                        request.coverUrl(),
                        request.contentHtml(),
                        request.contentMarkdown());
        ForumArticle article = new ForumArticle();
        article.setId(articleId);
        article.setPageUrl(forumProperties.publicArticleUri(articleId));
        apply(article, request, metadata);
        applyPreparedContent(article, storageContent(content));
        article.setSlug(slug);
        article.setCreatedByUserId(operator.userId());
        return save(article).getId();
    }

    /**
     * 校验权限与输入后更新论坛文章。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param operator 操作者。
     */
    @Override
    @Transactional
    public void update(
            ForumArticleSaveDTO request,
            ForumOperatorDTO operator) {
        BusinessAssert.isTrue(request.id() != null && request.id() > 0,
                CommonErrorCode.PARAM_INVALID, "修改或删除文章必须提供有效 id");
        ForumArticle article = findArticle(request.id());
        checkCanUpdate(article, operator);
        String slug = requiredTrimmed(request.slug(), "文章标识不能为空");
        ensureSlugAvailable(slug, request.id());
        ForumMetadata metadata = normalizeMetadata(request.category(), request.tags());
        ForumPreparedContentDTO content =
                fileService.promoteReferencedFiles(
                        request.coverUrl(),
                        request.contentHtml(),
                request.contentMarkdown());
        apply(article, request, metadata);
        applyPreparedContent(article, storageContent(content));
        article.setSlug(slug);
        article.setPageUrl(forumProperties.publicArticleUri(article.getId()));
        save(article);
    }

    /**
     * 校验权限后删除论坛文章，并维护关联资源。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param operator 操作者。
     */
    @Override
    @Transactional
    public void delete(
            ForumArticleDeleteDTO request,
            ForumOperatorDTO operator) {
        BusinessAssert.isTrue(request.id() != null && request.id() > 0,
                CommonErrorCode.PARAM_INVALID, "修改或删除文章必须提供有效 id");
        ForumArticle article = findArticle(request.id());
        checkCanDelete(article, operator);
        Set<String> filesToDelete = new LinkedHashSet<>(fileService.referencedPermanentFiles(
                article.getCoverObjectId(),
                article.getContentHtml(),
                article.getContentMarkdown()));
        if (CollUtil.isNotEmpty(filesToDelete)) {
            for (ForumArticleDTO otherArticle : repository.findAllProjectedByIdNot(article.getId())) {
                filesToDelete.removeAll(fileService.referencedPermanentFiles(
                        otherArticle.getCoverObjectId(),
                        otherArticle.getContentHtml(),
                        otherArticle.getContentMarkdown()));
                if (CollUtil.isEmpty(filesToDelete)) {
                    break;
                }
            }
        }
        repository.delete(article);
        repository.flush();
        fileService.deletePermanentFilesAfterCommit(filesToDelete);
    }

    /**
     * 将业务记录列表转换为当前页面需要的展示条目。
     *
     * @param articles 文章集合。
     * @param operator 操作者。
     * @return 文章详情及当前用户可用的操作组成的列表。
     */
    private List<ForumArticleVO> toItems(
            Collection<? extends ForumArticleContentDTO> articles,
            ForumOperatorDTO operator) {
        Set<Long> ownerIds = articles.stream()
                .map(ForumArticleContentDTO::getCreatedByUserId)
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

    /**
     * 将单条业务记录转换为当前页面需要的展示条目。
     *
     * @param article 文章。
     * @param operator 操作者。
     * @param ownerAuthorityLevel 所属人权限等级。
     * @return 文章详情及当前用户可用的操作。
     */
    private ForumArticleVO toItem(
            ForumArticleContentDTO article,
            ForumOperatorDTO operator,
            int ownerAuthorityLevel) {
        ForumPreparedContentDTO content = publicContent(article);
        return ForumArticleVO.from(article, operator, ownerAuthorityLevel, content);
    }

    /**
     * 将文章正文转换为持久化使用的稳定文件引用形式。
     *
     * @param content 内容。
     * @return 论坛已准备内容对象。
     */
    private ForumPreparedContentDTO storageContent(
            ForumPreparedContentDTO content) {
        ForumPreparedContentDTO storage = fileService.toStorageContent(content);
        return storage == null ? content : storage;
    }

    /**
     * 将已保存的文章正文转换为包含公开文件地址的展示内容。
     *
     * @param article 文章。
     * @return 论坛已准备内容对象。
     */
    private ForumPreparedContentDTO publicContent(ForumArticleContentDTO article) {
        ForumPreparedContentDTO content = fileService.toPublicContent(
                article.getCoverObjectId(), article.getContentHtml(), article.getContentMarkdown());
        return content == null
                ? new ForumPreparedContentDTO(
                        article.getCoverObjectId(), article.getContentHtml(), article.getContentMarkdown())
                : content;
    }

    /**
     * 规范化标签。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化标签对应的文本。
     */
    private static String normalizeTag(String value) {
        return normalize(value)
                .replace(",", "")
                .replace("，", "")
                .replace("、", "");
    }

    /**
     * 计算文章与查询关键词的匹配分值，供搜索排序使用。
     *
     * @param article 文章。
     * @param query 当前业务的查询条件。
     * @return 文章与查询条件的匹配分值；不匹配时为零。
     */
    private static int score(
            ForumArticleContentDTO article,
            ForumArticleSearchQuery query) {
        String title = normalize(article.getTitle());
        String slug = normalize(article.getSlug());
        String category = normalize(article.getCategory());
        String summary = normalize(article.getSummary());
        List<String> tags = splitTags(article.getTags()).stream()
                .map(ForumArticleServiceImpl::normalize)
                .toList();

        int score = 0;
        if (StrUtil.isNotBlank(query.title())) {
            String requested = normalize(query.title());
            if (title.equals(requested)) {
                score += 100;
            } else if (title.contains(requested)) {
                score += 60;
            } else {
                return 0;
            }
        }
        if (StrUtil.isNotBlank(query.category())) {
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

    /**
     * 拆分并规范化文章标签，去除空白和重复项。
     *
     * @param tags 标签。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> splitTags(String tags) {
        if (StrUtil.isBlank(tags)) {
            return List.of();
        }
        return Arrays.stream(tags.split("[,，、\\s]+"))
                .map(String::trim)
                .filter(value -> StrUtil.isNotBlank(value))
                .distinct()
                .toList();
    }

    /**
     * 将分类和标签归一化为数据库中的标准值。
     *
     *   <p>选项来自已有文章，因此管理端即使绕过下拉框直接调用接口，也不能写入
     *   一套新的分类或近似标签。数据库为空时保留初始化能力，允许第一篇文章建立
     *   初始分类和标签选项。</p>
     *
     * @param category 分类。
     * @param tags 标签。
     * @return 论坛元数据对象。
     */
    private ForumMetadata normalizeMetadata(String category, String tags) {
        List<ForumArticleDTO> existingArticles = repository.findAllProjectedBy();
        boolean allowInitialization = CollUtil.isEmpty(existingArticles);
        Map<String, String> categoryOptions = existingArticles.stream()
                .map(ForumArticleContentDTO::getCategory)
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
                CollUtil.isEmpty(normalizedTags) ? null : String.join(", ", normalizedTags));
    }

    /**
     * 规范化单个栏目或标签选项，拒绝无效值。
     *
     * @param value 待校验或规范化的文本。
     * @param options 选项。
     * @param fieldName 字段名称。
     * @param allowInitialization 允许初始化。
     * @return 规范化选项对应的文本。
     */
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

    /**
     * 规范化文章标签选项，并保持稳定的去重顺序。
     *
     * @param rawTags 原始标签。
     * @param options 选项。
     * @param allowInitialization 允许初始化。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> normalizeTagOptions(
            String rawTags,
            Map<String, String> options,
            boolean allowInitialization) {
        List<String> requestedTags = splitTags(rawTags);
        if (CollUtil.isEmpty(requestedTags)) {
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

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return StrUtil.emptyIfNull(value).trim().toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");
    }

    /**
     * 按文章主键读取非受管投影，不存在时直接报错。
     *
     * @param id 目标记录的本地主键。
     * @return 论坛文章对象。
     */
    private ForumArticleDTO findData(long id) {
        return repository.findProjectedById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "论坛文章不存在"));
    }

    /**
     * 按文章主键读取本次写操作使用的实体，不存在时直接报错。
     *
     * @param id 目标记录的本地主键。
     * @return 论坛文章对象。
     */
    private ForumArticle findArticle(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "论坛文章不存在"));
    }

    /**
     * 校验文章访问标识未被其他文章占用。
     *
     * @param slug 文章对外访问使用的唯一标识。
     * @param id 目标记录的本地主键。
     */
    private void ensureSlugAvailable(String slug, Long id) {
        boolean duplicated = id == null
                ? repository.existsBySlugIgnoreCase(slug)
                : repository.existsBySlugIgnoreCaseAndIdNot(slug, id);
        BusinessAssert.isFalse(duplicated, CommonErrorCode.DATA_CONFLICT, "论坛文章 slug 已存在");
    }

    /**
     * 校验当前用户有权修改目标文章。
     *
     * @param article 文章。
     * @param operator 操作者。
     */
    private void checkCanUpdate(
            ForumArticleContentDTO article,
            ForumOperatorDTO operator) {
        BusinessAssert.isTrue(
                operator.canUpdate(
                        article.getCreatedByUserId(),
                        assignedLevel(article.getCreatedByUserId())),
                ForumErrorCode.ARTICLE_OPERATION_DENIED);
    }

    /**
     * 校验当前用户有权删除目标文章。
     *
     * @param article 文章。
     * @param operator 操作者。
     */
    private void checkCanDelete(
            ForumArticleContentDTO article,
            ForumOperatorDTO operator) {
        BusinessAssert.isTrue(
                operator.canDelete(
                        article.getCreatedByUserId(),
                        assignedLevel(article.getCreatedByUserId())),
                ForumErrorCode.ARTICLE_OPERATION_DENIED);
    }

    /**
     * 取得当前用户在论坛中被授予的管理等级。
     *
     * @param userId 用户标识。
     * @return 当前操作计算或读取的数值。
     */
    private int assignedLevel(long userId) {
        return authorityProvider.assignedLevels(Set.of(userId))
                .getOrDefault(userId, Integer.MAX_VALUE);
    }

    /**
     * 显式保存当前业务记录。
     *
     * @param article 文章。
     * @return 论坛文章对象。
     */
    private ForumArticle save(ForumArticle article) {
        try {
            return repository.saveAndFlush(article);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(
                    CommonErrorCode.DATA_CONFLICT,
                    "论坛文章数据冲突",
                    exception);
        }
    }

    /**
     * 采纳已经确认的业务变更。
     *
     * @param article 文章。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @param metadata 元数据。
     */
    private static void apply(
            ForumArticle article,
            ForumArticleSaveDTO request,
            ForumMetadata metadata) {
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setTags(metadata.tags());
        article.setCategory(metadata.category());
        article.setCoverObjectId(request.coverUrl());
        article.setContentHtml(request.contentHtml());
        article.setContentMarkdown(request.contentMarkdown());
        article.setEnabled(request.enabled());
        article.setSortOrder(request.sortOrder());
    }

    /**
     * 将已经校验并完成文件处理的正文填入文章实体。
     *
     * @param article 文章。
     * @param content 内容。
     */
    private static void applyPreparedContent(
            ForumArticle article,
            ForumPreparedContentDTO content) {
        article.setCoverObjectId(content.coverUrl());
        article.setContentHtml(content.contentHtml());
        article.setContentMarkdown(content.contentMarkdown());
    }

    /**
     * 校验必需文本，并裁剪首尾空白。
     *
     * @param value 待校验或规范化的文本。
     * @param message 待发送或处理的消息。
     * @return 校验必需文本，并裁剪首尾空白对应的文本。
     */
    private static String requiredTrimmed(String value, String message) {
        return BusinessAssert.notBlank(value, CommonErrorCode.PARAM_INVALID, message).trim();
    }

    /**
     * 将输入页码规范化为允许的最小值及范围。
     *
     * @param current 从一开始的当前页码。
     * @return 不小于一的当前页码。
     */
    private static int normalizeCurrent(Integer current) {
        return current == null ? 1 : Math.max(current, 1);
    }

    /**
     * 将分页大小限制在配置允许的范围内。
     *
     * @param size 当前操作允许的数据数量。
     * @return 已限制在允许范围内的每页条目数。
     */
    private static int normalizeSize(Integer size) {
        return size == null ? 20 : Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    private record ScoredArticle(ForumArticleDTO article, int score) {
    }

    private record ForumMetadata(String category, String tags) {
    }
}
