package com.nnm.bot.warframe.runtime.pojo.dto.market;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Warframe Market 订单中返回的用户概要。
 *
 * <p>用户状态和最后在线时间属于订单查询时的动态信息，不能从本地物品
 * 数据推断，因此与物品数据分开建模。</p>
 */
public record WarframeMarketUserDTO(
        String id,
        String ingameName,
        String slug,
        Integer reputation,
        String platform,
        boolean crossplay,
        String locale,
        String status,
        String avatar,
        String lastSeen,
        Map<String, Object> activity,
        Map<String, Object> rawFields) {

    /**
     * 构造市场用户传输对象，并保存初始化参数。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param ingameName 游戏内名称。
     * @param slug 市场物品的地址标识。
     * @param reputation 信誉。
     * @param platform 市场或官方接口使用的平台代码。
     * @param crossplay 跨平台。
     * @param locale 当前语言区域代码。
     * @param status 状态。
     * @param avatar 外观。
     * @param lastSeen 末尾出现时间。
     * @param activity 活动。
     * @param rawFields 原始字段。
     */
    public WarframeMarketUserDTO {
        id = trim(id);
        ingameName = trim(ingameName);
        slug = trim(slug);
        platform = trim(platform);
        locale = trim(locale);
        status = trim(status);
        avatar = trim(avatar);
        lastSeen = trim(lastSeen);
        activity = immutable(activity);
        rawFields = immutable(rawFields);
    }

    /**
     * 构造没有有效业务内容的缺省对象。
     *
     * @return 游戏资料市场用户对象。
     */
    public static WarframeMarketUserDTO empty() {
        return new WarframeMarketUserDTO("", "", "", null, "", false, "", "", "", "", Map.of(), Map.of());
    }

    /**
     * 判断当前数据是否提供了可用的业务身份。
     *
     * @return 满足当前数据是否提供了可用的业务身份时为真，否则为假。
     */
    public boolean present() {
        return StrUtil.isNotBlank(id) || StrUtil.isNotBlank(ingameName);
    }

    /**
     * 裁剪首尾空白并统一缺省文本。
     *
     * @param value 待校验或规范化的文本。
     * @return 去除首尾空白的文本；输入缺省时返回空文本。
     */
    private static String trim(String value) {
        return StrUtil.emptyIfNull(value).trim();
    }

    /**
     * 复制当前集合，防止调用方修改共享数据。
     *
     * @param values 待处理的值集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> immutable(Map<String, Object> values) {
        return CollUtil.isEmpty(values)
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }
}
