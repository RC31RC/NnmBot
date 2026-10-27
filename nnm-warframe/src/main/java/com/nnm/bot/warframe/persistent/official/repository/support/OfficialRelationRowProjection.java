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
            // Child-row map values need their original source key when the relation tree is rebuilt.
            if (OfficialChildRow.class.isAssignableFrom(entityPath.getType())
                    && !properties.contains("memberKey")) {
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

    @Override
    public List<Expression<?>> getArgs() {
        return expressions;
    }

    @Override
    public OfficialRelationRowDTO newInstance(Object... values) {
        Map<String, Object> projected = new LinkedHashMap<>();
        for (int index = 0; index < properties.size(); index++) {
            projected.put(properties.get(index), values[index]);
        }
        return new OfficialRelationRowDTO(Collections.unmodifiableMap(projected));
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
