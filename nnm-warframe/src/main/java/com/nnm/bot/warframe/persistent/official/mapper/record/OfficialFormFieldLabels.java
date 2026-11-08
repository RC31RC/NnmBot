package com.nnm.bot.warframe.persistent.official.mapper.record;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.persistent.official.constant.OfficialFormErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/** 表单字段中文释义由官方 JSON 类型合同生成，与 DTO 共享同一来源。 */
public final class OfficialFormFieldLabels {
    private static final Map<String, Map<String, String>> LABELS = readLabels();

    private OfficialFormFieldLabels() {}

    /**
     * 返回已建模字段在指定官方文件中的中文含义。
     *
     * @param domain 官方数据领域编码。
     * @param path 字段路径，数组和字典分别使用 * 与 {key}。
     * @return 已登记的中文字段含义。
     */
    public static String label(String domain, String path) {
        String result = LABELS.getOrDefault(domain, Map.of()).get(path);
        if (result == null)
            throw new SystemException(OfficialFormErrorCode.LABELS_UNAVAILABLE, "缺少官方表单字段释义：" + domain + "/" + path);
        return result;
    }

    /**
     * 从打包资源读取与官方输入合同同步生成的字段中文释义。
     *
     * @return 按领域及路径索引的已建模字段释义。
     */
    private static Map<String, Map<String, String>> readLabels() {
        try (InputStream input =
            OfficialFormFieldLabels.class.getResourceAsStream("/warframe/official-form-labels.json")) {
            if (input == null)
                throw new SystemException(OfficialFormErrorCode.LABELS_UNAVAILABLE, "缺少官方表单字段释义资源");
            return new ObjectMapper().readValue(input, new TypeReference<>() {});
        } catch (IOException exception) {
            throw new SystemException(OfficialFormErrorCode.LABELS_UNAVAILABLE, "无法读取官方表单字段释义", exception);
        }
    }
}
