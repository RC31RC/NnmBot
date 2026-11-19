package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataErrorCode;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import java.io.IOException;

/** JSON 到非受管输入 DTO 的唯一转换，不把外部输入直接绑定到 JPA Entity。 */
public final class WarframeOfficialInputParser {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeOfficialInputParser() {}

    /**
     * 解析完整的一条官方记录，拒绝未知字段及记录之后的多余 JSON。
     *
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @param domain 官方数据文件所属的业务领域。
     * @param input 待解析或校验的输入。
     * @return 官方记录对象。
     */
    public static OfficialRecordDTO parseCompleteJson(ObjectMapper mapper, WarframeOfficialDataDomain domain,
        String input) {
        BusinessAssert.isTrue(domain != null && domain != WarframeOfficialDataDomain.OTHER,
            OfficialDataErrorCode.INPUT_TYPE_UNREGISTERED);
        try {
            OfficialRecordDTO result = mapper.readerFor(OfficialInputTypes.type(domain))
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).readValue(input);
            BusinessAssert.isTrue(result != null, OfficialDataErrorCode.RECORD_EMPTY);
            return result;
        } catch (IOException exception) {
            throw invalid(exception);
        }
    }

    /**
     * 仅绑定流式解析器当前值，保留同一数组或对象内的后续记录供调用方继续读取。
     *
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @param domain 官方数据文件所属的业务领域。
     * @param parser 已定位到当前输入值的流式解析器。
     * @return 官方记录对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public static OfficialRecordDTO parseCurrentValue(ObjectMapper mapper, WarframeOfficialDataDomain domain,
        JsonParser parser) throws IOException {
        BusinessAssert.isTrue(domain != null && domain != WarframeOfficialDataDomain.OTHER,
            OfficialDataErrorCode.INPUT_TYPE_UNREGISTERED);
        try {
            OfficialRecordDTO result = mapper.readerFor(OfficialInputTypes.type(domain))
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).readValue(parser);
            BusinessAssert.isTrue(result != null, OfficialDataErrorCode.RECORD_EMPTY);
            return result;
        } catch (JsonMappingException exception) {
            throw invalid(exception);
        }
    }

    /**
     * 将绑定失败转换为安全的业务异常，字段路径保留在 Jackson cause 供内部诊断。
     *
     * @param exception 带字段路径的原始绑定异常。
     * @return 保留原始失败原因但不回显 JSON 内容的业务异常。
     */
    private static BusinessException invalid(Exception exception) {
        return new BusinessException(OfficialDataErrorCode.RECORD_JSON_INVALID, exception);
    }

}
