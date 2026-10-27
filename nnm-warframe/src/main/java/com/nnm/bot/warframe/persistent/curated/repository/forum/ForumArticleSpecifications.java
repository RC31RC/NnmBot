package com.nnm.bot.warframe.persistent.curated.repository.forum;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.forum.ForumArticle;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class ForumArticleSpecifications {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private ForumArticleSpecifications() {
    }

    /**
     * 根据文章查询参数组合状态、栏目、标签和关键词筛选条件。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 筛选规格对象。
     */
    static Specification<ForumArticle> specification(
            ForumArticleQuery request) {
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
                        likePattern("," + tag.trim().toLowerCase(Locale.ROOT).replace(",", "").replace("，", "").replace("、", "") + ",")));
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

    /**
     * 转义关键词中的数据库通配符，并构造包含匹配模式。
     *
     * @param value 待校验或规范化的文本。
     * @return 已转义数据库通配符的包含匹配模式。
     */
    private static String likePattern(String value) {
        String escaped = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    /**
     * 将查询标签规范化为去重的有效标签集合。
     *
     * @param tags 标签。
     * @param criteriaBuilder 筛选条件构建器。
     * @return 表达式对象。
     */
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

}
