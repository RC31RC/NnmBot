package com.nnm.bot.warframe.persistent.official.pojo.dto.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialObjectSerializer;
import java.util.HashSet;
import java.util.Set;

/** 官方固定字段对象的输入状态；仅记录字段是否出现，不存放任意 JSON 载荷。 */
@JsonSerialize(using = OfficialObjectSerializer.class)
public abstract class OfficialObjectDTO {
    /** 本次绑定显式提供的字段，用于区分缺失字段与 null。 */
    @JsonIgnore
    private final Set<String> presentFields = new HashSet<>();

    /**
     * 由具体字段的 Jackson setter 登记输入状态。
     *
     * @param field 已经声明的字段或字段标识。
     */
    public final void markPresent(String field) {
        presentFields.add(field);
    }

    /**
     * 查询固定字段是否由上游显式提供。
     *
     * @param field 已经声明的字段或字段标识。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public final boolean isPresent(String field) {
        return presentFields.contains(field);
    }
}
