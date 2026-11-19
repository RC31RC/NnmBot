package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 将具体 Q 类型的标量字段装配为非受管关系行 DTO。 */
public final class OfficialRelationRowProjection extends FactoryExpressionBase<OfficialRelationRowDTO> {
    private final List<String> properties;
    private final List<Expression<?>> expressions;

    /**
     * 构造字段顺序固定的关系投影。
     *
     * @param properties 对应表达式的实体属性名。
     * @param expressions 由具体 Q 类型提供的标量表达式。
     */
    public OfficialRelationRowProjection(List<String> properties, List<Expression<?>> expressions) {
        super(OfficialRelationRowDTO.class);
        if (properties.size() != expressions.size()) {
            throw new IllegalArgumentException("Official relation projection fields do not match expressions");
        }
        if (expressions.getFirst() instanceof Path<?> fieldPath) {
            Path<?> entityPath = fieldPath.getRoot();
            // 重建关联树时，映射形式的子行必须保留原始来源键。
            if (OfficialChildRow.class.isAssignableFrom(entityPath.getType()) && !properties.contains("memberKey")) {
                ArrayList<String> projectedProperties = new ArrayList<>(properties);
                ArrayList<Expression<?>> projectedExpressions = new ArrayList<>(expressions);
                projectedProperties.add("memberKey");
                projectedExpressions.add(Expressions.stringPath(entityPath, "memberKey"));
                this.properties = List.copyOf(projectedProperties);
                this.expressions = List.copyOf(projectedExpressions);
                return;
            }
        }
        this.properties = List.copyOf(properties);
        this.expressions = List.copyOf(expressions);
    }

    /**
     * 返回构造 DTO 所需的标量表达式。
     *
     * @return 按投影属性顺序排列的 QueryDSL 表达式。
     */
    @Override
    public List<Expression<?>> getArgs() {
        return expressions;
    }

    /**
     * 按已声明的属性顺序将查询结果组装为非受管关系 DTO。
     *
     * @param values QueryDSL 返回的字段值。
     * @return 保存字段映射的关系行 DTO。
     */
    @Override
    public OfficialRelationRowDTO newInstance(Object... values) {
        Map<String, Object> projected = new LinkedHashMap<>();
        for (int index = 0; index < properties.size(); index++) {
            projected.put(properties.get(index), values[index]);
        }
        return new OfficialRelationRowDTO(Collections.unmodifiableMap(projected));
    }

    /**
     * 将此投影交给 QueryDSL 访问器。
     *
     * @param visitor QueryDSL 表达式访问器。
     * @param context 访问器调用上下文。
     * @param <R> 访问结果类型。
     * @param <C> 访问上下文类型。
     * @return 访问器生成的结果。
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
