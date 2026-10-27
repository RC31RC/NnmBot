package com.nnm.bot.warframe.persistent.official.mapper.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO.FactVO;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialObjectFields;
import java.util.ArrayList;
import java.util.List;

/** 通用资料命令根据已声明的具体字段展示标量事实，不进行业务字段解析。 */
public final class OfficialFactMapper {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private OfficialFactMapper() { }

    /**
     * 数组和对象由各自业务展示模型负责，通用列表仅枚举有值的具体标量字段。
     *
     * @param value 待处理的字段值。
     * @param identity 用于定位本次请求的业务身份。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<FactVO> facts(OfficialObjectDTO value, String identity) {
        List<FactVO> result = new ArrayList<>();
        if (value == null) return result;
        for (var field : OfficialObjectFields.fields(value.getClass())) {
            String key = field.getAnnotation(JsonProperty.class).value();
            Object item = OfficialObjectFields.get(value, key);
            if (item instanceof String || item instanceof Number || item instanceof Boolean) {
                result.add(new FactVO(key, identity, item.toString()));
            }
        }
        return List.copyOf(result);
    }
}
