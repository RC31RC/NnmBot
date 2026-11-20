package com.nnm.bot.warframe.persistent.official.parser;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 外部引用的候选身份；候选必须命中实际记录才算解析成功。 */
public final class WarframeOfficialReferenceKeys {
    private static final String STORE_PREFIX = "/Lotus/StoreItems/";
    private static final String ITEM_PREFIX = "/Lotus/";

    /** 只提供无状态的引用身份转换。 */
    private WarframeOfficialReferenceKeys() {}

    /**
     * 保留原始身份，并为商店物品引用提供对应游戏物品候选。
     *
     * @param reference 上游引用，不修改调用方原文。
     * @return 有序且去重的候选；不截断路径，不进行模糊匹配。
     */
    public static List<String> candidates(String reference) {
        if (reference == null || CharSequenceUtil.isBlank(reference))
            return List.of();
        String key = CharSequenceUtil.trim(reference);
        return key.startsWith(STORE_PREFIX) ? List.of(key, ITEM_PREFIX + key.substring(STORE_PREFIX.length()))
            : List.of(key);
    }

    /**
     * 为合同明确声明的分组记录增加组内身份，普通业务路径保持完整。
     *
     * @param domain 记录所属领域。
     * @param bkey 当前主表保存的完整业务身份。
     * @return 完整身份及合同允许的组内身份。
     */
    public static List<String> identities(WarframeOfficialDataDomain domain, String bkey) {
        LinkedHashSet<String> keys = new LinkedHashSet<>(candidates(bkey));
        for (String group : OfficialDataConstants.RECORD_GROUPS.getOrDefault(domain, Set.of())) {
            String prefix = group + "/";
            if (bkey.startsWith(prefix))
                keys.addAll(candidates(bkey.substring(prefix.length())));
        }
        return List.copyOf(keys);
    }
}
