package com.nnm.bot.warframe.persistent.curated.repository.reference;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Locale;

/** 人工资料单表关键词筛选，支持大小写无关的文本包含查询并转义数据库通配符。 */
public final class CommunitySpecifications {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private CommunitySpecifications() {}

    /**
     * 将关键词转换为多个明确文本字段的大小写无关包含筛选，并转义数据库通配符。
     *
     * @param keyword 筛选使用的关键词。
     * @param fields 实体中的文本字段名。
     * @return 筛选规格对象。
     */
    public static <T> Specification<T> contains(String keyword, List<String> fields) {
        return (root, query, builder) -> {
            if (CharSequenceUtil.isBlank(keyword))
                return builder.conjunction();
            String pattern = "%" + keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            String normalizedPattern = pattern.toLowerCase(Locale.ROOT);
            return builder.or(fields.stream()
                .map(field -> builder.like(builder.lower(root.get(field).as(String.class)), normalizedPattern, '\\'))
                .toArray(Predicate[]::new));
        };
    }
}
