package com.nnm.bot.warframe.persistent.official.pojo.dto.support;

import java.util.Map;

/** 单张官方关系表的标量投影，不进入 Hibernate 持久化上下文。 */
public record OfficialRelationRowDTO(Map<String, Object> values) {

    /**
     * 读取指定实体字段的投影值。
     *
     * @param property 生成模型中的 Java 属性名。
     * @return 当前字段对应的非受管标量值。
     */
    public Object get(String property) {
        return values.get(property);
    }

    /**
     * 读取关系父记录标识；主表的 bkey 是字符串业务键，因此返回空值。
     *
     * @return 子表直接父记录的本地主键。
     */
    public Long parentId() {
        Object value = get("bkey");
        return value instanceof Number number ? number.longValue() : null;
    }
}
