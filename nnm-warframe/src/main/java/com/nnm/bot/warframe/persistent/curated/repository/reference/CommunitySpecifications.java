package com.nnm.bot.warframe.persistent.curated.repository.reference;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

/** 人工资料单表关键词筛选，转义通配符并使用 DTO 投影。 */
final class CommunitySpecifications {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private CommunitySpecifications() {
    }

    /**
     * 将关键词转换为多个明确文本字段的包含筛选，并转义数据库通配符。
     *
     * @param keyword 筛选使用的关键词。
     * @param fields 字段集合。
     * @return 筛选规格对象。
     */
    static <T> Specification<T> contains(String keyword, List<String> fields) {
        return (root, query, builder) -> {
            if (StrUtil.isBlank(keyword)) return builder.conjunction();
            String pattern = "%" + keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            return builder.or(fields.stream().map(field -> builder.like(root.get(field), pattern, '\\'))
                    .toArray(Predicate[]::new));
        };
    }
}
